package net.squareshaper.neostamina.mixin.entity;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.entity.StaminaUsingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements StaminaUsingEntity {

    @Shadow
    public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    public abstract void stopUsingItem();

    @Shadow
    protected ItemStack activeItemStack;

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract float getAbsorptionAmount();

    @Unique
    private int staminaTickTimer = 0;
    @Unique
    private int staminaSyncTimer = 0;
    @Unique
    private int staminaSyncTimerMax = 20;
    @Unique
    private int depletedStaminaRegenerationDelayTimer = 0;
    @Unique
    private int staminaRegenerationDelayTimer = 0;
    @Unique
    private boolean delayStaminaRegeneration = false;
    @Unique
    private Float oldStamina = null;
    @Unique
    private Float oldMaxStamina = null;
    @Unique
    private boolean applyOldStamina = true;
    @Unique
    private boolean applyMaxStamina = false;

    @Unique
    private static final TrackedData<Float> STAMINA = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
    @Unique
    private static final TrackedData<Float> MAX_STAMINA = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker", at = @At("RETURN"))
    protected void neostamina$initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(STAMINA, 20.0F);
        builder.add(MAX_STAMINA, 20.0F);
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void neostamina$createLivingAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.getReturnValue()
                .add(Neostamina.STAMINA_REGENERATION)
                .add(Neostamina.MIN_MAX_STAMINA)
                .add(Neostamina.MAX_STAMINA)
                .add(Neostamina.DEPLETED_STAMINA_REGENERATION_DELAY_THRESHOLD)
                .add(Neostamina.STAMINA_REGENERATION_DELAY_THRESHOLD)
                .add(Neostamina.STAMINA_TICK_THRESHOLD)
                .add(Neostamina.RESERVED_STAMINA)
                .add(Neostamina.ITEM_SINGLE_USE_STAMINA_COST)
                .add(Neostamina.ITEM_CONTINUOUS_USE_STAMINA_COST)
                .add(Neostamina.SPRINTING_TICK_STAMINA_COST)
                .add(Neostamina.WALKING_TICK_STAMINA_COST)
                .add(Neostamina.SWIMMING_TICK_STAMINA_COST)
                .add(Neostamina.WALKING_UNDERWATER_TICK_STAMINA_COST)
                .add(Neostamina.WALKING_IN_WATER_TICK_STAMINA_COST)
                .add(Neostamina.CLIMBING_TICK_STAMINA_COST)
                .add(Neostamina.MINING_TICK_STAMINA_COST)
                .add(Neostamina.JUMPING_ACTION_STAMINA_COST)
                .add(Neostamina.SPRINT_JUMPING_ACTION_STAMINA_COST)
                .add(Neostamina.ATTACKING_ACTION_STAMINA_COST)
                .add(Neostamina.INTERACTION_ACTION_STAMINA_COST)
                .add(Neostamina.SHIELD_BLOCK_ACTION_STAMINA_COST)
        ;
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void neostamina$readCustomDataFromNbt_head(NbtCompound nbt, CallbackInfo ci) {
        float stamina;
        if (nbt.contains("stamina", NbtElement.NUMBER_TYPE)) {
            stamina = nbt.getFloat("stamina");
        } else {
            stamina = Float.MIN_VALUE;
        }
        if (stamina != Float.MIN_VALUE) {
            this.oldStamina = stamina;
        }

        float maxStamina;
        if (nbt.contains("max_stamina", NbtElement.NUMBER_TYPE)) {
            maxStamina = nbt.getFloat("max_stamina");
        } else {
            maxStamina = Float.MIN_VALUE;
        }
        if (maxStamina != Float.MIN_VALUE) {
            this.oldMaxStamina = maxStamina;
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void neostamina$readCustomDataFromNbt_tail(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("stamina", NbtElement.NUMBER_TYPE)) {
            this.neostamina$setStamina(nbt.getFloat("stamina"));
        }

        if (nbt.contains("max_stamina", NbtElement.NUMBER_TYPE)) {
            this.neostamina$setMaxStamina(nbt.getFloat("max_stamina"));
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void neostamina$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putFloat("max_stamina", this.neostamina$getMaxStamina());
        nbt.putFloat("stamina", this.neostamina$getStamina());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void neostamina$tick(CallbackInfo ci) {
        this.staminaSyncTimer++;
        if (this.staminaSyncTimer >= staminaSyncTimerMax) {
            this.neostamina$syncStaminaToHealth();
        }
        if (!this.getWorld().isClient) {

            this.staminaTickTimer++;

            if (this.neostamina$getStamina() <= 0 && this.delayStaminaRegeneration) {
                this.depletedStaminaRegenerationDelayTimer = 0;
                this.staminaRegenerationDelayTimer = this.neostamina$getStaminaRegenerationDelayThreshold();
                this.delayStaminaRegeneration = false;
            }
            if (this.neostamina$getStamina() > 0 && !this.delayStaminaRegeneration) {
                this.delayStaminaRegeneration = true;
            }
            if (this.depletedStaminaRegenerationDelayTimer <= this.neostamina$getDepletedStaminaRegenerationDelayThreshold()) {
                this.depletedStaminaRegenerationDelayTimer++;
            }
            if (this.staminaRegenerationDelayTimer <= this.neostamina$getStaminaRegenerationDelayThreshold()) {
                this.staminaRegenerationDelayTimer++;
            }

            if (
                    this.staminaTickTimer > this.neostamina$getStaminaTickThreshold()
                            && this.depletedStaminaRegenerationDelayTimer > this.neostamina$getDepletedStaminaRegenerationDelayThreshold()
                            && this.staminaRegenerationDelayTimer > this.neostamina$getStaminaRegenerationDelayThreshold()
            ) {
                if (this.neostamina$getStamina() < this.neostamina$getUnreservedStamina()) {
                    this.neostamina$addStamina(this.neostamina$getRegeneratedStamina(), false);
                }
                if (this.neostamina$getStamina() > this.neostamina$getUnreservedStamina() || this.neostamina$getRegeneratedStamina() < 0) {
                    this.neostamina$setStamina(this.neostamina$getUnreservedStamina());
                }
//                this.staminaTickTimer = 0; // commented out to make stamina regen smooth, per tick operation
            }

            if (this.isUsingItem() && this.activeItemStack.isIn(Neostamina.CONTINUOUS_USING_COSTS_STAMINA) && this.neostamina$getItemContinuousUseStaminaCost() > 0 && this.neostamina$getStamina() <= 0) {
                if (((LivingEntity) (Object) this) instanceof PlayerEntity playerEntity) {
                    playerEntity.getItemCooldownManager().set(this.activeItemStack.getItem(), Neostamina.SERVER_CONFIG.item_use_cooldown_when_no_stamina);
                }
                this.stopUsingItem();
            }
            if (this.applyOldStamina) {
                if (this.applyMaxStamina) {
                    this.oldStamina = this.neostamina$getHealthToStamina();
                    this.oldMaxStamina = this.neostamina$getHealthToStamina();
                    this.applyMaxStamina = false;
                }
                if (this.oldMaxStamina != null) {
                    this.neostamina$setMaxStamina(this.oldMaxStamina);
                    this.oldMaxStamina = null;
                }
                if (this.oldStamina != null) {
                    this.neostamina$setStamina(this.oldStamina);
                    this.oldStamina = null;
                }
            } else {
                this.applyOldStamina = true;
            }
        }
    }

    @Inject(method = "tickItemStackUsage", at = @At("HEAD"))
    protected void neostamina$tickItemStackUsage(ItemStack stack, CallbackInfo ci) {
        if (stack.isIn(Neostamina.CONTINUOUS_USING_COSTS_STAMINA) && neostamina$getItemContinuousUseStaminaCost() > 0 && neostamina$getStamina() > 0) {
            this.neostamina$addStamina(-neostamina$getItemContinuousUseStaminaCost(), true);
        }
    }

    @Override
    public int neostamina$getDepletedStaminaRegenerationDelayThreshold() {
        return (int) this.getAttributeValue(Neostamina.DEPLETED_STAMINA_REGENERATION_DELAY_THRESHOLD);
    }

    @Override
    public int neostamina$getStaminaRegenerationDelayThreshold() {
        return (int) this.getAttributeValue(Neostamina.STAMINA_REGENERATION_DELAY_THRESHOLD);
    }

    @Override
    public int neostamina$getStaminaTickThreshold() {
        return (int) this.getAttributeValue(Neostamina.STAMINA_TICK_THRESHOLD);
    }

    @Override
    public float neostamina$getRegeneratedStamina() {
        return this.neostamina$getStaminaRegeneration();
    }

    @Override
    public float neostamina$getStaminaRegeneration() {
        return (float) this.getAttributeValue(Neostamina.STAMINA_REGENERATION);
    }

    @Override
    public float neostamina$getUnreservedStamina() {
        return this.neostamina$getMaxStamina() - ((this.neostamina$getMaxStamina() * this.neostamina$getReservedStamina()) / 100);
    }

    @Override
    public float neostamina$getMaxStamina() {
        return this.dataTracker.get(MAX_STAMINA);
    }

    @Override
    public float neostamina$getMaxStaminaAttribute() {return (float) this.getAttributeValue(Neostamina.MAX_STAMINA);}

    @Override
    public float neostamina$getReservedStamina() {
        return (float) this.getAttributeValue(Neostamina.RESERVED_STAMINA);
    }

    @Override
    public float neostamina$getItemSingleUseStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.ITEM_SINGLE_USE_STAMINA_COST);
    }

    @Override
    public float neostamina$getItemContinuousUseStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.ITEM_CONTINUOUS_USE_STAMINA_COST);
    }

    @Override
    public float neostamina$getSprintingTickStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.SPRINTING_TICK_STAMINA_COST);
    }

    @Override
    public float neostamina$getWalkingTickStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.WALKING_TICK_STAMINA_COST);
    }

    @Override
    public float neostamina$getSwimmingTickStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.SWIMMING_TICK_STAMINA_COST);
    }

    @Override
    public float neostamina$getWalkingUnderwaterTickStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.WALKING_UNDERWATER_TICK_STAMINA_COST);
    }

    @Override
    public float neostamina$getWalkingInWaterTickStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.WALKING_IN_WATER_TICK_STAMINA_COST);
    }

    @Override
    public float neostamina$getClimbingTickStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.CLIMBING_TICK_STAMINA_COST);
    }

    @Override
    public float neostamina$getMiningTickStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.MINING_TICK_STAMINA_COST);
    }

    @Override
    public float neostamina$getJumpingActionStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.JUMPING_ACTION_STAMINA_COST);
    }

    @Override
    public float neostamina$getSprintJumpingActionStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.SPRINT_JUMPING_ACTION_STAMINA_COST);
    }

    @Override
    public float neostamina$getAttackActionStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.ATTACKING_ACTION_STAMINA_COST);
    }

    @Override
    public float neostamina$getInteractionActionStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.INTERACTION_ACTION_STAMINA_COST);
    }

    @Override
    public float neostamina$getShieldBlockActionStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.SHIELD_BLOCK_ACTION_STAMINA_COST);
    }

    @Override
    public void neostamina$addStamina(float amount, boolean shouldPauseRegeneration) {
        float f = this.neostamina$getStamina();
        this.neostamina$setStamina(f + amount);
        if (shouldPauseRegeneration) {
            this.staminaRegenerationDelayTimer = 0;
            this.staminaTickTimer = 0;
        }
    }

    @Override
    public float neostamina$getStamina() {
        return this.dataTracker.get(STAMINA);
    }

    @Override
    public void neostamina$setStamina(float stamina) {
        this.dataTracker.set(STAMINA, MathHelper.clamp(stamina, -0.1F, this.neostamina$getUnreservedStamina()));
    }

    @Override
    public void neostamina$setApplyOldStamina(boolean applyOldStamina) {
        this.applyOldStamina = applyOldStamina;
    }

    @Override
    public void neostamina$setApplyMaxStamina(boolean applyMaxStamina) {
        this.applyMaxStamina = applyMaxStamina;
    }

    @Override
    public void neostamina$setMaxStamina(float maxStamina) {
        this.dataTracker.set(MAX_STAMINA, MathHelper.clamp(maxStamina, this.neostamina$getMinMaxStamina(), maxStamina));
    }

    @Override
    public float neostamina$getMinMaxStamina() {
        return (float) this.getAttributeValue(Neostamina.MIN_MAX_STAMINA);
    }

    @Override
    public float neostamina$getHealthToStamina() {
        return (float) ((this.getHealth() + this.getAbsorptionAmount()) * (this.getAttributeValue(Neostamina.MAX_STAMINA) / 20));
    }

    @Override
    public void neostamina$syncStaminaToHealth() {
        this.neostamina$setMaxStamina(this.neostamina$getHealthToStamina());
        this.neostamina$setStamina(Math.min(this.neostamina$getStamina(), this.neostamina$getMaxStamina()));
    }

    @Inject(method = "damage", at = @At("TAIL"))
    private void neostamina$damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.neostamina$syncStaminaToHealth();
    }

    @Inject(method = "heal", at = @At("TAIL"))
    private void neostamina$heal(float amount, CallbackInfo ci) {
        this.neostamina$syncStaminaToHealth();
    }

    @Inject(method = "blockedByShield", at = @At("RETURN"))
    private void neostamina$block(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            this.neostamina$addStamina(-this.neostamina$getShieldBlockActionStaminaCost(), true);
        }
    }
}

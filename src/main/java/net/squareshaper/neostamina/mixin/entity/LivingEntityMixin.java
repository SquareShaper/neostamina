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
    public abstract boolean isSleeping();

    @Unique
    private int staminaTickTimer = 0;
    @Unique
    private int depletedStaminaRegenerationDelayTimer = 0;
    @Unique
    private int staminaRegenerationDelayTimer = 0;
    @Unique
    private boolean delayStaminaRegeneration = false;
    @Unique
    private Float oldStamina = null;
    @Unique
    private boolean applyOldStamina = true;
    @Unique
    private boolean applyMaxStamina = false;

    @Unique
    private static final TrackedData<Float> STAMINA = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
    @Unique
    private static final TrackedData<Boolean> BOOST_STAMINA = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker", at = @At("RETURN"))
    protected void neostamina$initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(STAMINA, 20.0F);
        builder.add(BOOST_STAMINA, true);
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void neostamina$createLivingAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.getReturnValue()
                .add(Neostamina.STAMINA_REGENERATION)
                .add(Neostamina.BASE_STAMINA)
                .add(Neostamina.BOOSTED_STAMINA)
                .add(Neostamina.MAX_STAMINA)
                .add(Neostamina.DEPLETED_STAMINA_REGENERATION_DELAY_THRESHOLD)
                .add(Neostamina.STAMINA_REGENERATION_DELAY_THRESHOLD)
                .add(Neostamina.STAMINA_TICK_THRESHOLD)
                .add(Neostamina.RESERVED_STAMINA)
                .add(Neostamina.ITEM_USE_STAMINA_COST)
                .add(Neostamina.SPRINTING_TICK_STAMINA_COST)
//                .add(Neostamina.SNEAKING_TICK_STAMINA_COST) don't need dis
                .add(Neostamina.WALKING_TICK_STAMINA_COST)
                .add(Neostamina.SWIMMING_TICK_STAMINA_COST)
                .add(Neostamina.WALKING_UNDERWATER_TICK_STAMINA_COST)
                .add(Neostamina.WALKING_IN_WATER_TICK_STAMINA_COST)
                .add(Neostamina.CLIMBING_TICK_STAMINA_COST)
                .add(Neostamina.JUMPING_ACTION_STAMINA_COST)
                .add(Neostamina.SPRINT_JUMPING_ACTION_STAMINA_COST)
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

        if (nbt.contains("boost_stamina", NbtElement.BYTE_TYPE)) {
            this.neostamina$setBoostStamina(nbt.getBoolean("boost_stamina"));
        } else {
            this.neostamina$setBoostStamina(false);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void neostamina$readCustomDataFromNbt_tail(NbtCompound nbt, CallbackInfo ci) {

        if (nbt.contains("stamina", NbtElement.NUMBER_TYPE)) {
            this.neostamina$setStamina(nbt.getFloat("stamina"));
        }

        if (nbt.contains("boost_stamina", NbtElement.BYTE_TYPE)) {
            this.neostamina$setBoostStamina(nbt.getBoolean("boost_stamina"));
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void neostamina$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {

        nbt.putFloat("stamina", this.neostamina$getStamina());

        nbt.putBoolean("boost_stamina", this.neostamina$getBoostStamina());

    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void neostamina$tick(CallbackInfo ci) {
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
                    this.neostamina$addStamina(this.neostamina$getRegeneratedStamina());
                }
                if (this.neostamina$getStamina() > this.neostamina$getUnreservedStamina() || this.neostamina$getRegeneratedStamina() < 0) {
                    this.neostamina$setStamina(this.neostamina$getUnreservedStamina());
                }
                this.staminaTickTimer = 0;
            }

            if (this.isUsingItem() && this.activeItemStack.isIn(Neostamina.CONTINUOUS_USING_COSTS_STAMINA) && this.neostamina$getItemUseStaminaCost() > 0 && this.neostamina$getStamina() <= 0) {
                if (((LivingEntity) (Object) this) instanceof PlayerEntity playerEntity) {
                    playerEntity.getItemCooldownManager().set(this.activeItemStack.getItem(), Neostamina.SERVER_CONFIG.item_use_cooldown_when_no_stamina);
                }
                this.stopUsingItem();
            }
            if (this.applyOldStamina) {
                if (this.applyMaxStamina) {
                    this.oldStamina = this.neostamina$getUnreservedStamina();
                    this.applyMaxStamina = false;
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
        if (stack.isIn(Neostamina.CONTINUOUS_USING_COSTS_STAMINA) && neostamina$getItemUseStaminaCost() > 0 && neostamina$getStamina() > 0) {
            this.neostamina$addStamina(-neostamina$getItemUseStaminaCost());
        }
    }

    @Inject(method = "onDamaged", at = @At("TAIL"))
    public void neostamina$onDamaged(DamageSource damageSource, CallbackInfo ci) {
        if (!this.getWorld().isClient()) {
            this.neostamina$setBoostStamina(false);
            this.neostamina$setStamina(MathHelper.clamp(this.neostamina$getStamina()-(this.neostamina$getBoostedStamina()-this.neostamina$getBaseStamina()), 0, this.neostamina$getBaseStamina()));
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
    public float neostamina$getBaseStamina() {
        return (float) this.getAttributeValue(Neostamina.BASE_STAMINA);
    }

    @Override
    public float neostamina$getBoostedStamina() {
        return (float) this.getAttributeValue(Neostamina.BOOSTED_STAMINA);
    }

    @Override
    public float neostamina$getMaxStamina() {
        return this.neostamina$getBoostStamina() ? this.neostamina$getBoostedStamina() : this.neostamina$getBaseStamina();
    }

    @Override
    public float neostamina$getReservedStamina() {
        return (float) this.getAttributeValue(Neostamina.RESERVED_STAMINA);
    }

    @Override
    public float neostamina$getItemUseStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.ITEM_USE_STAMINA_COST);
    }

    @Override
    public float neostamina$getSprintingTickStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.SPRINTING_TICK_STAMINA_COST);
    }

//    @Override
//    public float neostamina$getSneakingTickStaminaCost() {
//        return (float) this.getAttributeValue(Neostamina.SNEAKING_TICK_STAMINA_COST);
//    }

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
    public float neostamina$getJumpingActionStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.JUMPING_ACTION_STAMINA_COST);
    }

    @Override
    public float neostamina$getSprintJumpingActionStaminaCost() {
        return (float) this.getAttributeValue(Neostamina.SPRINT_JUMPING_ACTION_STAMINA_COST);
    }

    @Override
    public boolean neostamina$getBoostStamina() {
        return this.dataTracker.get(BOOST_STAMINA);
    }

    @Override
    public void neostamina$addStamina(float amount) {
        float f = this.neostamina$getStamina();
        this.neostamina$setStamina(f + amount);
        if (amount < 0) {
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
        this.dataTracker.set(STAMINA, MathHelper.clamp(stamina, -100, this.neostamina$getUnreservedStamina()));
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
    public void neostamina$setBoostStamina(boolean boostStamina) {
        this.dataTracker.set(BOOST_STAMINA, boostStamina);
    }
}

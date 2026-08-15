package net.squareshaper.neostamina.mixin.entity.player;

import com.google.common.collect.HashMultimap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.entity.StaminaUsingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements StaminaUsingEntity {
    @Shadow
    @Final
    private PlayerAbilities abilities;

    @Shadow
    public abstract boolean isInCreativeMode();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void neostamina$tick(CallbackInfo ci) {
        if (!this.getWorld().isClient()) {

            this.getAttributes().addTemporaryModifiers(getNaturalStaminaModifiers());
//            if (Neostamina.SERVER_CONFIG.players_can_exhaust) {
//                Optional<RegistryEntry.Reference<StatusEffect>> exhausted_status_effect = Registries.STATUS_EFFECT.getEntry(Neostamina.SERVER_CONFIG.exhausted_status_effect_identifier.get());
//                if (exhausted_status_effect.isPresent()) {
//                    if (this.neostamina$getStamina() <= 0) {
//                        if (!this.hasStatusEffect(exhausted_status_effect.get())) {
//                            this.addStatusEffect(new StatusEffectInstance(exhausted_status_effect.get(), -1, 0, false, false, true));
//                        }
//                    } else {
//                        this.removeStatusEffect(exhausted_status_effect.get());
//                    }
//                }
//            } // exhaust ain't implemented yet
        }
    }

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void neostamina$createPlayerAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.getReturnValue()
                .add(Neostamina.BASE_STAMINA, 0.0)
                .add(Neostamina.BOOSTED_STAMINA, 0.0)
                .add(Neostamina.MAX_STAMINA, 0.0)
                .add(Neostamina.MAX_STAMINA_CHANGE, 0.0)
                .add(Neostamina.DEPLETED_STAMINA_REGENERATION_DELAY_THRESHOLD, 0.0)
                .add(Neostamina.STAMINA_REGENERATION_DELAY_THRESHOLD, 0.0)
                .add(Neostamina.STAMINA_TICK_THRESHOLD, 0.0)
        ;
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void neostamina$pre_jump(CallbackInfo ci) {
        if (!this.abilities.invulnerable && Neostamina.SERVER_CONFIG.jumping_requires_stamina && ((StaminaUsingEntity) this).neostamina$getStamina() <= 0) {
            ci.cancel();
        }
    }

    @Inject(method = "jump", at = @At("RETURN"))
    public void neostamina$post_jump(CallbackInfo ci) {
        if (!this.abilities.invulnerable) {
            if (this.isSprinting()) {
                ((StaminaUsingEntity) this).neostamina$addStamina(-((StaminaUsingEntity) this).neostamina$getSprintJumpingActionStaminaCost());
            } else {
                ((StaminaUsingEntity) this).neostamina$addStamina(-((StaminaUsingEntity) this).neostamina$getJumpingActionStaminaCost());
            }
        }
    }

    @Override
    protected void swimUpward(TagKey<Fluid> fluid) {
        if (this.abilities.invulnerable || !Neostamina.SERVER_CONFIG.swimming_requires_stamina || ((StaminaUsingEntity) this).neostamina$getStamina() > 0) {
            super.swimUpward(fluid);
        }
    }

    @Unique
    private HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getNaturalStaminaModifiers() {
        HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> hashMultimap = HashMultimap.create();
        hashMultimap.put(Neostamina.STAMINA_REGENERATION, new EntityAttributeModifier(Neostamina.id("natural_stamina_regeneration_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_stamina_regeneration, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.BASE_STAMINA, new EntityAttributeModifier(Neostamina.id("natural_base_stamina_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_base_stamina, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.BOOSTED_STAMINA, new EntityAttributeModifier(Neostamina.id("natural_boosted_stamina_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_boosted_stamina, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.MAX_STAMINA, new EntityAttributeModifier(Neostamina.id("natural_max_stamina_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_max_stamina, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.MAX_STAMINA_CHANGE, new EntityAttributeModifier(Neostamina.id("natural_max_stamina_change_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_max_stamina_change, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.DEPLETED_STAMINA_REGENERATION_DELAY_THRESHOLD, new EntityAttributeModifier(Neostamina.id("natural_depleted_stamina_regeneration_delay_threshold_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_depleted_stamina_regeneration_delay_threshold, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.STAMINA_REGENERATION_DELAY_THRESHOLD, new EntityAttributeModifier(Neostamina.id("natural_stamina_regeneration_delay_threshold_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_stamina_regeneration_delay_threshold, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.STAMINA_TICK_THRESHOLD, new EntityAttributeModifier(Neostamina.id("natural_stamina_tick_threshold_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_stamina_tick_threshold, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.RESERVED_STAMINA, new EntityAttributeModifier(Neostamina.id("natural_reserved_stamina_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_reserved_stamina, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.ITEM_USE_STAMINA_COST, new EntityAttributeModifier(Neostamina.id("natural_item_use_stamina_cost_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_item_use_stamina_cost, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.SPRINTING_TICK_STAMINA_COST, new EntityAttributeModifier(Neostamina.id("natural_sprinting_tick_stamina_cost_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_sprinting_tick_stamina_cost, EntityAttributeModifier.Operation.ADD_VALUE));
//        hashMultimap.put(Neostamina.SNEAKING_TICK_STAMINA_COST, new EntityAttributeModifier(Neostamina.id("natural_sneaking_tick_stamina_cost_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_sneaking_tick_stamina_cost, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.WALKING_TICK_STAMINA_COST, new EntityAttributeModifier(Neostamina.id("natural_walking_tick_stamina_cost_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_walking_tick_stamina_cost, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.SWIMMING_TICK_STAMINA_COST, new EntityAttributeModifier(Neostamina.id("natural_swimming_tick_stamina_cost_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_swimming_tick_stamina_cost, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.WALKING_UNDERWATER_TICK_STAMINA_COST, new EntityAttributeModifier(Neostamina.id("natural_walking_underwater_tick_stamina_cost_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_walking_underwater_tick_stamina_cost, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.WALKING_IN_WATER_TICK_STAMINA_COST, new EntityAttributeModifier(Neostamina.id("natural_walking_in_water_tick_stamina_cost_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_walking_in_water_tick_stamina_cost, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.CLIMBING_TICK_STAMINA_COST, new EntityAttributeModifier(Neostamina.id("natural_climbing_tick_stamina_cost_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_climbing_tick_stamina_cost, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.SPRINT_JUMPING_ACTION_STAMINA_COST, new EntityAttributeModifier(Neostamina.id("natural_action_stamina_cost_sprint_jumping_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_action_stamina_cost_sprint_jumping, EntityAttributeModifier.Operation.ADD_VALUE));
        hashMultimap.put(Neostamina.JUMPING_ACTION_STAMINA_COST, new EntityAttributeModifier(Neostamina.id("natural_action_stamina_cost_jumping_modifier"), Neostamina.SERVER_CONFIG.naturalPlayerAttributeValues.natural_action_stamina_cost_jumping, EntityAttributeModifier.Operation.ADD_VALUE));
        return hashMultimap;
    }
}

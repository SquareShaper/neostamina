package net.squareshaper.neostamina.registry;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.effect.StaminaRegenerationEffect;
import net.squareshaper.neostamina.effect.StatusEffectPublic;

public class StatusEffectsRegistry {
    public static final RegistryEntry<StatusEffect> STAMINA_REGENERATION = register("stamina_regeneration", new StaminaRegenerationEffect(StatusEffectCategory.BENEFICIAL, 0xd4af37));
    public static final RegistryEntry<StatusEffect> EXHAUSTION = register("exhaustion", new StatusEffectPublic(StatusEffectCategory.HARMFUL, 0x63471a)
            .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, Identifier.ofVanilla("effect.weakness"), -4.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, Identifier.ofVanilla("effect.slowness"), -0.15F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, Identifier.ofVanilla("effect.mining_fatigue"), -0.1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static void init() {}

    private static RegistryEntry<StatusEffect> register(String name, StatusEffect effect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Neostamina.id(name), effect);
    }
}

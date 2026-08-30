package net.squareshaper.neostamina.registry;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.effect.StaminaRegenerationEffect;

public class StatusEffectsRegistry {
    public static final RegistryEntry<StatusEffect> STAMINA_REGENERATION = register("stamina_regeneration", new StaminaRegenerationEffect(StatusEffectCategory.BENEFICIAL, 0xd4af37));

    public static void init() {

    }

    private static RegistryEntry<StatusEffect> register(String name, StatusEffect effect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Neostamina.id(name), effect);
    }
}

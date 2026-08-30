package net.squareshaper.neostamina.registry;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.squareshaper.neostamina.Neostamina;

public class PotionRegistry {



    public static final RegistryEntry<Potion> STAMINA_REGENERATION = register("stamina_regeneration", new Potion(new StatusEffectInstance(StatusEffectsRegistry.STAMINA_REGENERATION, 900)));
    public static final RegistryEntry<Potion> LONG_STAMINA_REGENERATION = register("long_stamina_regeneration", new Potion("stamina_regeneration", new StatusEffectInstance(StatusEffectsRegistry.STAMINA_REGENERATION, 1800)));
    public static final RegistryEntry<Potion> STRONG_STAMINA_REGENERATION = register("strong_stamina_regeneration", new Potion("stamina_regeneration", new StatusEffectInstance(StatusEffectsRegistry.STAMINA_REGENERATION, 450, 1)));

    public static void init() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.AWKWARD, Items.SUGAR, STAMINA_REGENERATION);
            builder.registerPotionRecipe(STAMINA_REGENERATION, Items.REDSTONE, LONG_STAMINA_REGENERATION);
            builder.registerPotionRecipe(STAMINA_REGENERATION, Items.GUNPOWDER, STRONG_STAMINA_REGENERATION);
        });
    }

    private static RegistryEntry<Potion> register(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Neostamina.id(name), potion);
    }
}

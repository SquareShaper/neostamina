package net.squareshaper.neostamina.mixin.entity.attribute;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.squareshaper.neostamina.Neostamina;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityAttributes.class)
public class EntityAttributesMixin {
    static {
        Neostamina.STAMINA_REGENERATION = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.stamina_regeneration"), new ClampedEntityAttribute("attribute.name.generic.stamina_regeneration", 0.5F, -65536.0F, 65536.0F).setTracked(true));
        Neostamina.MIN_MAX_STAMINA = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.min_max_stamina"), new ClampedEntityAttribute("attribute.name.generic.min_max_stamina", 100.0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.MAX_STAMINA = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.max_stamina"), new ClampedEntityAttribute("attribute.name.generic.max_stamina", 2000.0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.DEPLETED_STAMINA_REGENERATION_DELAY_THRESHOLD = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.depleted_stamina_regeneration_delay_threshold"), new ClampedEntityAttribute("attribute.name.generic.depleted_stamina_regeneration_delay_threshold", 60.0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.STAMINA_REGENERATION_DELAY_THRESHOLD = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.stamina_regeneration_delay_threshold"), new ClampedEntityAttribute("attribute.name.generic.stamina_regeneration_delay_threshold", 20.0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.STAMINA_TICK_THRESHOLD = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.stamina_tick_threshold"), new ClampedEntityAttribute("attribute.name.generic.stamina_tick_threshold", 20.0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.RESERVED_STAMINA = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.reserved_stamina"), new ClampedEntityAttribute("attribute.name.generic.reserved_stamina", 0.0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.ITEM_SINGLE_USE_STAMINA_COST  = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.item_single_use_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.item_single_use_stamina_cost", 0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.ITEM_CONTINUOUS_USE_STAMINA_COST  = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.item_continuous_use_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.item_continuous_use_stamina_cost", 0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.SPRINTING_TICK_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.sprinting_tick_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.sprinting_tick_stamina_cost", 0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.WALKING_TICK_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.walking_tick_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.walking_tick_stamina_cost", 0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.CRAWLING_TICK_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.crawling_tick_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.crawling_tick_stamina_cost", 0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.SWIMMING_TICK_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.swimming_tick_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.swimming_tick_stamina_cost", 0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.WALKING_UNDERWATER_TICK_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.walking_underwater_tick_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.walking_underwater_tick_stamina_cost", 0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.WALKING_IN_WATER_TICK_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.walking_in_water_tick_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.walking_in_water_tick_stamina_cost", 0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.CLIMBING_TICK_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.climbing_tick_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.climbing_tick_stamina_cost", 0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.MINING_TICK_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.mining_tick_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.mining_tick_stamina_cost", 0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.JUMPING_ACTION_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.jumping_action_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.jumping_action_stamina_cost", 0.0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.SPRINT_JUMPING_ACTION_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.sprint_jumping_action_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.sprint_jumping_action_stamina_cost", 0.0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.ATTACKING_ACTION_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.attacking_action_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.attacking_action_stamina_cost", 0.0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.INTERACTION_ACTION_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.interaction_action_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.interaction_action_stamina_cost", 0.0F, 0.0F, 65536.0F).setTracked(true));
        Neostamina.SHIELD_BLOCK_ACTION_STAMINA_COST = Registry.registerReference(Registries.ATTRIBUTE, Neostamina.id("generic.shield_block_action_stamina_cost"), new ClampedEntityAttribute("attribute.name.generic.shield_block_action_stamina_cost", 0.0F, 0.0F, 65536.0F).setTracked(true));
    }

}

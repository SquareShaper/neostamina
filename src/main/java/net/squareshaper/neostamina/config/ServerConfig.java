package net.squareshaper.neostamina.config;

import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.registry.StatusEffectsRegistry;

@ConvertFrom(fileName = "server.json5", folder = "neostamina")
public class ServerConfig extends Config {
    public ServerConfig() {
        super(Neostamina.id("server"));
    }

    public int item_use_cooldown_when_no_stamina = 20;
    public boolean walking_requires_stamina = false;
    public boolean jumping_requires_stamina = false;
    public boolean jumping_is_blocked_by_stamina = false;
    public boolean sprinting_requires_stamina = true;
    public boolean sprinting_is_blocked_by_stamina = false;
    public boolean swimming_requires_stamina = true;
    public boolean swimming_is_blocked_by_stamina = false;
    public boolean breaking_blocks_requires_stamina = true;
    public boolean breaking_blocks_is_blocked_by_stamina = false;
    public boolean attacking_requires_stamina = true;
    public boolean attacking_is_blocked_by_stamina = false;
    public boolean interacting_requires_stamina = true;
    public boolean interacting_is_blocked_by_stamina = false;
    public boolean rowing_requires_stamina = true;
    public boolean climbing_requires_stamina = true;
    public boolean crawling_requires_stamina = true;
    public boolean blocking_with_shield_requires_stamina = true;
    public boolean players_can_exhaust = true;
    public float stamina_regeneration_effect = 25.0F;
    public int stamina_regeneration_doubling_interval = 10*20;
    public ValidatedIdentifier exhausted_status_effect_identifier = ValidatedIdentifier.ofRegistry(Neostamina.id("exhaustion"), Registries.STATUS_EFFECT);
    public NaturalPlayerAttributeValuesSection naturalPlayerAttributeValues = new NaturalPlayerAttributeValuesSection();

    public static class NaturalPlayerAttributeValuesSection extends ConfigSection {
        public float natural_stamina_regeneration = 0.5F;
        public float natural_min_max_stamina = 100.0f;
        public float natural_max_stamina = 2000.0F;
        public float natural_depleted_stamina_regeneration_delay_threshold = 60.0F;
        public float natural_stamina_regeneration_delay_threshold = 20.0F;
        public float natural_stamina_tick_threshold = 5.0F;
        public float natural_reserved_stamina = 0.0F;
        public float natural_item_continuous_use_stamina_cost = 0.5F;
        public float natural_item_single_use_stamina_cost = 2F;
        public float natural_sprinting_tick_stamina_cost = 0.5F;
        public float natural_walking_tick_stamina_cost = 0.25F;
        public float natural_crawling_tick_stamina_cost = 0.1F;
        public float natural_swimming_tick_stamina_cost = 0.5F;
        public float natural_walking_underwater_tick_stamina_cost = 0.1F;
        public float natural_walking_in_water_tick_stamina_cost = 0.1F;
        public float natural_climbing_tick_stamina_cost = 0.5F;
        public float natural_mining_tick_stamina_cost = 0.5F;
        public float natural_rowing_land_tick_stamina_cost = 1.0F;
        public float natural_rowing_water_tick_stamina_cost = 0.5F;
        public float natural_rowing_ice_tick_stamina_cost = 0.25F;
        public float natural_action_stamina_cost_sprint_jumping = 5.0F;
        public float natural_action_stamina_cost_jumping = 10.0F;
        public float natural_action_stamina_cost_interaction = 2.0F;
        public float natural_action_stamina_cost_attack = 2.0F;
        public float natural_action_stamina_cost_shield_block = 100.0F;
    }
}

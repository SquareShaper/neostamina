package net.squareshaper.neostamina.config;

import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import net.squareshaper.neostamina.Neostamina;

@ConvertFrom(fileName = "server.json5", folder = "neostamina")
public class ServerConfig extends Config {
    public ServerConfig() {
        super(Neostamina.id("server"));
    }

    public int item_use_cooldown_when_no_stamina = 20;
    public boolean jumping_requires_stamina = true;
    public boolean sprinting_requires_stamina = true;
    public boolean swimming_requires_stamina = true;
    public boolean breaking_blocks_requires_stamina = true;
    public boolean attacking_requires_stamina = true;
    public float stamina_regeneration_effect = 25.0F;
    public int stamina_regeneration_doubling_interval = 10*20;
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
        public float natural_climbing_tick_stamina_cost = 0.1F;
        public float natural_action_stamina_cost_sprint_jumping = 5.0F;
        public float natural_action_stamina_cost_jumping = 10.0F;
        public float natural_action_stamina_cost_interaction = 2.0F;
        public float natural_action_stamina_cost_attack = 2.0F;
        public float natural_action_stamina_cost_shield_block = 100.0F;
    }
}

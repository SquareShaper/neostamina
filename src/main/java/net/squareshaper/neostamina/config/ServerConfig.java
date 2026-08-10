package net.squareshaper.neostamina.config;

import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
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
    public boolean players_can_exhaust = true;
    public ValidatedIdentifier exhausted_status_effect_identifier = ValidatedIdentifier.ofRegistry(Identifier.of("variousstatuseffects:exhausted"), Registries.STATUS_EFFECT);

    public NaturalPlayerAttributeValuesSection naturalPlayerAttributeValues = new NaturalPlayerAttributeValuesSection();

    public static class NaturalPlayerAttributeValuesSection extends ConfigSection {
        public float natural_stamina_regeneration = 1.0F;
        public float natural_base_stamina = 10.0F;
        public float natural_boosted_stamina = 20.0F;
        public float natural_max_stamina = 20.0F;
        public float natural_depleted_stamina_regeneration_delay_threshold = 60.0F;
        public float natural_stamina_regeneration_delay_threshold = 20.0F;
        public float natural_stamina_tick_threshold = 20.0F;
        public float natural_reserved_stamina = 0.0F;
        public float natural_item_use_stamina_cost = 0.0F;
        public float natural_sprinting_tick_stamina_cost = 0.005F;
//        public float natural_sneaking_tick_stamina_cost = 0.05F; // remove sneaking from here
        public float natural_walking_tick_stamina_cost = 0.0F;
        public float natural_swimming_tick_stamina_cost = 0.005F;
        public float natural_walking_underwater_tick_stamina_cost = 0.0F;
        public float natural_walking_in_water_tick_stamina_cost = 0.0F;
        public float natural_climbing_tick_stamina_cost = 0.0F;
        public float natural_action_stamina_cost_sprint_jumping = 0.1F;
        public float natural_action_stamina_cost_jumping = 0.1F;
    }
}

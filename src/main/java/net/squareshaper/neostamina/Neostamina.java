package net.squareshaper.neostamina;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.squareshaper.neostamina.config.ServerConfig;
import net.squareshaper.neostamina.registry.ServerEventsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Neostamina implements ModInitializer {
	public static final String MOD_ID = "neostamina";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ServerConfig SERVER_CONFIG;

	public static RegistryEntry<EntityAttribute> STAMINA_REGENERATION;
	public static RegistryEntry<EntityAttribute> BASE_STAMINA;
	public static RegistryEntry<EntityAttribute> BOOSTED_STAMINA;
	public static RegistryEntry<EntityAttribute> MAX_STAMINA;
	public static RegistryEntry<EntityAttribute> DEPLETED_STAMINA_REGENERATION_DELAY_THRESHOLD;
	public static RegistryEntry<EntityAttribute> STAMINA_REGENERATION_DELAY_THRESHOLD;
	public static RegistryEntry<EntityAttribute> STAMINA_TICK_THRESHOLD;
	public static RegistryEntry<EntityAttribute> RESERVED_STAMINA;
	public static RegistryEntry<EntityAttribute> ITEM_USE_STAMINA_COST;
	public static RegistryEntry<EntityAttribute> SPRINTING_TICK_STAMINA_COST;
//	public static RegistryEntry<EntityAttribute> SNEAKING_TICK_STAMINA_COST; // don't need this
	public static RegistryEntry<EntityAttribute> WALKING_TICK_STAMINA_COST;
	public static RegistryEntry<EntityAttribute> SWIMMING_TICK_STAMINA_COST;
	public static RegistryEntry<EntityAttribute> WALKING_UNDERWATER_TICK_STAMINA_COST;
	public static RegistryEntry<EntityAttribute> WALKING_IN_WATER_TICK_STAMINA_COST;
	public static RegistryEntry<EntityAttribute> CLIMBING_TICK_STAMINA_COST;
	public static RegistryEntry<EntityAttribute> JUMPING_ACTION_STAMINA_COST;
	public static RegistryEntry<EntityAttribute> SPRINT_JUMPING_ACTION_STAMINA_COST;

	public static final TagKey<Item> USING_COSTS_STAMINA = TagKey.of(RegistryKeys.ITEM, id("using_costs_stamina"));
	public static final TagKey<Item> CONTINUOUS_USING_COSTS_STAMINA = TagKey.of(RegistryKeys.ITEM, id("continuous_using_costs_stamina"));

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Neostamina!");
		LOGGER.info("(thx @TheRedBrain!)");
		SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerConfig::new);

		ServerEventsRegistry.init();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}

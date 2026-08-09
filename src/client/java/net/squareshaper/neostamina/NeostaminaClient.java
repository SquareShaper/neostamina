package net.squareshaper.neostamina;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ClientModInitializer;
import net.squareshaper.neostamina.config.ClientConfig;
import net.squareshaper.neostamina.registry.ClientEventsRegistry;

public class NeostaminaClient implements ClientModInitializer {
    public static ClientConfig CLIENT_CONFIG;

    @Override
    public void onInitializeClient() {
        CLIENT_CONFIG = ConfigApiJava.registerAndLoadConfig(ClientConfig::new, RegisterType.CLIENT);
        ClientEventsRegistry.initializeClientEvents();
    }
}

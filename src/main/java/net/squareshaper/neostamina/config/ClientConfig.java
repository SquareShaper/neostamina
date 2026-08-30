package net.squareshaper.neostamina.config;

import com.github.theredbrain.resourcebarapi.ResourceBarAPI;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedMap;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIdentifier;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedColor;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import net.minecraft.util.Identifier;
import net.squareshaper.neostamina.Neostamina;

import java.util.HashMap;

@ConvertFrom(fileName = "client.json5", folder = "neostamina")
public class ClientConfig extends Config {

    public ClientConfig() {
        super(Neostamina.id("client"));
    }

    public boolean show_full_stamina_bar = true;

    public ResourceBarAPI.ResourceBarOrigin origin = ResourceBarAPI.ResourceBarOrigin.BOTTOM_MIDDLE;
    public ResourceBarAPI.ResourceBarFillDirection fill_direction = ResourceBarAPI.ResourceBarFillDirection.RIGHT_TO_LEFT;

    public boolean dynamically_adjust_to_armor_bar = false;
    public boolean dynamically_adjust_to_air_bar = true;
    public boolean replace_hunger = false;
    public IconBarSettings iconBarSettings = new IconBarSettings();

    public static class IconBarSettings extends ConfigSection {
        public ValidatedInt offset_x = new ValidatedInt(91);
        public ValidatedInt offset_y = new ValidatedInt(-49);
        public ValidatedInt max_icon_amount_per_bar = new ValidatedInt(10);
        public ValidatedBoolean reverse_stack_direction = new ValidatedBoolean(true);
        public ValidatedInt bar_color_variants = new ValidatedInt(3);
        public ValidatedBoolean override_rows = new ValidatedBoolean(true);
    }

    public NumberSettings numberSettings = new NumberSettings();

    public static class NumberSettings extends ConfigSection {
        public boolean show_number = false;
        public boolean show_max_value = false;
        public boolean show_when_stamina_full = true;
        public int offset_x = 0;
        public int offset_y = -46;
        public ValidatedColor color = new ValidatedColor(150, 150, 150);
    }
}
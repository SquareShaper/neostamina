package net.squareshaper.neostamina.registry;

import com.github.theredbrain.resourcebarapi.ResourceBarAPI;
import com.github.theredbrain.resourcebarapi.ResourceBarAPIClient;
import com.mojang.blaze3d.systems.RenderSystem;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.NeostaminaClient;
import net.squareshaper.neostamina.config.ClientConfig;
import net.squareshaper.neostamina.entity.StaminaUsingEntity;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;

public class ClientEventsRegistry {
    private static final String RESOURCE_BAR_IDENTIFIER_STRING = Neostamina.MOD_ID + ":stamina";
    private static final Identifier ICON_STAMINA_CONTAINER = Neostamina.id("hud/icon_stamina_container");
    private static final Identifier ICON_HALF_STAMINA_CONTAINER = Neostamina.id("hud/icon_stamina_container_half");
    private static final Identifier ICON_STAMINA_FULL = Neostamina.id("hud/icon_stamina_full");
    private static final Identifier ICON_STAMINA_HALF = Neostamina.id("hud/icon_stamina_half");
    private static final ResourceBarAPI.ResourceBarDisplay STAMINA_BAR_DISPLAY = ResourceBarAPI.ResourceBarDisplay.ICON;

    public static void initializeClientEvents() {
        HudRenderCallback.EVENT.register((matrixStack, delta) -> {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            PlayerEntity playerEntity = minecraftClient.player;
            ClientConfig clientConfig = NeostaminaClient.CLIENT_CONFIG;
            if (playerEntity != null && !minecraftClient.options.hudHidden) {
                double stamina = MathHelper.ceil(((StaminaUsingEntity) playerEntity).neostamina$getStamina());
                double maxStamina = MathHelper.ceil(((StaminaUsingEntity) playerEntity).neostamina$getMaxStamina());
                double unreservedStamina = MathHelper.ceil(((StaminaUsingEntity) playerEntity).neostamina$getUnreservedStamina());

                if (!playerEntity.isCreative() && maxStamina > 0) {

                    int u = playerEntity.getMaxAir();
                    int v = Math.min(playerEntity.getAir(), u);
                    int air_offset = clientConfig.dynamically_adjust_to_air_bar && playerEntity.isSubmergedIn(FluidTags.WATER) || v < u ? -10 : 0;
                    int armor_offset = clientConfig.dynamically_adjust_to_armor_bar && playerEntity.getArmor() > 0 ? -10 : 0;
                    int hunger_offset = clientConfig.replace_hunger ? 10 : 0;
                    int units_per_bar = MathHelper.ceil((((StaminaUsingEntity) playerEntity).neostamina$getMaxStaminaAttribute()));

                    MutablePair<Integer, Integer> originPos = ResourceBarAPIClient.getOriginPos(matrixStack, clientConfig.origin);

                    if (STAMINA_BAR_DISPLAY == ResourceBarAPI.ResourceBarDisplay.ICON && (stamina < maxStamina || clientConfig.show_full_stamina_bar)) {

                        if (!(units_per_bar > 0)) {
                            Neostamina.LOGGER.warn("MAX_STAMINA attribute not set properly");
                        }
                        if (units_per_bar > 0) {
                            drawIconResourceBar(
                                    minecraftClient,
                                    matrixStack,
                                    RESOURCE_BAR_IDENTIFIER_STRING,
                                    stamina,
                                    maxStamina,
                                    ICON_STAMINA_CONTAINER,
                                    ICON_HALF_STAMINA_CONTAINER,
                                    ICON_STAMINA_FULL,
                                    ICON_STAMINA_HALF,
                                    new ArrayList<>(),// TODO reserved stamina
                                    new ArrayList<>(),
                                    originPos.getLeft(),
                                    originPos.getRight(),
                                    clientConfig.iconBarSettings.offset_x.get(),
                                    clientConfig.iconBarSettings.offset_y.get() + air_offset + armor_offset + hunger_offset,
                                    clientConfig.fill_direction,
                                    clientConfig.iconBarSettings.reverse_stack_direction.get(),
                                    clientConfig.iconBarSettings.max_icon_amount_per_bar.get(),
                                    units_per_bar,
                                    clientConfig.iconBarSettings.bar_color_variants.get(),
                                    clientConfig.iconBarSettings.override_rows.get()
                            );
                        }
                    }
                    if (clientConfig.numberSettings.show_number && (stamina < maxStamina || clientConfig.numberSettings.show_when_stamina_full)) {
                        ResourceBarAPIClient.drawResourceNumber(
                                minecraftClient,
                                minecraftClient.textRenderer,
                                matrixStack,
                                RESOURCE_BAR_IDENTIFIER_STRING,
                                stamina,
                                maxStamina,
                                unreservedStamina,
                                originPos.getLeft(),
                                originPos.getRight(),
                                clientConfig.numberSettings.show_max_value,
                                clientConfig.numberSettings.offset_x,
                                clientConfig.numberSettings.offset_y + air_offset + armor_offset + hunger_offset,
                                clientConfig.numberSettings.color.toInt()
                        );
                    }
                }
            }
        });
    }

    public static void drawIconResourceBar(MinecraftClient client, DrawContext context, String identifier_string, double current_value, double max_value, Identifier container_texture_id, Identifier half_container_texture_id, Identifier full_texture_id, Identifier half_texture_id, List<ResourceBarAPI.AdditionalIconType> additional_affix_values, List<ResourceBarAPI.AdditionalIconType> additional_prefix_values, int origin_x, int origin_y, int offset_x, int offset_y, ResourceBarAPI.ResourceBarFillDirection resource_bar_fill_direction, boolean reverse_stack_direction, int max_icon_amount_per_bar, int units_per_bar, int bar_color_variants, boolean override_rows) {
        // upi = 2000 / 20 = 100
        int units_per_icon = units_per_bar / max_icon_amount_per_bar;
        int half_containers_to_draw = (int) Math.ceil((Math.min(max_value, units_per_bar) / units_per_icon * 2));
        int half_icons_to_draw = (int) Math.ceil(current_value / units_per_icon * 2);
        if (half_icons_to_draw != 0) {
            client.getProfiler().push(identifier_string);
            int bar_y = origin_y + offset_y;
            int bar_x = origin_x + offset_x;
            if (resource_bar_fill_direction == ResourceBarAPI.ResourceBarFillDirection.RIGHT_TO_LEFT) {
                RenderSystem.enableBlend();

                int row_y = bar_y;
                // Draw containers
                for (int i = 0; i < half_containers_to_draw; ++i) {
                    int icon_x = bar_x - i/2 * 8 - 9;
                    if (i % 2 == 0) {
                        context.drawGuiTexture(half_container_texture_id, icon_x, row_y, 9, 9);
                    } else {
                        context.drawGuiTexture(container_texture_id, icon_x, row_y, 9, 9);
                    }
                }

                int icon_x = bar_x - 9;
                int row_number = 0;

                for (int i = 0; i < half_icons_to_draw; i++) {
                    if (i % 2 == 0) {
                        context.drawGuiTexture(half_texture_id.withSuffixedPath("_" + row_number % bar_color_variants), icon_x, row_y, 9, 9);
                    } else {
                        context.drawGuiTexture(full_texture_id.withSuffixedPath("_" + row_number % bar_color_variants), icon_x, row_y, 9, 9);
                        icon_x -= 8;
                    }

                    if (i % 20 == 0 && i > 0) {
                        if (!override_rows) {
                            if (reverse_stack_direction) {
                                row_y -= 10;
                            } else {
                                row_y += 10;
                            }
                        }
                        row_number++;
                        icon_x = bar_x - 9;
                    }
                }
            }

            RenderSystem.disableBlend();
        }
        client.getProfiler().pop();
    }
}

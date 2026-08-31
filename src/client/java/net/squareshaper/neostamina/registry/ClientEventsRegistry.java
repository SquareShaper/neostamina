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

                        if (units_per_bar > 0) {
                            Neostamina.LOGGER.warn("MAX_STAMINA attribute not set properly");
                            drawIconResourceBar(
                                    minecraftClient,
                                    matrixStack,
                                    RESOURCE_BAR_IDENTIFIER_STRING,
                                    stamina,
                                    maxStamina,
                                    ICON_STAMINA_CONTAINER,
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
        ConfigApi.event().onUpdateClient((identifier, config) -> {
            if (identifier.equals(Identifier.of(Neostamina.MOD_ID, "client"))) {
                ResourceBarAPIClient.clearCache(
                        RESOURCE_BAR_IDENTIFIER_STRING,
                        new double[]{
                                -1,
                                -1,
                                0,
                                -91,
                                -45,
                                5,
                                182,
                                5,
                                182,
                                5,
                                182,
                                5,
                                5,
                                0,
                                0
                        },
                        new Identifier[]{
                                Identifier.of("neostamina", "textures/gui/sprites/hud/horizontal_stamina_background.png"),
                                Identifier.of("neostamina", "textures/gui/sprites/hud/horizontal_stamina_progress_decrease_animation.png"),
                                Identifier.of("neostamina", "textures/gui/sprites/hud/horizontal_stamina_progress_increase_animation.png"),
                                Identifier.of("neostamina", "textures/gui/sprites/hud/horizontal_stamina_progress_increase_value.png"),
                                Identifier.of("neostamina", "textures/gui/sprites/hud/horizontal_stamina_progress.png"),
                                Identifier.of("neostamina", "textures/gui/sprites/hud/horizontal_stamina_reserved.png"),
                                Identifier.of("neostamina", "textures/gui/sprites/hud/horizontal_stamina_overlay.png"),
                                null
                        }
                );
            }
        });
    }

    public static void drawIconResourceBar(MinecraftClient client, DrawContext context, String identifier_string, double current_value, double max_value, Identifier container_texture_id, Identifier full_texture_id, Identifier half_texture_id, List<ResourceBarAPI.AdditionalIconType> additional_affix_values, List<ResourceBarAPI.AdditionalIconType> additional_prefix_values, int origin_x, int origin_y, int offset_x, int offset_y, ResourceBarAPI.ResourceBarFillDirection resource_bar_fill_direction, boolean reverse_stack_direction, int max_icon_amount_per_bar, int units_per_bar, int bar_color_variants, boolean override_rows) {
        int units_per_icon = units_per_bar/max_icon_amount_per_bar;
        int bar_counter = (int)(max_value + (double)0.5F) / units_per_icon;
        if (bar_counter != 0) {
            client.getProfiler().push(identifier_string);
            int bar_y = origin_y + offset_y;
            int bar_x = origin_x + offset_x;
            if (resource_bar_fill_direction == ResourceBarAPI.ResourceBarFillDirection.LEFT_TO_RIGHT) {
                int m = bar_y;
                int n = 0;
                RenderSystem.enableBlend();

                for(; bar_counter > 0; n += max_icon_amount_per_bar * 2) {
                    int o = Math.min(bar_counter, max_icon_amount_per_bar);
                    bar_counter -= o;

                    for(int p = 0; p < o; ++p) {
                        int q = bar_x + p * 8;
                        context.drawGuiTexture(container_texture_id, q, m, 9, 9);
                        if ((double)(p * 2 + 1 + n) < current_value) {
                            context.drawGuiTexture(full_texture_id, q, m, 9, 9);
                        }

                        if ((double)(p * 2 + 1 + n) == current_value) {
                            context.drawGuiTexture(half_texture_id, q, m, 9, 9);
                        }
                    }

                    if (reverse_stack_direction) {
                        m -= 10;
                    } else {
                        m += 10;
                    }
                }

                RenderSystem.disableBlend();
            } else if (resource_bar_fill_direction == ResourceBarAPI.ResourceBarFillDirection.RIGHT_TO_LEFT) {
                int row_y = bar_y;
                int stamina_so_far = 0;
                int row_number = 0;
                RenderSystem.enableBlend();

                for(; bar_counter > 0; stamina_so_far += max_icon_amount_per_bar * units_per_icon) {
                    int bar_counter_reduction = Math.min(bar_counter, max_icon_amount_per_bar);
                    bar_counter -= bar_counter_reduction;

                    for(int icon_in_bar = 0; icon_in_bar < bar_counter_reduction; ++icon_in_bar) {
                        int icon_x = bar_x - icon_in_bar * 8 - 9;
                        if (row_number == 0) {
                            context.drawGuiTexture(container_texture_id, icon_x, row_y, 9, 9);
                        }

                        double stamina_left = current_value - stamina_so_far;
                        if (stamina_left > icon_in_bar * units_per_icon && stamina_left <= icon_in_bar * units_per_icon + (double) units_per_icon / 2) {
                            context.drawGuiTexture(half_texture_id.withSuffixedPath("_"+row_number % bar_color_variants), icon_x, row_y, 9, 9);
                        }
                        if (stamina_left > icon_in_bar * units_per_icon + (double) units_per_icon / 2) {
                            context.drawGuiTexture(full_texture_id.withSuffixedPath("_"+row_number % bar_color_variants), icon_x, row_y, 9, 9);
                        }


//                        if ((double)(icon_in_bar * units_per_icon + units_per_icon/2 + stamina_so_far) < current_value) {
//                            context.drawGuiTexture(full_texture_id.withSuffixedPath("_"+row_number % bar_color_variants), icon_x, row_y, 9, 9);
//                        }
//
//                        if ((double)(icon_in_bar * units_per_icon + units_per_icon/2 + stamina_so_far) == current_value) {
//                            context.drawGuiTexture(half_texture_id.withSuffixedPath("_"+row_number % bar_color_variants), icon_x, row_y, 9, 9);
//                        }
                    }

                    if (!override_rows) {
                        if (reverse_stack_direction) {
                            row_y -= 10;
                        } else {
                            row_y += 10;
                        }
                    }
                    row_number++;
                }

                RenderSystem.disableBlend();
            } else if (resource_bar_fill_direction == ResourceBarAPI.ResourceBarFillDirection.TOP_TO_BOTTOM) {
                int n = bar_x;
                int m = 0;
                RenderSystem.enableBlend();

                for(; bar_counter > 0; m += max_icon_amount_per_bar * 2) {
                    int o = Math.min(bar_counter, max_icon_amount_per_bar);
                    bar_counter -= o;

                    for(int p = 0; p < o; ++p) {
                        int q = bar_y + p * 8;
                        context.drawGuiTexture(container_texture_id, n, q, 9, 9);
                        if ((double)(p * 2 + 1 + m) < current_value) {
                            context.drawGuiTexture(full_texture_id, n, q, 9, 9);
                        }

                        if ((double)(p * 2 + 1 + m) == current_value) {
                            context.drawGuiTexture(half_texture_id, n, q, 9, 9);
                        }
                    }

                    if (reverse_stack_direction) {
                        n -= 10;
                    } else {
                        n += 10;
                    }
                }

                RenderSystem.disableBlend();
            } else if (resource_bar_fill_direction == ResourceBarAPI.ResourceBarFillDirection.BOTTOM_TO_TOP) {
                int n = bar_x;
                int m = 0;
                RenderSystem.enableBlend();

                for(; bar_counter > 0; m += max_icon_amount_per_bar * 2) {
                    int o = Math.min(bar_counter, max_icon_amount_per_bar);
                    bar_counter -= o;

                    for(int p = 0; p < o; ++p) {
                        int q = bar_y - p * 8 - 9;
                        context.drawGuiTexture(container_texture_id, n, q, 9, 9);
                        if ((double)(p * 2 + 1 + m) < current_value) {
                            context.drawGuiTexture(full_texture_id, n, q, 9, 9);
                        }

                        if ((double)(p * 2 + 1 + m) == current_value) {
                            context.drawGuiTexture(half_texture_id, n, q, 9, 9);
                        }
                    }

                    if (reverse_stack_direction) {
                        n -= 10;
                    } else {
                        n += 10;
                    }
                }

                RenderSystem.disableBlend();
            }

            client.getProfiler().pop();
        }

    }
}

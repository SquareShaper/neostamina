package net.squareshaper.neostamina.registry;

import com.github.theredbrain.resourcebarapi.ResourceBarAPI;
import com.github.theredbrain.resourcebarapi.ResourceBarAPIClient;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
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

public class ClientEventsRegistry {
    private static final String RESOURCE_BAR_IDENTIFIER_STRING = Neostamina.MOD_ID + ":stamina";
    private static final Identifier ICON_STAMINA_CONTAINER = Neostamina.id("hud/icon_stamina_container");
    private static final Identifier ICON_STAMINA_FULL = Neostamina.id("hud/icon_stamina_full");
    private static final Identifier ICON_STAMINA_HALF = Neostamina.id("hud/icon_stamina_half");

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

                    MutablePair<Integer, Integer> originPos = ResourceBarAPIClient.getOriginPos(matrixStack, clientConfig.origin);

                    if (clientConfig.stamina_bar_display == ResourceBarAPI.ResourceBarDisplay.ICON && (stamina < maxStamina || clientConfig.show_full_stamina_bar)) {
                        ResourceBarAPIClient.drawIconResourceBar(
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
                                clientConfig.iconBarSettings.offset_y.get() + air_offset + armor_offset,
                                clientConfig.fill_direction,
                                clientConfig.iconBarSettings.reverse_stack_direction.get(),
                                clientConfig.iconBarSettings.max_icon_amount_per_bar.get()
                        );
                    } else if (clientConfig.stamina_bar_display == ResourceBarAPI.ResourceBarDisplay.SMOOTH && (stamina < maxStamina || clientConfig.show_full_stamina_bar)) {
                        ResourceBarAPIClient.drawSmoothResourceBar(
                                minecraftClient,
                                matrixStack,
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
                                },
                                stamina,
                                maxStamina,
                                MathHelper.ceil(((StaminaUsingEntity) playerEntity).neostamina$getRegeneratedStamina()),
                                unreservedStamina,
                                originPos.getLeft(),
                                originPos.getRight(),
                                clientConfig.smoothBarSettings.positionSettings.offsets_x,
                                clientConfig.smoothBarSettings.positionSettings.offsets_y,
                                0,
                                air_offset + armor_offset,
                                clientConfig.fill_direction,
                                clientConfig.smoothBarSettings.textureSettings.backgroundTextureSettings.texture_heights,
                                clientConfig.smoothBarSettings.textureSettings.backgroundTextureSettings.texture_widths,
                                clientConfig.smoothBarSettings.textureSettings.backgroundTextureSettings.texture_ids,
                                clientConfig.smoothBarSettings.textureSettings.progressTextureSettings.offset_x,
                                clientConfig.smoothBarSettings.textureSettings.progressTextureSettings.offset_y,
                                clientConfig.smoothBarSettings.textureSettings.progressTextureSettings.texture_heights,
                                clientConfig.smoothBarSettings.textureSettings.progressTextureSettings.texture_widths,
                                clientConfig.smoothBarSettings.textureSettings.progressTextureSettings.progress_decrease_animation_texture_ids,
                                clientConfig.smoothBarSettings.textureSettings.progressTextureSettings.progress_increase_animation_texture_ids,
                                clientConfig.smoothBarSettings.textureSettings.progressTextureSettings.progress_increase_value_texture_ids,
                                clientConfig.smoothBarSettings.textureSettings.progressTextureSettings.progress_texture_ids,
                                clientConfig.smoothBarSettings.textureSettings.reservedTextureSettings.offset_x,
                                clientConfig.smoothBarSettings.textureSettings.reservedTextureSettings.offset_y,
                                clientConfig.smoothBarSettings.textureSettings.reservedTextureSettings.texture_heights,
                                clientConfig.smoothBarSettings.textureSettings.reservedTextureSettings.texture_widths,
                                clientConfig.smoothBarSettings.textureSettings.reservedTextureSettings.texture_ids,
                                clientConfig.smoothBarSettings.show_current_value_overlay,
                                clientConfig.smoothBarSettings.textureSettings.overlayTextureSettings.offset_x,
                                clientConfig.smoothBarSettings.textureSettings.overlayTextureSettings.offset_y,
                                clientConfig.smoothBarSettings.textureSettings.overlayTextureSettings.texture_heights,
                                clientConfig.smoothBarSettings.textureSettings.overlayTextureSettings.texture_widths,
                                clientConfig.smoothBarSettings.textureSettings.overlayTextureSettings.texture_ids,
                                clientConfig.smoothBarSettings.show_icon,
                                clientConfig.smoothBarSettings.iconTextureSettings.offset_x,
                                clientConfig.smoothBarSettings.iconTextureSettings.offset_y,
                                clientConfig.smoothBarSettings.iconTextureSettings.texture_heights,
                                clientConfig.smoothBarSettings.iconTextureSettings.texture_widths,
                                clientConfig.smoothBarSettings.iconTextureSettings.texture_ids,
                                clientConfig.smoothBarSettings.enable_smooth_animation,
                                clientConfig.smoothBarSettings.animationSettings.animation_interval,
                                clientConfig.smoothBarSettings.animationSettings.max_value_change_is_animated
                        );
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
                                clientConfig.numberSettings.offset_y + air_offset + armor_offset,
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
}

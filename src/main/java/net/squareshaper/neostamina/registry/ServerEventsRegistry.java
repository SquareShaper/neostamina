package net.squareshaper.neostamina.registry;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.entity.StaminaUsingEntity;

public class ServerEventsRegistry {
    public static void init() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.isIn(Neostamina.USING_COSTS_STAMINA)) {
                if (((StaminaUsingEntity) player).neostamina$getStamina() <= 0 && ((StaminaUsingEntity) player).neostamina$getItemSingleUseStaminaCost() > 0) {
                    player.getItemCooldownManager().set(itemStack.getItem(), Neostamina.SERVER_CONFIG.item_use_cooldown_when_no_stamina);
                    return TypedActionResult.fail(itemStack);
                }
                ((StaminaUsingEntity) player).neostamina$addStamina(-((StaminaUsingEntity) player).neostamina$getItemSingleUseStaminaCost(), true);
            }
            return TypedActionResult.pass(itemStack);
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (((StaminaUsingEntity) player).neostamina$getStamina() <= 0 && ((StaminaUsingEntity) player).neostamina$getAttackActionStaminaCost() > 0) {
                return ActionResult.FAIL;
            }
            ((StaminaUsingEntity) player).neostamina$addStamina(-((StaminaUsingEntity) player).neostamina$getAttackActionStaminaCost(), true);
            return ActionResult.PASS;
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (((StaminaUsingEntity) player).neostamina$getStamina() <= 0 && ((StaminaUsingEntity) player).neostamina$getMiningTickStaminaCost() > 0) {
                return ActionResult.FAIL;
            }
            ((StaminaUsingEntity) player).neostamina$addStamina(-((StaminaUsingEntity) player).neostamina$getMiningTickStaminaCost(), true);
            return ActionResult.PASS;
        });
    }
}

package net.squareshaper.neostamina.mixin.client.network;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.entity.StaminaUsingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private BlockPos currentBreakingPos;

    @Shadow
    public abstract void cancelBlockBreaking();

    @WrapMethod(method = "isCurrentlyBreaking")
    private boolean neostamina$breakingBlock(BlockPos pos, Operation<Boolean> original) {
        if (Neostamina.SERVER_CONFIG.breaking_blocks_requires_stamina) {
            if (((StaminaUsingEntity) this.client.player).neostamina$getStamina() <= 0 && ((StaminaUsingEntity) this.client.player).neostamina$getMiningTickStaminaCost() > 0
                    && pos.equals(this.currentBreakingPos)) {
                this.cancelBlockBreaking();
                this.client.world.setBlockBreakingInfo(this.client.player.getId(), this.currentBreakingPos, 0);
            }
        }
        return original.call(pos);
    }

    @WrapMethod(method = "interactBlock")
    private ActionResult neostamina$interactingBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, Operation<ActionResult> original) {
        ActionResult result = original.call(player, hand, hitResult);
    //TODO: Make this work via server packets uwu much pain
        if (result == ActionResult.SUCCESS_NO_ITEM_USED || result == ActionResult.SUCCESS) {
            int i = 1;
            if (Neostamina.SERVER_CONFIG.interacting_requires_stamina && ((StaminaUsingEntity) player).neostamina$getInteractionActionStaminaCost() > 0) {
                if (((StaminaUsingEntity) player).neostamina$getStamina() <= 0) {
                    return ActionResult.FAIL;
                }
                ((StaminaUsingEntity) player).neostamina$addStamina(-((StaminaUsingEntity) player).neostamina$getInteractionActionStaminaCost(), true);
            }
        }

        return result;
    }
}

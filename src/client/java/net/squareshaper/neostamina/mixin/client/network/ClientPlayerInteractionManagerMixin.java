package net.squareshaper.neostamina.mixin.client.network;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.entity.StaminaUsingEntity;
import net.squareshaper.neostamina.networking.UseStaminaPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private BlockPos currentBreakingPos;

    @Shadow
    public abstract void cancelBlockBreaking();

    @Shadow
    public abstract boolean isBreakingBlock();

    @WrapMethod(method = "isCurrentlyBreaking")
    private boolean neostamina$breakingBlock(BlockPos pos, Operation<Boolean> original) {
        if (Neostamina.SERVER_CONFIG.breaking_blocks_is_blocked_by_stamina && ((StaminaUsingEntity) this.client.player).neostamina$getStamina() <= 0 && pos.equals(this.currentBreakingPos)) {
            this.cancelBlockBreaking();
            this.client.world.setBlockBreakingInfo(this.client.player.getId(), this.currentBreakingPos, 0);
        }
        return original.call(pos);
    }

    @WrapMethod(method = "interactBlock")
    private ActionResult neostamina$interactingBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, Operation<ActionResult> original) {
        ActionResult result = original.call(player, hand, hitResult);
        if (client.player.isCreative() || client.player.isSpectator() || !(result == ActionResult.SUCCESS_NO_ITEM_USED || result == ActionResult.SUCCESS)) {
            return result;
        }
        if (Neostamina.SERVER_CONFIG.interacting_is_blocked_by_stamina && ((StaminaUsingEntity) player).neostamina$getStamina() <= 0) {
            return ActionResult.FAIL;
        }
        if (Neostamina.SERVER_CONFIG.interacting_costs_stamina && ((StaminaUsingEntity) player).neostamina$getInteractionActionStaminaCost() > 0) {
            ClientPlayNetworking.send(new UseStaminaPayload(-((StaminaUsingEntity) player).neostamina$getInteractionActionStaminaCost(), true)); // Using a custom payload to send a package to the server
        }


        return result;
    }

    // TODO: Make a method for costing stamina when breaking
    @Inject(method = "tick", at = @At("TAIL"))
    private void neostamina$doBlockBreakingCosts(CallbackInfo ci) {
        if (!this.isBreakingBlock()) {
            return;
        }
        ClientPlayerEntity player = this.client.player;
        if (Neostamina.SERVER_CONFIG.breaking_blocks_costs_stamina && ((StaminaUsingEntity) player).neostamina$getMiningTickStaminaCost() > 0) {
            ClientPlayNetworking.send(new UseStaminaPayload(-((StaminaUsingEntity) player).neostamina$getMiningTickStaminaCost(), true)); // Using a custom payload to send a package to the server
        }
    }
}

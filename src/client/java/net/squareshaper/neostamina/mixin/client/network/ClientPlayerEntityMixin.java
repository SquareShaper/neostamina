package net.squareshaper.neostamina.mixin.client.network;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.entity.StaminaUsingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements StaminaUsingEntity {
    @WrapMethod(method = "canSprint")
    private boolean staminaattributes$canSprint(Operation<Boolean> original) {
        boolean result = original.call();
        if (Neostamina.SERVER_CONFIG.staminaConsumtionAndActionBlockingRules.sprinting_is_blocked_by_stamina && this.neostamina$getStamina() <= 0) {
            result = false;
        }
        return result;
    }
}

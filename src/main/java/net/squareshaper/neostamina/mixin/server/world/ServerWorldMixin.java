package net.squareshaper.neostamina.mixin.server.world;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.squareshaper.neostamina.entity.StaminaUsingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Shadow
    @Final
    private List<ServerPlayerEntity> players;

    @Inject(method = "wakeSleepingPlayers", at = @At("HEAD"))
    public void neostamina$wakeSleepingPlayers(CallbackInfo ci) {
        this.players.stream().filter(LivingEntity::isSleeping).toList().forEach(player -> ((StaminaUsingEntity) player).neostamina$setApplyMaxStamina(true));
    }
}

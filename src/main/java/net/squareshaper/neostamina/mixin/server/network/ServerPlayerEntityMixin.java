package net.squareshaper.neostamina.mixin.server.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.squareshaper.neostamina.entity.StaminaUsingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements StaminaUsingEntity {

    @Shadow public abstract ServerStatHandler getStatHandler();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V", ordinal = 0))
    private void neostamina$increaseTravelMotionStats_swimming(CallbackInfo ci) {
        if (!this.getAbilities().invulnerable) {
            this.neostamina$addStamina(-this.neostamina$getSwimmingTickStaminaCost());
        }
    }

    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V", ordinal = 1))
    private void neostamina$increaseTravelMotionStats_walk_underwater(CallbackInfo ci) {
        if (!this.getAbilities().invulnerable) {
            this.neostamina$addStamina(-this.neostamina$getWalkingUnderwaterTickStaminaCost());
        }
    }

    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V", ordinal = 2))
    private void neostamina$increaseTravelMotionStats_walk_in_water(CallbackInfo ci) {
        if (!this.getAbilities().invulnerable) {
            this.neostamina$addStamina(-this.neostamina$getWalkingInWaterTickStaminaCost());
        }
    }

    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;increaseStat(Lnet/minecraft/util/Identifier;I)V", ordinal = 3))
    private void neostamina$increaseTravelMotionStats_climbing(CallbackInfo ci) {
        if (!this.getAbilities().invulnerable) {
            this.neostamina$addStamina(-this.neostamina$getClimbingTickStaminaCost());
        }
    }

    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V", ordinal = 3))
    private void neostamina$increaseTravelMotionStats_sprinting(CallbackInfo ci) {
        if (!this.getAbilities().invulnerable) {
            this.neostamina$addStamina(-this.neostamina$getSprintingTickStaminaCost());
        }
    }

//    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V", ordinal = 4))
//    private void neostamina$increaseTravelMotionStats_sneaking(CallbackInfo ci) {
//        if (!this.getAbilities().invulnerable) {
//            this.neostamina$addStamina(-this.neostamina$getSneakingTickStaminaCost());
//        }
//    } // no sneaking uwu

    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V", ordinal = 5))
    private void neostamina$increaseTravelMotionStats_walking(CallbackInfo ci) {
        if (!this.getAbilities().invulnerable) {
            this.neostamina$addStamina(-this.neostamina$getWalkingTickStaminaCost());
        }
    }

    @Inject(method = "onSpawn", at = @At("TAIL"))
    public void neostamina$onSpawn(CallbackInfo ci) {
        this.neostamina$setApplyOldStamina(false);
        if (this.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME)) <= 0) {
            this.neostamina$setApplyMaxStamina(true);
        }
    }
}

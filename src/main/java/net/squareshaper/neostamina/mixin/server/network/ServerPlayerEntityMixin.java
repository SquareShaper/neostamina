package net.squareshaper.neostamina.mixin.server.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Blocks;
import net.minecraft.block.IceBlock;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.squareshaper.neostamina.Neostamina;
import net.squareshaper.neostamina.entity.StaminaUsingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements StaminaUsingEntity {

    @Shadow public abstract ServerStatHandler getStatHandler();

    @Shadow
    public abstract ServerWorld getServerWorld();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V", ordinal = 0))
    private void neostamina$increaseTravelMotionStats_swimming(CallbackInfo ci) {
        if (!this.getAbilities().invulnerable) {
            this.neostamina$addStamina(-this.neostamina$getSwimmingTickStaminaCost(), true);
        }
    }

    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V", ordinal = 1))
    private void neostamina$increaseTravelMotionStats_walk_underwater(CallbackInfo ci) {
        if (!this.getAbilities().invulnerable && !this.isSneaking()) {
            this.neostamina$addStamina(-this.neostamina$getWalkingUnderwaterTickStaminaCost(), true);
        }
    }

    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V", ordinal = 2))
    private void neostamina$increaseTravelMotionStats_walk_in_water(CallbackInfo ci) {
        if (!this.getAbilities().invulnerable && !this.isSneaking()) {
            this.neostamina$addStamina(-this.neostamina$getWalkingInWaterTickStaminaCost(), true);
        }
    }

    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;increaseStat(Lnet/minecraft/util/Identifier;I)V", ordinal = 3))
    private void neostamina$increaseTravelMotionStats_climbing(CallbackInfo ci) {
        if (!this.getAbilities().invulnerable) {
            this.neostamina$addStamina(-this.neostamina$getClimbingTickStaminaCost(), true);
        }
    }

    @Inject(method = "increaseTravelMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;addExhaustion(F)V", ordinal = 3))
    private void neostamina$increaseTravelMotionStats_sprinting(CallbackInfo ci) {
        if (!this.getAbilities().invulnerable) {
            this.neostamina$addStamina(-this.neostamina$getSprintingTickStaminaCost(), true);
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
        if (!this.getAbilities().invulnerable && !this.isSneaking() && !this.isSwimming()) {
            if (this.canChangeIntoPose(EntityPose.SWIMMING) && !this.canChangeIntoPose(EntityPose.CROUCHING)) {
                // These requirements are from updatePose() in PlayerEntity - if testing shows that it applies to non crawling as well,
                // go there and figure stuff out
                this.neostamina$addStamina(-this.neostamina$getCrawlingTickStaminaCost(), true);
            } else {
                this.neostamina$addStamina(-this.neostamina$getWalkingTickStaminaCost(), false);
            }
        }
    }

    @Inject(method = "increaseRidingMotionStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;increaseStat(Lnet/minecraft/util/Identifier;I)V", ordinal = 1))
    private void neostamina$increaseRidingMotionStats_boat(double deltaX, double deltaY, double deltaZ, CallbackInfo ci) {
        ServerWorld world = this.getServerWorld();

        if(!Neostamina.SERVER_CONFIG.rowing_requires_stamina) {
            return;
        }

        if (!world.getBlockState(this.getBlockPos()).getFluidState().isEmpty()) {
            // Water costs
            if (this.neostamina$getRowingWaterTickStaminaCost() > 0) {
                this.neostamina$addStamina(-this.neostamina$getRowingWaterTickStaminaCost(), true);
            }
        } else if (world.getBlockState(this.getBlockPos()).isIn(BlockTags.ICE)) {
            //ice costs
            if (this.neostamina$getRowingIceTickStaminaCost() > 0) {
                this.neostamina$addStamina(-this.neostamina$getRowingIceTickStaminaCost(), true);
            }
        } else {
            // land costs
            if (this.neostamina$getRowingLandTickStaminaCost() > 0) {
                this.neostamina$addStamina(-this.neostamina$getRowingLandTickStaminaCost(), true);
            }
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

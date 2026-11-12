package io.github.apace100.origins.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.origins.power.OriginsPowerTypes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public class LikeWaterMixin {

    @com.llamalad7.mixinextras.injector.ModifyExpressionValue(method = "getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityFluidInteraction;isEyeInFluidMatching(Lnet/minecraft/world/entity/Entity;Lnet/neoforged/neoforge/fluids/InFluidPredicate;)Z"))
    private boolean origins$modifyUnderwaterMiningSpeed(boolean isEyeInFluid) {
        if (isEyeInFluid) {
            if (PowerHolderComponent.KEY.get((Player) (Object) this).hasPower(OriginsPowerTypes.LIKE_WATER)) {
                return false; // Pretend we are not in water to prevent the 5x speed penalty
            }
        }
        return isEyeInFluid;
    }
}

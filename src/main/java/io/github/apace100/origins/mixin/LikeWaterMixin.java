package io.github.apace100.origins.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.origins.power.OriginsPowerTypes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public class LikeWaterMixin {

    @com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean origins$modifyUnderwaterMiningSpeed(Player instance, net.minecraft.tags.TagKey<net.minecraft.world.level.material.Fluid> tag, com.llamalad7.mixinextras.injector.wrapoperation.Operation<Boolean> original) {
        boolean isEyeInFluid = original.call(instance, tag);
        if (isEyeInFluid && tag == net.minecraft.tags.FluidTags.WATER) {
            if (PowerHolderComponent.KEY.get(instance).hasPower(OriginsPowerTypes.LIKE_WATER)) {
                return false; // Pretend we are not in water to prevent the 5x speed penalty
            }
        }
        return isEyeInFluid;
    }
}

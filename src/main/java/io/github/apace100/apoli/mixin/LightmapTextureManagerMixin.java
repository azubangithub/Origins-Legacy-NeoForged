package io.github.apace100.apoli.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.NightVisionPower;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapRenderStateExtractor.class)
public abstract class LightmapTextureManagerMixin {

    @WrapOperation(
        method = "extract",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/core/Holder;)Z")
    )
    private boolean apoli$fakeNightVisionEffect(LocalPlayer instance, Holder<MobEffect> holder, Operation<Boolean> original) {
        boolean result = original.call(instance, holder);
        if (!result && holder.is(MobEffects.NIGHT_VISION)) {
            return PowerHolderComponent.KEY.get(instance).getPowers(NightVisionPower.class)
                .stream().anyMatch(NightVisionPower::isActive);
        }
        return result;
    }
}

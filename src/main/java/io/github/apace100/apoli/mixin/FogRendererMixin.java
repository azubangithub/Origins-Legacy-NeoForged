package io.github.apace100.apoli.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.NightVisionPower;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @WrapOperation(
        method = "computeFogColor",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z")
    )
    private static boolean apoli$fakeNightVisionEffectForFogColor(LivingEntity instance, Holder<MobEffect> effect, Operation<Boolean> original) {
        var value = original.call(instance, effect);
        if (!value && instance instanceof Player player && effect.is(MobEffects.NIGHT_VISION) && !player.hasEffect(MobEffects.NIGHT_VISION)) {
            return PowerHolderComponent.KEY.get(player).getPowers(NightVisionPower.class)
                .stream().anyMatch(NightVisionPower::isActive);
        }
        return value;
    }

}

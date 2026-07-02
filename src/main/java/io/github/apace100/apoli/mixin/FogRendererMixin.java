package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.NightVisionPower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FogRenderer.class)
@OnlyIn(Dist.CLIENT)
public abstract class FogRendererMixin {

    @com.llamalad7.mixinextras.injector.ModifyExpressionValue(
        method = "setupColor",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 0)
    )
    private static boolean apoli$fakeNightVisionEffectForFogColor(boolean original) {
        if (original) return true;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            return PowerHolderComponent.KEY.get(minecraft.player).getPowers(NightVisionPower.class)
                .stream().anyMatch(NightVisionPower::isActive);
        }
        return false;
    }

}

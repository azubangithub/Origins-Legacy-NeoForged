package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.NightVisionPower;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(LightTexture.class)
@OnlyIn(Dist.CLIENT)
public abstract class LightmapTextureManagerMixin implements AutoCloseable {

    @Shadow @Final private Minecraft minecraft;

    // Inject into the hasEffect(NIGHT_VISION) check to make it return true when the power is active
    @com.llamalad7.mixinextras.injector.ModifyExpressionValue(
        method = "updateLightTexture",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 0)
    )
    private boolean apoli$fakeNightVisionEffect(boolean original) {
        if (original) return true;
        // If the player has a NightVisionPower active, pretend they have the effect
        if (minecraft.player != null) {
            return PowerHolderComponent.KEY.get(minecraft.player).getPowers(NightVisionPower.class)
                .stream().anyMatch(NightVisionPower::isActive);
        }
        return false;
    }
}

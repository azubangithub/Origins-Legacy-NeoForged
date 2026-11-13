package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.OverlayPower;
import io.github.apace100.apoli.power.OverrideHudTexturePower;
import io.github.apace100.apoli.screen.GameHudRender;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Gui.class)
public class InGameHudMixin {

    @Shadow @Final private Minecraft minecraft;



    // TODO Origins-Legacy: Map GUI icons correctly
    @ModifyArg(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"), index = 1)
    private static ResourceLocation changeStatusBarTextures(ResourceLocation original) {
        Optional<OverrideHudTexturePower> power = PowerHolderComponent.getPowers(Minecraft.getInstance().player, OverrideHudTexturePower.class).stream().findFirst();
        if (power.isPresent()) {
            return power.get().getStatusBarTexture();
        }
        return original;
    }

    @ModifyArg(method = "renderHeart", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"), index = 1)
    public ResourceLocation changeHearts(ResourceLocation original)
    {
        Optional<OverrideHudTexturePower> power = PowerHolderComponent.getPowers(this.minecraft.player, OverrideHudTexturePower.class).stream().findFirst();
        if (power.isPresent()) {
            return power.get().getStatusBarTexture();
        }
        return original;
    }

    @ModifyArg(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"), index = 1)
    public ResourceLocation changeCrosshair(ResourceLocation original) {
        Optional<OverrideHudTexturePower> power = PowerHolderComponent.getPowers(this.minecraft.player, OverrideHudTexturePower.class).stream().findFirst();
        if (power.isPresent()) {
            return power.get().getStatusBarTexture();
        }
        return original;
    }

    @ModifyArg(method = "renderVehicleHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V"), index = 1)
    public ResourceLocation changeMountHealth(ResourceLocation original) {
        Optional<OverrideHudTexturePower> power = PowerHolderComponent.getPowers(this.minecraft.player, OverrideHudTexturePower.class).stream().findFirst();
        if (power.isPresent()) {
            return power.get().getStatusBarTexture();
        }
        return original;
    }
}

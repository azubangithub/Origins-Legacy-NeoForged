package io.github.apace100.apoli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ElytraFlightPower;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ElytraLayer.class)
public class ElytraFeatureRendererMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void apoli$allowElytraRendering(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (PowerHolderComponent.getPowers(entity, ElytraFlightPower.class).stream().anyMatch(ElytraFlightPower::shouldRenderElytra) && !entity.isInvisible()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getElytraTexture", at = @At("HEAD"), cancellable = true)
    private void apoli$getElytraTexture(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<ResourceLocation> cir) {
        for (ElytraFlightPower power : PowerHolderComponent.getPowers(entity, ElytraFlightPower.class)) {
            if (power.getTextureLocation() != null) {
                cir.setReturnValue(power.getTextureLocation());
            }
        }
    }
}
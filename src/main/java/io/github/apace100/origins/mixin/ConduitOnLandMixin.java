package io.github.apace100.origins.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.origins.power.OriginsPowerTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class ConduitOnLandMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void origins$conduitOnLand(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (PowerHolderComponent.KEY.get(player).hasPower(OriginsPowerTypes.CONDUIT_POWER_ON_LAND)) {
            if (player.hasEffect(MobEffects.CONDUIT_POWER)) {
                // Do not apply if they already have it from a real conduit or if they are in water
                if (player.isEyeInFluid(net.minecraft.tags.FluidTags.WATER)) {
                    return;
                }
            }
            if (player.level().isRainingAt(player.blockPosition())) {
                player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 200, 0, false, false, true));
            }
        }
    }
}

package io.github.apace100.origins.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.apace100.origins.power.OriginsPowerTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public final class WaterBreathingMixin {

    @Mixin(LivingEntity.class)
    public static abstract class CanBreatheInWater extends Entity {

        public CanBreatheInWater(EntityType<?> type, Level world) {
            super(type, world);
        }

        @Inject(at = @At("HEAD"), method = "canBreatheUnderwater", cancellable = true)
        public void doWaterBreathing(CallbackInfoReturnable<Boolean> info) {
            if(OriginsPowerTypes.WATER_BREATHING.isActive(this)) {
                info.setReturnValue(true);
            }
        }
    }

    @Mixin(Player.class)
    public static abstract class UpdateAir extends LivingEntity {

        protected UpdateAir(EntityType<? extends LivingEntity> entityType, Level world) {
            super(entityType, world);
        }

        @ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"), method = "turtleHelmetTick")
        public boolean isSubmergedInProxy(boolean submerged) {
            return submerged || OriginsPowerTypes.WATER_BREATHING.isActive(this);
        }
    }
}

package io.github.apace100.apoli.power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.util.ApoliSharedMixinValues;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

public class IgnoreWaterPower extends Power {

    public IgnoreWaterPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    /**
     * Checks whether fluid pushing should be suppressed for {@code entity}.
     * {@link ApoliSharedMixinValues#CURRENT_PUSHING_FLUID} is checked because
     * NeoForge clears {@code forgeFluidTypeHeight} before evaluating fluid push vectors.
     */
    public static boolean shouldIgnoreFluidPush(Entity entity) {
        return ApoliSharedMixinValues.CURRENT_PUSHING_FLUID.get() != NeoForgeMod.LAVA_TYPE.value()
            && PowerHolderComponent.hasPower(entity, IgnoreWaterPower.class)
            && !entity.isInLava();
    }

    public static boolean isNonIgnoredFluidEntry(Object2DoubleMap.Entry<FluidType> entry) {
        return entry.getKey() == NeoForgeMod.LAVA_TYPE.value() && entry.getDoubleValue() > 0.0D;
    }

}

package io.github.apace100.apoli.power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.util.ApoliSharedMixinValues;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForgeMod;

public class IgnoreWaterPower extends Power {

    public IgnoreWaterPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    /**
     * Checks whether fluid pushing should be suppressed for {@code entity}.
     * {@link ApoliSharedMixinValues#CURRENT_PUSHING_FLUID} is checked because
     * {@code EntityFluidInteraction#update} resets tracked fluid heights before testing push predicates.
     */
    public static boolean shouldIgnoreFluidPush(Entity entity) {
        return ApoliSharedMixinValues.CURRENT_PUSHING_FLUID.get() != NeoForgeMod.LAVA_TYPE.value()
            && PowerHolderComponent.hasPower(entity, IgnoreWaterPower.class)
            && !entity.isInLava();
    }

}

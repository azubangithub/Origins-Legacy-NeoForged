package io.github.apace100.apoli.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.apace100.apoli.access.ModifiableFoodEntity;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ModifyFoodPower;
import io.github.apace100.apoli.util.ApoliSharedMixinValues;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {
    @WrapOperation(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
    private void apoli$storeSharedStack(FoodData instance, FoodProperties foodProperties, Operation<Void> original, @Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) ItemStack stack) {
        List<ModifyFoodPower> mfps = ((ModifiableFoodEntity) entity).getCurrentModifyFoodPowers();
        if (mfps == null || mfps.isEmpty()) {
            mfps = PowerHolderComponent.getPowers(entity, ModifyFoodPower.class).stream().filter(mfp -> mfp.doesApply(stack)).toList();
        }

        ApoliSharedMixinValues.CURRENT_STACK.set(stack);

        var foodModifiers = mfps.stream().flatMap(p -> p.getFoodModifiers().stream()).toList();
        var saturationModifiers = mfps.stream().flatMap(p -> p.getSaturationModifiers().stream()).toList();

        FoodProperties effectiveProperties = foodProperties;
        boolean modified = !foodModifiers.isEmpty() || !saturationModifiers.isEmpty();
        if (modified) {
            int newNutrition = (int) ModifierUtil.applyModifiers(entity, foodModifiers, foodProperties.nutrition());
            float newSaturation = (float) ModifierUtil.applyModifiers(entity, saturationModifiers, foodProperties.saturation());
            effectiveProperties = new FoodProperties(newNutrition, newSaturation, foodProperties.canAlwaysEat());
        }

        original.call(instance, effectiveProperties);

        if (!entity.level().isClientSide) {
            mfps.stream().filter(p -> p.doesApply(stack)).forEach(ModifyFoodPower::eat);
        }

        if (modified && entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetHealthPacket(
                entity.getHealth(), instance.getFoodLevel(), instance.getSaturationLevel()));
        }

        ApoliSharedMixinValues.CURRENT_STACK.remove();
    }
}

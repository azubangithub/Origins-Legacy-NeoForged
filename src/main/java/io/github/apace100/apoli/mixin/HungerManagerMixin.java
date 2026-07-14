package io.github.apace100.apoli.mixin;

import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;

// Food/saturation modification logic has been moved to PlayerEntityMixin.apoli$storeSharedStack.
// The previous injections into the private FoodData.add() method were silently failing.
@Mixin(FoodData.class)
public class HungerManagerMixin {
}

package io.github.apace100.calio.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.apace100.calio.Calio;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class CustomNonItalicNameItemMixin {
    @ModifyExpressionValue(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z", ordinal = 0))
    private boolean hasCustomNameWhichIsItalic(boolean original) {
        return original && !Calio.hasNonItalicName((ItemStack) (Object) this);
    }
}

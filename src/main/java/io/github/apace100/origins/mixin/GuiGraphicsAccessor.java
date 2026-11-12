package io.github.apace100.origins.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {
    @Invoker("setTooltipForNextFrameInternal")
    void callSetTooltipForNextFrameInternal(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, @Nullable Identifier sprite, boolean focused);
}

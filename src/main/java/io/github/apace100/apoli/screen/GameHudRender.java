package io.github.apace100.apoli.screen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public interface GameHudRender {

    List<GameHudRender> HUD_RENDERS = new ArrayList<>();

    void render(GuiGraphics context, float tickDelta);
}
package io.github.apace100.apoli.screen;

import io.github.apace100.apoli.power.OverlayPower;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.TriState;

import java.util.function.Function;

/**
 * Client-only renderer for {@link OverlayPower} to keep client GUI types out of server classloading.
 */
public final class OverlayPowerRenderer {

    private static final Function<ResourceLocation, RenderType> GUI_NAUSEA_OVERLAY = Util.memoize(texture ->
        RenderType.create(
            "apoli_overlay_nausea",
            1536,
            RenderPipelines.GUI_NAUSEA_OVERLAY,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(texture, TriState.DEFAULT, false))
                .createCompositeState(false)
        )
    );

    private OverlayPowerRenderer() {
    }

    public static void render(OverlayPower power, GuiGraphics guiGraphics) {
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        ResourceLocation texture = power.getTexture();
        float strength = power.getStrength();
        float red = power.getRed();
        float green = power.getGreen();
        float blue = power.getBlue();

        if (power.getDrawMode() == OverlayPower.DrawMode.NAUSEA) {
            float scale = Mth.lerp(strength, 2.0f, 1.0f);
            int color = ARGB.colorFromFloat(1.0F, red * strength, green * strength, blue * strength);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(width / 2.0f, height / 2.0f, 0.0f);
            guiGraphics.pose().scale(scale, scale, scale);
            guiGraphics.pose().translate(-width / 2.0f, -height / 2.0f, 0.0f);
            guiGraphics.blit(GUI_NAUSEA_OVERLAY, texture, 0, 0, 0f, 0f, width, height, width, height, color);
            guiGraphics.pose().popPose();
        } else {
            int color = ARGB.colorFromFloat(strength, red, green, blue);
            guiGraphics.blit(RenderType::guiTexturedOverlay, texture, 0, 0, 0f, 0f, width, height, width, height, color);
        }
    }
}

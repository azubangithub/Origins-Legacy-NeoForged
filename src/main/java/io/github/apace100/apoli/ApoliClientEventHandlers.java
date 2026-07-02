package io.github.apace100.apoli;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.OverlayPower;
import io.github.apace100.apoli.screen.GameHudRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = io.github.apace100.origins.Origins.MODID, value = Dist.CLIENT)
public class ApoliClientEventHandlers {

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        
        boolean hudHidden = minecraft.options.hideGui;
        boolean thirdPerson = !minecraft.options.getCameraType().isFirstPerson();
        
        if (minecraft.getCameraEntity() != null) {
            PowerHolderComponent.withPower(minecraft.getCameraEntity(), OverlayPower.class, p -> {
                if(p.getDrawPhase() != OverlayPower.DrawPhase.BELOW_HUD) {
                    return false;
                }
                if(hudHidden && p.doesHideWithHud()) {
                    return false;
                }
                if(thirdPerson && !p.shouldBeVisibleInThirdPerson()) {
                    return false;
                }
                return true;
            }, p -> p.render());
        }

        for(GameHudRender hudRender : GameHudRender.HUD_RENDERS) {
            hudRender.render(guiGraphics, event.getPartialTick().getGameTimeDeltaTicks());
        }
    }
}

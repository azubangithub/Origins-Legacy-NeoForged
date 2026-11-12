package io.github.apace100.origins;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.Minecraft;

@Mod(value = Origins.MODID, dist = Dist.CLIENT)
public class OriginsClient {

    public static boolean isServerRunningOrigins = false;

    public static boolean queueOpenActiveScreen = false;
    public static boolean showDirtBackground = false;

    public static final net.minecraft.client.KeyMapping.Category ORIGINS_CATEGORY = new net.minecraft.client.KeyMapping.Category(Origins.identifier("origins"));

    public static net.minecraft.client.KeyMapping usePrimaryActivePowerKeybind;
    public static net.minecraft.client.KeyMapping useSecondaryActivePowerKeybind;
    public static net.minecraft.client.KeyMapping viewCurrentOriginKeybind;

    public OriginsClient(net.neoforged.bus.api.IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(this::onRegisterKeyMappings);
        modEventBus.addListener(this::onRegisterRenderers);
        modEventBus.addListener(OriginsClient::onClientSetup);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void onRegisterRenderers(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(io.github.apace100.origins.registry.ModEntities.ENDERIAN_PEARL.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
    }

    private void onRegisterKeyMappings(net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent event) {
        event.registerCategory(ORIGINS_CATEGORY);
        usePrimaryActivePowerKeybind = new net.minecraft.client.KeyMapping("key.origins.primary_active", com.mojang.blaze3d.platform.InputConstants.Type.KEYBOARD, com.mojang.blaze3d.platform.InputConstants.KEY_G, ORIGINS_CATEGORY);
        useSecondaryActivePowerKeybind = new net.minecraft.client.KeyMapping("key.origins.secondary_active", com.mojang.blaze3d.platform.InputConstants.Type.KEYBOARD, com.mojang.blaze3d.platform.InputConstants.KEY_V, ORIGINS_CATEGORY);
        viewCurrentOriginKeybind = new net.minecraft.client.KeyMapping("key.origins.view_origin", com.mojang.blaze3d.platform.InputConstants.Type.KEYBOARD, com.mojang.blaze3d.platform.InputConstants.KEY_O, ORIGINS_CATEGORY);
        
        event.register(usePrimaryActivePowerKeybind);
        event.register(useSecondaryActivePowerKeybind);
        event.register(viewCurrentOriginKeybind);
        
        io.github.apace100.apoli.ApoliClient.registerPowerKeybinding("key.origins.primary_active", usePrimaryActivePowerKeybind);
        io.github.apace100.apoli.ApoliClient.registerPowerKeybinding("key.origins.secondary_active", useSecondaryActivePowerKeybind);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        Origins.LOGGER.info("Origins client setup on NeoForge");
        io.github.apace100.apoli.ApoliClient.init();
    }

    public void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && queueOpenActiveScreen) {
            queueOpenActiveScreen = false;
            ArrayList<io.github.apace100.origins.origin.OriginLayer> layers = new ArrayList<>();
            io.github.apace100.origins.component.OriginComponent component = io.github.apace100.origins.registry.ModComponents.get(client.player);
            io.github.apace100.origins.origin.OriginLayers.getLayers().forEach(layer -> {
                if(layer.isEnabled() && !component.hasOrigin(layer)) {
                    layers.add(layer);
                }
            });
            Collections.sort(layers);
            client.gui.setScreen(new io.github.apace100.origins.screen.ChooseOriginScreen(layers, 0, showDirtBackground));
        }

        while (viewCurrentOriginKeybind.consumeClick()) {
            if (!(client.gui.screen() instanceof io.github.apace100.origins.screen.ViewOriginScreen)) {
                client.gui.setScreen(new io.github.apace100.origins.screen.ViewOriginScreen());
            }
        }
    }
}

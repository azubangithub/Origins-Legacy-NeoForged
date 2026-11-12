package io.github.apace100.origins.networking;

import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.OriginsClient;
import io.github.apace100.origins.badge.Badge;
import io.github.apace100.origins.badge.BadgeManager;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.integration.OriginDataLoadedCallback;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import io.github.apace100.origins.screen.ChooseOriginScreen;
import io.github.apace100.origins.screen.WaitForNextLayerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ModPacketsS2C {

    public static void receiveOriginConfirmation(ConfirmOriginPacket packet, IPayloadContext context) {
        ResourceLocation layerId = packet.layerId();
        ResourceLocation originId = packet.originId();
        context.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            if (client.player == null) return;
            OriginLayer layer = OriginLayers.getLayer(layerId);
            Origin origin = OriginRegistry.get(originId);
            OriginComponent component = ModComponents.get(client.player);
            component.setOrigin(layer, origin);
            if(client.screen instanceof WaitForNextLayerScreen screen) {
                screen.openSelection();
            }
        });
    }

    public static void openOriginScreen(OpenOriginScreenPacket packet, IPayloadContext context) {
        boolean showDirtBackground = packet.showDirtBackground();
        context.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            if (client.player == null) {
                OriginsClient.queueOpenActiveScreen = true;
                OriginsClient.showDirtBackground = showDirtBackground;
                return;
            }
            ArrayList<OriginLayer> layers = new ArrayList<>();
            OriginComponent component = ModComponents.get(client.player);
            OriginLayers.getLayers().forEach(layer -> {
                if(layer.isEnabled() && !component.hasOrigin(layer)) {
                    layers.add(layer);
                }
            });
            Collections.sort(layers);
            client.setScreen(new ChooseOriginScreen(layers, 0, showDirtBackground));
        });
    }

    public static void receiveOriginList(OriginListPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                OriginsClient.isServerRunningOrigins = true;
                OriginRegistry.reset();
                packet.origins().forEach(OriginRegistry::register);
            } catch (Exception e) {
                Origins.LOGGER.error(e.getMessage(), e);
            }
        });
    }

    public static void receiveLayerList(LayerListPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                OriginLayers.clear();
                packet.layers().forEach(OriginLayers::add);
                OriginDataLoadedCallback.EVENT.invoker().onDataLoaded(true);
            } catch (Exception e) {
                Origins.LOGGER.error(e.getMessage(), e);
            }
        });
    }

    public static void receiveBadgeList(BadgeListPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                BadgeManager.clear();
                for(Map.Entry<ResourceLocation, List<Badge>> badgeEntry : packet.badges().entrySet()) {
                    for(Badge badge : badgeEntry.getValue()) {
                        BadgeManager.putPowerBadge(badgeEntry.getKey(), badge);
                    }
                }
            } catch (Exception e) {
                Origins.LOGGER.error(e.getMessage(), e);
            }
        });
    }

    public static void receivePowersAndOrigins(PowersAndOriginsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            PowerTypeRegistry.clear();
            packet.powers().factories().forEach(PowerTypeRegistry::register);
            try {
                OriginsClient.isServerRunningOrigins = true;
                OriginRegistry.reset();
                packet.origins().origins().forEach(OriginRegistry::register);
            } catch (Exception e) {
                Origins.LOGGER.error(e.getMessage(), e);
            }
        });
    }

    public static void receiveOriginComponentSync(SyncOriginComponentPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            net.minecraft.world.entity.Entity entity = context.player().level().getEntity(packet.entityId());
            if (entity == null && packet.entityId() == context.player().getId()) {
                entity = context.player();
            }
            if (entity instanceof net.minecraft.world.entity.player.Player player) {
                ModComponents.maybeGet(player).ifPresent(component -> {
                    if (component instanceof io.github.apace100.origins.component.PlayerOriginComponent poc) {
                        poc.deserializeNBT(context.player().level().registryAccess(), packet.componentNbt());
                    }
                });
            }
        });
    }
}

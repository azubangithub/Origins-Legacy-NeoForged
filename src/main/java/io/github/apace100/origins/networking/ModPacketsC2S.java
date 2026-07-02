package io.github.apace100.origins.networking;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Random;

public class ModPacketsC2S {

    public static void register() {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ModPacketsC2S::onPlayerJoin);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ModPacketsC2S::onPlayerRespawn);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ModPacketsC2S::onPlayerChangedDimension);
    }

    private static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sync(player);
        }
    }

    private static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sync(player);
        }
    }

    private static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sync(player);
        }
    }

    private static void sync(ServerPlayer player) {
        // Sync registries first so the client knows what origins/layers exist
        PacketDistributor.sendToPlayer(player, OriginListPacket.fromOriginsMap(OriginRegistry.get()));
        PacketDistributor.sendToPlayer(player, new LayerListPacket(new java.util.ArrayList<>(OriginLayers.getLayers())));
        
        // Sync badges
        io.github.apace100.origins.badge.BadgeManager.sync(player);
        
        // Sync player state (OriginComponent also syncs PowerHolderComponent)
        OriginComponent component = ModComponents.get(player);
        component.sync();

        // Open origin selection screen if the player doesn't have all origins
        if (!component.hasAllOrigins()) {
            PacketDistributor.sendToPlayer(player, new OpenOriginScreenPacket(true));
        }
    }

    public static void chooseOrigin(ChooseOriginPacket packet, IPayloadContext context) {
        ResourceLocation originId = packet.originId();
        ResourceLocation layerId = packet.layerId();
        
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer playerEntity)) return;
            OriginComponent component = ModComponents.get(playerEntity);
            OriginLayer layer = OriginLayers.getLayer(layerId);
            if(!component.hasAllOrigins() && !component.hasOrigin(layer)) {
                if(originId != null) {
                    Origin origin = OriginRegistry.get(originId);
                    if(origin.isChoosable() && layer.contains(origin, playerEntity)) {
                        boolean hadOriginBefore = component.hadOriginBefore();
                        boolean hadAllOrigins = component.hasAllOrigins();
                        component.setOrigin(layer, origin);
                        component.checkAutoChoosingLayers(playerEntity, false);
                        component.sync();
                        if(component.hasAllOrigins() && !hadAllOrigins) {
                            OriginComponent.onChosen(playerEntity, hadOriginBefore);
                        }
                        Origins.LOGGER.info("Player " + playerEntity.getDisplayName().getString() + " chose Origin: " + originId + ", for layer: " + layerId);
                    } else {
                        Origins.LOGGER.info("Player " + playerEntity.getDisplayName().getString() + " tried to choose unchoosable Origin for layer " + layerId + ": " + originId + ".");
                        component.setOrigin(layer, Origin.EMPTY);
                    }
                    confirmOrigin(playerEntity, layer, component.getOrigin(layer));
                    component.sync();
                } else {
                    Origins.LOGGER.warn("Player " + playerEntity.getDisplayName().getString() + " chose unknown origin: " + originId);
                }
            } else {
                Origins.LOGGER.warn("Player " + playerEntity.getDisplayName().getString() + " tried to choose origin for layer " + layerId + " while having one already.");
            }
        });
    }

    public static void chooseRandomOrigin(ChooseRandomOriginPacket packet, IPayloadContext context) {
        ResourceLocation layerId = packet.layerId();
        
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer playerEntity)) return;
            OriginComponent component = ModComponents.get(playerEntity);
            OriginLayer layer = OriginLayers.getLayer(layerId);
            if(!component.hasAllOrigins() && !component.hasOrigin(layer)) {
                List<ResourceLocation> randomOrigins = layer.getRandomOrigins(playerEntity);
                if(layer.isRandomAllowed() && randomOrigins.size() > 0) {
                    ResourceLocation randomOrigin = randomOrigins.get(new Random().nextInt(randomOrigins.size()));
                    Origin origin = OriginRegistry.get(randomOrigin);
                    boolean hadOriginBefore = component.hadOriginBefore();
                    boolean hadAllOrigins = component.hasAllOrigins();
                    component.setOrigin(layer, origin);
                    component.checkAutoChoosingLayers(playerEntity, false);
                    component.sync();
                    if(component.hasAllOrigins() && !hadAllOrigins) {
                        OriginComponent.onChosen(playerEntity, hadOriginBefore);
                    }
                    Origins.LOGGER.info("Player " + playerEntity.getDisplayName().getString() + " was randomly assigned the following Origin: " + randomOrigin + ", for layer: " + layerId);
                } else {
                    Origins.LOGGER.info("Player " + playerEntity.getDisplayName().getString() + " tried to choose a random Origin for layer " + layerId + ", which is not allowed!");
                    component.setOrigin(layer, Origin.EMPTY);
                }
                confirmOrigin(playerEntity, layer, component.getOrigin(layer));
                component.sync();
            } else {
                Origins.LOGGER.warn("Player " + playerEntity.getDisplayName().getString() + " tried to choose origin for layer " + layerId + " while having one already.");
            }
        });
    }

    private static void confirmOrigin(ServerPlayer player, OriginLayer layer, Origin origin) {
        PacketDistributor.sendToPlayer(player, new ConfirmOriginPacket(layer.getIdentifier(), origin.getIdentifier()));
    }
}

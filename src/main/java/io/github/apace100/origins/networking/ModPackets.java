package io.github.apace100.origins.networking;

import io.github.apace100.origins.Origins;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPackets {

    public static final ResourceLocation HANDSHAKE = Origins.identifier("handshake");

    public static final CustomPacketPayload.Type<OpenOriginScreenPacket> OPEN_ORIGIN_SCREEN = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MODID, "open_origin_screen"));
    public static final CustomPacketPayload.Type<ChooseOriginPacket> CHOOSE_ORIGIN = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MODID, "choose_origin"));
    public static final CustomPacketPayload.Type<OriginListPacket> ORIGIN_LIST = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MODID, "origin_list"));
    public static final CustomPacketPayload.Type<LayerListPacket> LAYER_LIST = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MODID, "layer_list"));
    public static final CustomPacketPayload.Type<ChooseRandomOriginPacket> CHOOSE_RANDOM_ORIGIN = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Origins.MODID, "choose_random_origin"));
    public static final CustomPacketPayload.Type<ConfirmOriginPacket> CONFIRM_ORIGIN = new CustomPacketPayload.Type<>(Origins.identifier("confirm_origin"));
    public static final CustomPacketPayload.Type<BadgeListPacket> BADGE_LIST = new CustomPacketPayload.Type<>(Origins.identifier("badge_list"));
    public static final CustomPacketPayload.Type<PowersAndOriginsPacket> POWERS_AND_ORIGINS = new CustomPacketPayload.Type<>(Origins.identifier("powers_and_origins"));
    public static final CustomPacketPayload.Type<SyncOriginComponentPacket> SYNC_ORIGIN_COMPONENT = new CustomPacketPayload.Type<>(Origins.identifier("sync_origin_component"));

    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Origins.MODID);

        registrar.playToClient(OPEN_ORIGIN_SCREEN, OpenOriginScreenPacket.CODEC, (payload, context) -> ModPacketsS2C.openOriginScreen((OpenOriginScreenPacket) payload, context));
        registrar.playToServer(CHOOSE_ORIGIN, ChooseOriginPacket.CODEC, (payload, context) -> ModPacketsC2S.chooseOrigin((ChooseOriginPacket) payload, context));

        registrar.playToClient(ORIGIN_LIST, OriginListPacket.CODEC, (payload, context) -> ModPacketsS2C.receiveOriginList((OriginListPacket) payload, context));
        registrar.playToClient(LAYER_LIST, LayerListPacket.CODEC, (payload, context) -> ModPacketsS2C.receiveLayerList((LayerListPacket) payload, context));
        registrar.playToServer(CHOOSE_RANDOM_ORIGIN, ChooseRandomOriginPacket.CODEC, (payload, context) -> ModPacketsC2S.chooseRandomOrigin((ChooseRandomOriginPacket) payload, context));
        registrar.playToClient(CONFIRM_ORIGIN, ConfirmOriginPacket.CODEC, (payload, context) -> ModPacketsS2C.receiveOriginConfirmation((ConfirmOriginPacket) payload, context));
        registrar.playToClient(BADGE_LIST, BadgeListPacket.CODEC, (payload, context) -> ModPacketsS2C.receiveBadgeList((BadgeListPacket) payload, context));

        registrar.playToClient(POWERS_AND_ORIGINS, PowersAndOriginsPacket.CODEC, (payload, context) -> ModPacketsS2C.receivePowersAndOrigins((PowersAndOriginsPacket) payload, context));
        registrar.playToClient(SYNC_ORIGIN_COMPONENT, SyncOriginComponentPacket.CODEC, (payload, context) -> ModPacketsS2C.receiveOriginComponentSync((SyncOriginComponentPacket) payload, context));
    }
}

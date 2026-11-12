package io.github.apace100.calio.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Calio networking - ported to NeoForge.
 * Packet registration happens via RegisterPayloadHandlersEvent in CalioNetworkHandler.
 */
public class CalioNetworking {

    public static final CustomPacketPayload.Type<SyncDataObjectRegistryPacket<?>> SYNC_DATA_OBJECT_REGISTRY =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("calio", "sync_data_object_registry"));

    // Registration is handled by CalioNetworkHandler.onRegisterPayloads()
    public static void init() {
        // No-op on NeoForge - registration happens via event
    }
}

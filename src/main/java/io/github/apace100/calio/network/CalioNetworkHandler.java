package io.github.apace100.calio.network;

import io.github.apace100.calio.Calio;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = io.github.apace100.origins.Origins.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CalioNetworkHandler {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Calio.MODID);

        registrar.playToClient(
                (net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type) CalioNetworking.SYNC_DATA_OBJECT_REGISTRY,
                (net.minecraft.network.codec.StreamCodec) SyncDataObjectRegistryPacket.CODEC,
                (payload, context) -> CalioNetworkingClient.onDataObjectRegistrySync((SyncDataObjectRegistryPacket<?>) payload, context)
        );
    }
}

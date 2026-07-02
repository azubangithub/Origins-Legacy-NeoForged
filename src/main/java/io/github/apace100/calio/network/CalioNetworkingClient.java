package io.github.apace100.calio.network;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client-side Calio networking - ported from Fabric to NeoForge.
 */
@OnlyIn(Dist.CLIENT)
public class CalioNetworkingClient {

    /**
     * Handler for SyncDataObjectRegistryPacket on the client side.
     * Called by the NeoForge payload handler registered in CalioNetworkHandler.
     */
    public static void onDataObjectRegistrySync(
        SyncDataObjectRegistryPacket<?> packet,
        IPayloadContext context) {
        // Currently no-op in the original Fabric code (handler body was commented out)
        // TODO: Implement when DataObjectRegistry sync is needed
    }
}

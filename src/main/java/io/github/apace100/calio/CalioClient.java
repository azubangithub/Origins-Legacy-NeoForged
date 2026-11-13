package io.github.apace100.calio;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Calio client - ported to NeoForge.
 * Client networking receivers are registered via NeoForge event system.
 */
@OnlyIn(Dist.CLIENT)
public class CalioClient {
    // Client init handled by OriginsClient on NeoForge
    // CalioNetworkingClient receivers are registered in the payload handler event
}

package io.github.apace100.calio.resource;

/**
 * Ported to NeoForge - this interface is kept for API compatibility but
 * the implementation now works via NeoForge's AddServerReloadListenersEvent
 * instead of Fabric's entrypoint system.
 *
 * @deprecated Use NeoForge's AddServerReloadListenersEvent directly instead.
 */
@Deprecated
public interface OrderedResourceListenerInitializer {

    void registerResourceListeners(OrderedResourceListenerManager manager);
}

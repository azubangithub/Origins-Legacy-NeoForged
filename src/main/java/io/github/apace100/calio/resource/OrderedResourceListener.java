package io.github.apace100.calio.resource;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Ported to NeoForge - no longer uses Fabric's IdentifiableResourceReloadListener.
 * Instead, uses NeoForge's AddServerReloadListenersEvent for registration.
 * The ordering/dependency system is simplified — we register listeners in the correct
 * order manually since NeoForge doesn't have the same dependency graph system.
 *
 * @deprecated Use NeoForge's AddServerReloadListenersEvent directly.
 */
@Deprecated
public class OrderedResourceListener {

    public static final String ENTRYPOINT_KEY = "calio:ordered-resource-listener";

    /**
     * On NeoForge, we don't use Fabric entrypoints.
     * Origins.java will directly call registerResourceListeners() on known implementations.
     */
    public static void init() {
        // No-op on NeoForge — Origins handles registration directly
    }

    /**
     * Registration builder - maintains the API surface but simplified for NeoForge.
     * On NeoForge, the actual registration happens via AddServerReloadListenersEvent,
     * so this class just records the metadata for ordering.
     */
    public static class Registration {

        final Identifier id;
        final PreparableReloadListener resourceReloadListener;
        final Function<HolderLookup.Provider, PreparableReloadListener> reloadListenerProvider;
        final Set<Identifier> dependencies = new HashSet<>();
        final Set<Identifier> dependants = new HashSet<>();
        private boolean isCompleted;
        private final OrderedResourceListenerManager.Instance manager;

        Registration(OrderedResourceListenerManager.Instance manager, Identifier id, PreparableReloadListener listener) {
            this.id = id;
            this.manager = manager;
            this.resourceReloadListener = listener;
            this.reloadListenerProvider = null;
        }

        Registration(Identifier id, OrderedResourceListenerManager.Instance manager, Function<HolderLookup.Provider, PreparableReloadListener> listenerProvider) {
            this.id = id;
            this.manager = manager;
            this.resourceReloadListener = null;
            this.reloadListenerProvider = listenerProvider;
        }

        public Registration after(String identifier) {
            return after(Identifier.parse(identifier));
        }

        public Registration after(Identifier identifier) {
            if(isCompleted) {
                throw new IllegalStateException(
                    "Can't add a resource reload listener registration dependency after it was completed.");
            }
            dependencies.add(identifier);
            return this;
        }

        public Registration before(String identifier) {
            return before(Identifier.parse(identifier));
        }

        public Registration before(Identifier identifier) {
            if(isCompleted) {
                throw new IllegalStateException(
                    "Can't add a resource reload listener registration dependant after it was completed.");
            }
            dependants.add(identifier);
            return this;
        }

        public void complete() {
            isCompleted = true;
            manager.add(this);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(id.toString());
            builder.append("{depends_on=[");
            boolean first = true;
            for (Identifier afterId : dependencies) {
                if(!first) {
                    builder.append(',');
                }
                builder.append(afterId);
                first = false;
            }
            builder.append("]}");
            return builder.toString();
        }
    }
}

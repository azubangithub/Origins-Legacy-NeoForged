package io.github.apace100.calio.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.*;

/**
 * Allows registering data resource listeners in a specified order, to prevent problems
 * due to mod loading order and inter-mod data dependencies.
 *
 * On NeoForge, the actual registration happens via AddServerReloadListenersEvent.
 * This class collects and orders the listeners, then they can be retrieved in order.
 *
 * @deprecated Use NeoForge's AddServerReloadListenersEvent with ordering directly.
 */
@Deprecated
public final class OrderedResourceListeners {

    private static final Set<ResourceLocation> finalizedRegistrations = new HashSet<>();
    private static final HashMap<ResourceLocation, Registration> registrations = new HashMap<>();
    private static final List<PreparableReloadListener> orderedListeners = new ArrayList<>();

    /**
     * Get all registered listeners in their resolved order.
     */
    public static List<PreparableReloadListener> getOrderedListeners() {
        return orderedListeners;
    }

    public static Registration register(ResourceLocation id, PreparableReloadListener resourceReloadListener) {
        Registration registration = new Registration(id, resourceReloadListener);
        return registration;
    }

    private static void completeRegistration(Registration registration) {
        registration.afterSet.removeAll(finalizedRegistrations);
        if(registration.afterSet.size() == 0) {
            finalizeRegistration(registration);
        } else {
            registrations.put(registration.id, registration);
        }
    }

    private static void finalizeRegistration(Registration registration) {
        orderedListeners.add(registration.resourceReloadListener);
        ResourceLocation id = registration.id;
        finalizedRegistrations.add(id);
        registrations.remove(id);
        Set<ResourceLocation> finishedOnes = new HashSet<>();
        for(Map.Entry<ResourceLocation, Registration> registrationEntry : registrations.entrySet()) {
            registrationEntry.getValue().afterSet.remove(id);
            if(registrationEntry.getValue().afterSet.size() == 0) {
                finishedOnes.add(registrationEntry.getKey());
            }
        }
        for(ResourceLocation finished : finishedOnes) {
            finalizeRegistration(registrations.get(finished));
        }
    }

    public static class Registration {

        private final ResourceLocation id;
        private final PreparableReloadListener resourceReloadListener;
        private final Set<ResourceLocation> afterSet = new HashSet<>();
        private final Set<ResourceLocation> beforeSet = new HashSet<>();
        private boolean isCompleted;

        private Registration(ResourceLocation id, PreparableReloadListener resourceReloadListener) {
            this.id = id;
            this.resourceReloadListener = resourceReloadListener;
        }

        public Registration after(ResourceLocation identifier) {
            if(isCompleted) {
                throw new IllegalStateException(
                    "Can't add a resource reload listener registration dependency after it was completed.");
            }
            afterSet.add(identifier);
            return this;
        }

        public Registration before(ResourceLocation identifier) {
            if(isCompleted) {
                throw new IllegalStateException(
                    "Can't add a resource reload listener registration dependency after it was completed.");
            }
            beforeSet.add(identifier);
            return this;
        }

        public void complete() {
            completeRegistration(this);
            isCompleted = true;
        }
    }
}

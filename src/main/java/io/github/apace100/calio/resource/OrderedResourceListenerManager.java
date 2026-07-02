package io.github.apace100.calio.resource;

import com.google.common.collect.Lists;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Ported to NeoForge. Replaces Fabric's IdentifiableResourceReloadListener
 * and ResourceManagerHelper with a simple ordered list that can be registered
 * via NeoForge's AddServerReloadListenersEvent.
 *
 * @deprecated Use NeoForge's AddServerReloadListenersEvent directly.
 */
@Deprecated
public class OrderedResourceListenerManager {

    /** Stores the ordered list of reload listener providers after finishing registration. */
    private static final List<Function<HolderLookup.Provider, PreparableReloadListener>> registryBasedReloadListenerProviders = new ArrayList<>();

    /** Stores directly-registered reload listeners (not registry-based). */
    private static final List<PreparableReloadListener> directReloadListeners = new ArrayList<>();

    public static List<Function<HolderLookup.Provider, PreparableReloadListener>> getRegistryBasedReloadListenerProviders() {
        return registryBasedReloadListenerProviders;
    }

    public static List<PreparableReloadListener> getDirectReloadListeners() {
        return directReloadListeners;
    }

    private final Instance registryBasedInstance = new Instance(
        listener -> directReloadListeners.add(listener),
        provider -> registryBasedReloadListenerProviders.add(provider)
    );

    private final HashMap<String, Instance> instances = new HashMap<>();

    public OrderedResourceListenerManager() {}

    public OrderedResourceListener.Registration register(net.minecraft.server.packs.PackType resourceType, ResourceLocation id, PreparableReloadListener resourceReloadListener) {
        Instance inst = instances.computeIfAbsent(resourceType.toString(), rt -> new Instance(
            listener -> directReloadListeners.add(listener)
        ));
        return new OrderedResourceListener.Registration(inst, id, resourceReloadListener);
    }

    public OrderedResourceListener.Registration registerWithRegistries(ResourceLocation id, Function<HolderLookup.Provider, PreparableReloadListener> reloadListener) {
        return new OrderedResourceListener.Registration(id, registryBasedInstance, reloadListener);
    }

    public void finishRegistration() {
        for(Instance inst : instances.values()) {
            inst.finish();
        }
        registryBasedInstance.finish();
    }

    static class Instance {
        private final HashMap<ResourceLocation, OrderedResourceListener.Registration> registrations = new HashMap<>();
        private final HashMap<Integer, List<ResourceLocation>> sortedMap = new HashMap<>();
        private int maxIndex = 0;

        private final Consumer<PreparableReloadListener> registrationMethod;
        private final Consumer<Function<HolderLookup.Provider, PreparableReloadListener>> registrationProviderMethod;

        private Instance(Consumer<PreparableReloadListener> registrationMethod) {
            this.registrationMethod = registrationMethod;
            this.registrationProviderMethod = null;
        }

        private Instance(Consumer<PreparableReloadListener> registrationMethod, Consumer<Function<HolderLookup.Provider, PreparableReloadListener>> registrationProviderMethod) {
            this.registrationMethod = registrationMethod;
            this.registrationProviderMethod = registrationProviderMethod;
        }

        void add(OrderedResourceListener.Registration registration) {
            registrations.put(registration.id, registration);
        }

        void finish() {
            prepareSetsAndSort();
            List<ResourceLocation> sortedList = new LinkedList<>();
            List<ResourceLocation> nextListeners;
            while(!(nextListeners = copy(getRegistrations(0))).isEmpty()) {
                sortedList.addAll(nextListeners);
                sortedMap.remove(0);
                for(int i = 1; i <= maxIndex; i++) {
                    for(ResourceLocation regId : copy(getRegistrations(i))) {
                        OrderedResourceListener.Registration registration = registrations.get(regId);
                        int before = registration.dependencies.size();
                        nextListeners.forEach(registration.dependencies::remove);
                        update(registration, before);
                    }
                }
            }
            if(!sortedMap.isEmpty()) {
                StringBuilder errorBuilder = new StringBuilder("Couldn't resolve ordered resource listener dependencies. Unsolved:");
                for(int i = 0; i <= maxIndex; i++) {
                    if(!getRegistrations(i).isEmpty()) {
                        errorBuilder.append("\t").append(i).append(" dependencies:");
                        for(ResourceLocation id : getRegistrations(i)) {
                            OrderedResourceListener.Registration registration = registrations.get(id);
                            errorBuilder.append("\t\t").append(registration.toString());
                            if (registration.resourceReloadListener != null)
                                registrationMethod.accept(registration.resourceReloadListener);
                            else if (registrationProviderMethod != null)
                                registrationProviderMethod.accept(registration.reloadListenerProvider);
                        }
                    }
                }
                throw new RuntimeException(errorBuilder.toString());
            } else {
                for(ResourceLocation id : sortedList) {
                    OrderedResourceListener.Registration registration = registrations.get(id);
                    if (registration.resourceReloadListener != null)
                        registrationMethod.accept(registration.resourceReloadListener);
                    else if (registrationProviderMethod != null)
                        registrationProviderMethod.accept(registration.reloadListenerProvider);
                }
            }
        }

        private void prepareSetsAndSort() {
            for (OrderedResourceListener.Registration reg : registrations.values()) {
                reg.dependencies.removeIf(id -> !registrations.containsKey(id));
                reg.dependants.forEach(id -> {
                    if(registrations.containsKey(id)) {
                        registrations.get(id).dependencies.add(reg.id);
                    }
                });
            }
            registrations.values().forEach(this::sortIntoMap);
        }

        private void sortIntoMap(OrderedResourceListener.Registration registration) {
            int index = registration.dependencies.size();
            List<ResourceLocation> list = sortedMap.computeIfAbsent(index, i -> new LinkedList<>());
            list.add(registration.id);
            if(index > maxIndex) {
                maxIndex = index;
            }
        }

        private void update(OrderedResourceListener.Registration registration, int indexBefore) {
            int index = registration.dependencies.size();
            if(index == indexBefore) {
                return;
            }
            List<ResourceLocation> regs = getRegistrations(indexBefore);
            regs.remove(registration.id);
            if(regs.isEmpty()) {
                sortedMap.remove(indexBefore);
            }
            List<ResourceLocation> list = sortedMap.computeIfAbsent(index, i -> new LinkedList<>());
            list.add(registration.id);
        }

        private List<ResourceLocation> getRegistrations(int index) {
            return sortedMap.getOrDefault(index, new LinkedList<>());
        }
    }

    private static <T> List<T> copy(List<T> list) {
        return Lists.newLinkedList(list);
    }
}

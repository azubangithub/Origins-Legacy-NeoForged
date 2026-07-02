package io.github.apace100.calio.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.apace100.calio.resource.OrderedResourceListenerManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Ported to NeoForge - replaced IdentifiableResourceReloadListener with PreparableReloadListener.
 */
@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {
    @Unique private final List<PreparableReloadListener> calio$registryListeners = new ArrayList<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void calio$initRegistryBasedListeners(RegistryAccess.Frozen registryAccess, FeatureFlagSet enabledFeatures, Commands.CommandSelection commandSelection, int functionCompilationLevel, CallbackInfo ci) {
        for (Function<HolderLookup.Provider, PreparableReloadListener> provider : OrderedResourceListenerManager.getRegistryBasedReloadListenerProviders()) {
            calio$registryListeners.add(provider.apply(registryAccess));
        }
    }

    @ModifyReturnValue(method = "listeners", at = @At("RETURN"))
    private List<PreparableReloadListener> calio$appendRegistryListeners(List<PreparableReloadListener> original) {
        var modifiable = new ArrayList<>(original);
        modifiable.addAll(this.calio$registryListeners);

        return modifiable;
    }
}

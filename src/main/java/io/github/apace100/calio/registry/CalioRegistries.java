package io.github.apace100.calio.registry;

import com.mojang.serialization.Codec;
import io.github.apace100.calio.Calio;
import io.github.apace100.calio.CodeTriggerCriterion;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * NeoForge DeferredRegister wrappers for Calio's registrations.
 */
public class CalioRegistries {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Calio.MODID);

    public static final DeferredRegister<net.minecraft.advancements.CriterionTrigger<?>> TRIGGER_TYPES =
        DeferredRegister.create(Registries.TRIGGER_TYPE, Calio.MODID);

    // Data components
    public static final Supplier<DataComponentType<Boolean>> NON_ITALIC_NAME =
        DATA_COMPONENT_TYPES.register("non_italic_name", () ->
            DataComponentType.<Boolean>builder()
                .persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL)
                .build()
        );

    public static final Supplier<DataComponentType<Boolean>> HAS_ADDITIONAL_ATTRIBUTES =
        DATA_COMPONENT_TYPES.register("has_additional_attributes", () ->
            DataComponentType.<Boolean>builder()
                .persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL)
                .build()
        );

    // Criterion triggers
    public static final Supplier<CodeTriggerCriterion> CODE_TRIGGER =
        TRIGGER_TYPES.register("code_trigger", () -> CodeTriggerCriterion.INSTANCE);

    /**
     * Called from Origins mod constructor to register all Calio deferred registries.
     */
    public static void register(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
        TRIGGER_TYPES.register(modEventBus);

        // Set the static references in Calio for backward compatibility
        // These will be populated after registration fires
        modEventBus.addListener(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent.class, event -> {
            Calio.NON_ITALIC_NAME = NON_ITALIC_NAME.get();
            Calio.HAS_ADDITIONAL_ATTRIBUTES = HAS_ADDITIONAL_ATTRIBUTES.get();
        });
    }
}

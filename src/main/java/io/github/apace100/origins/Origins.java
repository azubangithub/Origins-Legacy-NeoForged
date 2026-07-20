package io.github.apace100.origins;

import com.mojang.logging.LogUtils;
import io.github.apace100.origins.badge.BadgeManager;
import io.github.apace100.origins.networking.ModPackets;
import io.github.apace100.origins.networking.ModPacketsC2S;
import io.github.apace100.origins.registry.ModBlocks;
import io.github.apace100.origins.registry.ModComponents;
import io.github.apace100.origins.registry.ModEntities;
import io.github.apace100.origins.registry.ModItems;
import io.github.apace100.origins.registry.ModLoot;
import io.github.apace100.origins.util.Scheduler;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(Origins.MODID)
public class Origins {

    public static final String MODID = "origins";
    public static final String LEGACY_MODID = "origins_legacy";

    public static String VERSION = "";
    public static int[] SEMVER;
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Scheduler SCHEDULER;

    private static IEventBus modBus;

    public Origins(IEventBus modEventBus, ModContainer modContainer) {
        modBus = modEventBus;

        // Parse version
        ModList.get().getModContainerById(MODID).ifPresent(container -> {
            VERSION = container.getModInfo().getVersion().toString();
            if (VERSION.contains("+")) {
                VERSION = VERSION.split("\\+")[0];
            }
            if (VERSION.contains("-")) {
                VERSION = VERSION.split("-")[0];
            }
            String[] splitVersion = VERSION.split("\\.");
            SEMVER = new int[splitVersion.length];
            for (int i = 0; i < SEMVER.length; i++) {
                SEMVER[i] = Integer.parseInt(splitVersion[i]);
            }
        });
        LOGGER.info("Origins " + VERSION + " is initializing on NeoForge. Have fun!");

        // Phase 2: Register Calio subsystem
        io.github.apace100.calio.registry.CalioRegistries.register(modBus);

        // Phase 3/4: Register Apoli subsystem
        io.github.apace100.apoli.registry.ApoliRegistries.register(modBus);
        io.github.apace100.apoli.Apoli.onInitialize(modBus);

        // Map origins:xxx powers to apoli:xxx powers
        io.github.apace100.apoli.util.NamespaceAlias.addAlias("origins", "apoli");

        // Register Origins resource listeners
        io.github.apace100.calio.resource.OrderedResourceListenerManager resourceManager = new io.github.apace100.calio.resource.OrderedResourceListenerManager();
        resourceManager.registerWithRegistries(identifier("origins"), io.github.apace100.origins.origin.OriginManager::new).complete();
        resourceManager.registerWithRegistries(identifier("origin_layers"), io.github.apace100.origins.origin.OriginLayers::new).complete();
        resourceManager.finishRegistration();

        // Phase 5: Register Origins DeferredRegisters
        ModBlocks.register(modBus);
        ModComponents.ATTACHMENT_TYPES.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);
        ModLoot.LOOT_CONDITION_TYPES.register(modBus);
        ModItems.register(modBus);

        // Register custom command argument types
        net.neoforged.neoforge.registries.DeferredRegister<net.minecraft.commands.synchronization.ArgumentTypeInfo<?, ?>> ORIGINS_ARG_TYPES = 
            net.neoforged.neoforge.registries.DeferredRegister.create(net.minecraft.core.registries.Registries.COMMAND_ARGUMENT_TYPE, MODID);
        ORIGINS_ARG_TYPES.register("layer", () -> 
            net.minecraft.commands.synchronization.ArgumentTypeInfos.registerByClass(
                io.github.apace100.origins.command.LayerArgumentType.class,
                net.minecraft.commands.synchronization.SingletonArgumentInfo.contextFree(io.github.apace100.origins.command.LayerArgumentType::layer)));
        ORIGINS_ARG_TYPES.register("origin", () -> 
            net.minecraft.commands.synchronization.ArgumentTypeInfos.registerByClass(
                io.github.apace100.origins.command.OriginArgumentType.class,
                net.minecraft.commands.synchronization.SingletonArgumentInfo.contextFree(io.github.apace100.origins.command.OriginArgumentType::origin)));
        ORIGINS_ARG_TYPES.register(modBus);

        modBus.addListener((net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.TOOLS_AND_UTILITIES) {
                event.accept(ModItems.ORB_OF_ORIGIN.get());
            }
        });


        // Register Origins-specific power factories
        io.github.apace100.origins.power.OriginsPowerTypes.register();

        // Phase 6: Register packet handlers
        modBus.addListener(ModPackets::register);

        // Phase 7: Register event listeners (server-side sync on join/respawn)
        ModPacketsC2S.register();

        // Phase 8: Initialize badge system
        BadgeManager.init();

        // Phase 9: Scheduler
        SCHEDULER = new Scheduler();

        // Phase 10: Register commands
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.RegisterCommandsEvent event) -> {
            io.github.apace100.origins.command.OriginCommand.register(event.getDispatcher());
        });

        // Touch registry classes to trigger static init
        ModEntities.register();

        LOGGER.info("Origins initialization complete.");
    }

    public static IEventBus getModBus() {
        return modBus;
    }

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static ResourceLocation legacy(String path) {
        return ResourceLocation.fromNamespaceAndPath(LEGACY_MODID, path);
    }
}


package io.github.apace100.apoli;

import io.github.apace100.apoli.command.PowerCommand;
import io.github.apace100.apoli.command.ResourceCommand;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.component.PowerHolderComponentImpl;
import io.github.apace100.apoli.component.StackPowerComponent;
import io.github.apace100.apoli.global.GlobalPowerSetLoader;
import io.github.apace100.apoli.networking.ModPackets;
import io.github.apace100.apoli.networking.ModPacketsC2S;
import io.github.apace100.apoli.power.PowerTypes;
import io.github.apace100.apoli.power.factory.PowerFactories;
import io.github.apace100.apoli.power.factory.action.BiEntityActions;
import io.github.apace100.apoli.power.factory.action.BlockActions;
import io.github.apace100.apoli.power.factory.action.EntityActions;
import io.github.apace100.apoli.power.factory.action.ItemActions;
import io.github.apace100.apoli.power.factory.condition.*;
import io.github.apace100.apoli.registry.ApoliClassData;
import io.github.apace100.apoli.util.*;
import io.github.apace100.apoli.util.modifier.ModifierOperations;
import io.github.apace100.calio.resource.OrderedResourceListenerInitializer;
import io.github.apace100.calio.resource.OrderedResourceListenerManager;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Apoli implements OrderedResourceListenerInitializer {

	public static ApoliConfig config;

	public static MinecraftServer server;

	public static final Scheduler SCHEDULER = new Scheduler();

	public static final String MODID = "apoli";
	public static final String LEGACY_MODID = "apoli_legacy";

	public static final Logger LOGGER = LogManager.getLogger(Apoli.class);
	public static String VERSION = "";
	public static int[] SEMVER;

	// public static final AbilitySource LEGACY_POWER_SOURCE = Pal.getAbilitySource(Apoli.identifier("power_source")); // MOCKED

	public static final boolean PERFORM_VERSION_CHECK = false;

	public static void onInitialize(net.neoforged.bus.api.IEventBus modBus) {
		// Register configs
		net.neoforged.fml.ModList.get().getModContainerById(io.github.apace100.origins.Origins.MODID).ifPresent(mc -> {
			mc.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, ApoliConfig.COMMON_SPEC, "apoli-common.toml");
			mc.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, ApoliConfigClient.CLIENT_SPEC, "apoli-client.toml");
		});
		ApoliConfigClient clientConfig = new ApoliConfigClient();
		config = clientConfig;

		ModPackets.init();
		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStartedEvent event) -> {
			server = event.getServer();
			clientConfig.syncFromSpec();
		});

		net.neoforged.fml.ModList.get().getModContainerById(io.github.apace100.origins.Origins.MODID).ifPresent(modContainer -> {
			VERSION = modContainer.getModInfo().getVersion().toString();
			if(VERSION.contains("+")) {
				VERSION = VERSION.split("\\+")[0];
			}
			if(VERSION.contains("-")) {
				VERSION = VERSION.split("-")[0];
			}
			String[] splitVersion = VERSION.split("\\.");
			SEMVER = new int[splitVersion.length];
			for(int i = 0; i < SEMVER.length; i++) {
				SEMVER[i] = Integer.parseInt(splitVersion[i]);
			}
		});

		ModPacketsC2S.register();

		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.RegisterCommandsEvent event) -> {
			PowerCommand.register(event.getDispatcher());
			ResourceCommand.register(event.getDispatcher());
		});

		ApoliClassData.registerAll();

		ModifierOperations.registerAll();

		PowerFactories.register();
		EntityConditions.register();
		BiEntityConditions.register();
		ItemConditions.register();
		BlockConditions.register();
		DamageConditions.register();
		FluidConditions.register();
		BiomeConditions.register();
		EntityActions.register();
		ItemActions.register();
		BlockActions.register();
		BiEntityActions.register();

		if (net.neoforged.fml.loading.FMLEnvironment.dist == net.neoforged.api.distmarker.Dist.CLIENT) {
			EntityConditionsClient.register();
			ItemConditionsClient.register();
		}
		EntityConditionsServer.register();
		ItemConditionsServer.register();

		// Wire up the OrderedResourceListenerManager (replaces Fabric entrypoint system)
		OrderedResourceListenerManager manager = new OrderedResourceListenerManager();
		// Register Apoli's own listeners (PowerTypes)
		new Apoli().registerResourceListeners(manager);
		// Register GlobalPowerSetLoader (depends on PowerTypes)
		manager.registerWithRegistries(identifier("global_powers"), GlobalPowerSetLoader::new)
			.after(identifier("powers")).complete();
		// Finalize — sorts listeners and populates the static lists the mixin reads from
		manager.finishRegistration();

		LOGGER.info("Apoli " + VERSION + " has initialized. Ready to power up your game!");
	}

	public static ResourceLocation identifier(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public static ResourceLocation legacy(String path) {
		return ResourceLocation.fromNamespaceAndPath(LEGACY_MODID, path);
	}

	@Override
	public void registerResourceListeners(OrderedResourceListenerManager manager) {
		manager.registerWithRegistries(identifier("powers"), PowerTypes::new).complete();
	}
}

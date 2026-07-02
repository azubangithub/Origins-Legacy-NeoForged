package io.github.apace100.origins.origin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.MultiJsonDataLoader;
import io.github.apace100.origins.Origins;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class OriginManager extends MultiJsonDataLoader {
	
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	private final HolderLookup.Provider provider;

	public OriginManager(HolderLookup.Provider provider) {
		super(GSON, "origins");
		this.provider = provider;
	}

	@Override
	protected void apply(Map<ResourceLocation, List<JsonElement>> loader, ResourceManager manager, ProfilerFiller profiler) {
		OriginRegistry.reset();
		AtomicBoolean hasConfigChanged = new AtomicBoolean(false);
		loader.forEach((id, jel) -> {
			jel.forEach(je -> {
				try {
					Origin origin = Origin.fromJson(id, je.getAsJsonObject(), this.provider);
					if(!OriginRegistry.contains(id)) {
						OriginRegistry.register(id, origin);
					} else {
						if(OriginRegistry.get(id).getLoadingPriority() < origin.getLoadingPriority()) {
							OriginRegistry.update(id, origin);
						}
					}
				} catch(Exception e) {
					Origins.LOGGER.error("There was a problem reading Origin file " + id.toString() + " (skipping): " + e.getMessage());
				}
			});
			if(OriginRegistry.contains(id)) {
				Origin origin = OriginRegistry.get(id);
				// TODO: Port config checks
			}
		});
		Origins.LOGGER.info("Finished loading layers from data files. Registry contains " + OriginRegistry.size() + " layers.");
	}

}

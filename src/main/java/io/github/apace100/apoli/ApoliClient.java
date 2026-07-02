package io.github.apace100.apoli;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.networking.ModPacketsS2C;
import io.github.apace100.apoli.networking.UseActivePowersPacket;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.condition.EntityConditionsClient;
import io.github.apace100.apoli.power.factory.condition.ItemConditionsClient;
import io.github.apace100.apoli.registry.ApoliClassDataClient;
import io.github.apace100.apoli.screen.GameHudRender;
import io.github.apace100.apoli.screen.PowerHudRenderer;
import io.github.apace100.apoli.util.ApoliConfigClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = io.github.apace100.origins.Origins.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ApoliClient  {

	public static boolean shouldReloadWorldRenderer = false;

	static HashMap<String, KeyMapping> idToKeyBindingMap = new HashMap<>();
	static HashMap<String, Boolean> lastKeyBindingStates = new HashMap<>();
	static boolean initializedKeyBindingMap = false;

	public static void registerPowerKeybinding(String keyId, KeyMapping keyBinding) {
		idToKeyBindingMap.put(keyId, keyBinding);
	}

	@SubscribeEvent
	public static void onRegister(net.neoforged.neoforge.registries.RegisterEvent event) {
		if (event.getRegistryKey().equals(io.github.apace100.apoli.registry.ApoliRegistries.ENTITY_CONDITION_KEY)) {
			EntityConditionsClient.register();
		} else if (event.getRegistryKey().equals(io.github.apace100.apoli.registry.ApoliRegistries.ITEM_CONDITION_KEY)) {
			ItemConditionsClient.register();
		}
	}

	public static void init() {
		ApoliClassDataClient.registerAll();
		GameHudRender.HUD_RENDERS.add(new PowerHudRenderer());
	}
}

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = io.github.apace100.origins.Origins.MODID, value = Dist.CLIENT)
class ApoliClientTickHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft tick = Minecraft.getInstance();
		if(tick.player != null) {
			List<Power> powers = PowerHolderComponent.KEY.get(tick.player).getPowers();
			List<Power> pressedPowers = new LinkedList<>();
			HashMap<String, Boolean> currentKeyBindingStates = new HashMap<>();
			for(Power power : powers) {
				if(power instanceof Active) {
					Active active = (Active)power;
					Active.Key key = active.getKey();
					KeyMapping keyBinding = getKeyBinding(key.key);
					if(keyBinding != null) {
						if(!currentKeyBindingStates.containsKey(key.key)) {
							currentKeyBindingStates.put(key.key, keyBinding.isDown());
						}
						if(currentKeyBindingStates.get(key.key) && (key.continuous || !ApoliClient.lastKeyBindingStates.getOrDefault(key.key, false))) {
							pressedPowers.add(power);
						}
					}
				}
			}
			ApoliClient.lastKeyBindingStates = currentKeyBindingStates;
			if(pressedPowers.size() > 0) {
				performActivePowers(pressedPowers);
			}
		}
    }

	@OnlyIn(Dist.CLIENT)
	private static void performActivePowers(List<Power> powers) {
		for(Power power : powers) {
			((Active) power).onUse();
		}
		PacketDistributor.sendToServer(new UseActivePowersPacket((List<PowerType<?>>) (Object) powers.stream().map(p -> p.getType()).toList()));
	}

	@OnlyIn(Dist.CLIENT)
	private static KeyMapping getKeyBinding(String key) {
		if(!ApoliClient.idToKeyBindingMap.containsKey(key)) {
			if(!ApoliClient.initializedKeyBindingMap) {
				ApoliClient.initializedKeyBindingMap = true;
				Minecraft client = Minecraft.getInstance();
				for(int i = 0; i < client.options.keyMappings.length; i++) {
					ApoliClient.idToKeyBindingMap.put(client.options.keyMappings[i].getName(), client.options.keyMappings[i]);
				}
				return getKeyBinding(key);
			}
			return null;
		}
		return ApoliClient.idToKeyBindingMap.get(key);
	}

    @SubscribeEvent
    public static void onRenderFog(net.neoforged.neoforge.client.event.ViewportEvent.RenderFog event) {
        if(event.getCamera().getEntity() instanceof net.minecraft.world.entity.player.Player player) {
            // Lava Visibility
            if(event.getCamera().getFluidInCamera() == net.minecraft.world.level.material.FogType.LAVA) {
                if(player.getAttributes().hasAttribute(io.github.apace100.apoli.registry.ApoliAttributes.LAVA_VISIBILITY)) {
                    double visibility = player.getAttributeValue(io.github.apace100.apoli.registry.ApoliAttributes.LAVA_VISIBILITY);
                    if(visibility > 0) {
                        event.setNearPlaneDistance((float)(event.getNearPlaneDistance() * visibility));
                        event.setFarPlaneDistance((float)(event.getFarPlaneDistance() * visibility));
                        event.setCanceled(true);
                    }
                }
            }
            // Water Visibility (Merling)
            if(event.getCamera().getFluidInCamera() == net.minecraft.world.level.material.FogType.WATER) {
                if (PowerHolderComponent.KEY.get(player).hasPower(io.github.apace100.origins.power.OriginsPowerTypes.WATER_VISION)) {
                    // Set far plane distance to a much larger value to clear the fog
                    event.setFarPlaneDistance(96.0f);
                    event.setNearPlaneDistance(0.0f);
                    event.setCanceled(true);
                }
            }
            // Phasing Blindness
            java.util.List<io.github.apace100.apoli.power.PhasingPower> phasings = PowerHolderComponent.getPowers(player, io.github.apace100.apoli.power.PhasingPower.class);
            if(phasings.stream().anyMatch(pp -> pp.getRenderType() == io.github.apace100.apoli.power.PhasingPower.RenderType.BLINDNESS)) {
                if(io.github.apace100.apoli.util.MiscUtil.getInWallBlockState(player) != null) {
                    float view = phasings.stream().filter(pp -> pp.getRenderType() == io.github.apace100.apoli.power.PhasingPower.RenderType.BLINDNESS).map(io.github.apace100.apoli.power.PhasingPower::getViewDistance).min(Float::compareTo).orElse(1.0f);
                    if (event.getMode() == net.minecraft.client.renderer.FogRenderer.FogMode.FOG_SKY) {
                        event.setNearPlaneDistance(0.0f);
                        event.setFarPlaneDistance(view * 0.8f);
                    } else {
                        event.setNearPlaneDistance(view * 0.25f);
                        event.setFarPlaneDistance(view);
                    }
                    event.setCanceled(true);
                }
            }
        }
    }
}

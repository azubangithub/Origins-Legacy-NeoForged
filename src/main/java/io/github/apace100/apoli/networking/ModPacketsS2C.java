package io.github.apace100.apoli.networking;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.util.SyncStatusEffectsUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ModPacketsS2C {

    public static void onStatusEffectSync(SyncStatusEffectPacket payload, IPayloadContext context) {
        int targetId = payload.entityId();
        SyncStatusEffectsUtil.UpdateType updateType = SyncStatusEffectsUtil.UpdateType.values()[payload.updateType()];
        MobEffectInstance finalInstance = payload.effectInstance().orElse(null);
        context.enqueueWork(() -> {
            Entity target = context.player().level().getEntity(targetId);
            if (!(target instanceof LivingEntity living)) {
                Apoli.LOGGER.warn("Received unknown target for status effect synchronization");
            } else {
                switch(updateType) {
                    case CLEAR -> living.getActiveEffectsMap().clear();
                    case APPLY, UPGRADE -> living.getActiveEffectsMap().put(finalInstance.getEffect(), finalInstance);
                    case REMOVE -> living.getActiveEffectsMap().remove(finalInstance.getEffect());
                }
            }
        });
    }

    public static void onSetAttacker(SetAttackerPacket payload, IPayloadContext context) {
        int targetId = payload.entityId();
        boolean hasAttacker = payload.attackingEntityId().isPresent();
        int attackerId = 0;
        if(hasAttacker) {
            attackerId = payload.attackingEntityId().orElseThrow();
        }
        int finalAttackerId = attackerId;
        context.enqueueWork(() -> {
            Entity target = context.player().level().getEntity(targetId);
            Entity attacker = null;
            if(hasAttacker) {
                attacker = context.player().level().getEntity(finalAttackerId);
            }
            if (!(target instanceof LivingEntity)) {
                Apoli.LOGGER.warn("Received unknown target");
            } else if(hasAttacker && !(attacker instanceof LivingEntity)) {
                Apoli.LOGGER.warn("Received unknown attacker");
            } else {
                if(hasAttacker) {
                    ((LivingEntity)target).setLastHurtByMob((LivingEntity)attacker);
                } else {
                    ((LivingEntity)target).setLastHurtByMob(null);
                }
            }
        });
    }


    public static void receivePowerList(PowerListPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            PowerTypeRegistry.clear();
            payload.factories().forEach(PowerTypeRegistry::register);
        });
    }

    public static void onPlayerMount(PlayerMountPacket payload, IPayloadContext context) {
        int mountingPlayerId = payload.ridingEntity();
        int mountedPlayerId = payload.vehicleEntity();
        context.enqueueWork(() -> {
            Entity mountingPlayer = context.player().level().getEntity(mountingPlayerId);
            Entity mountedPlayer = context.player().level().getEntity(mountedPlayerId);
            if (mountedPlayer == null) {
                Apoli.LOGGER.warn("Received passenger for unknown player");
            } else if(mountingPlayer == null) {
                Apoli.LOGGER.warn("Received unknown passenger for player");
            } else {
                boolean result = mountingPlayer.startRiding(mountedPlayer, true);
                if(result) {
                    Apoli.LOGGER.info(mountingPlayer.getDisplayName().getString() + " started riding " + mountedPlayer.getDisplayName().getString());
                } else {
                    Apoli.LOGGER.warn(mountingPlayer.getDisplayName().getString() + " failed to start riding " + mountedPlayer.getDisplayName().getString());
                }
            }
        });
    }

    public static void onPlayerDismount(PlayerDismountPacket payload, IPayloadContext context) {
        int dismountingPlayerId = payload.ridingEntity();
        context.enqueueWork(() -> {
            Entity dismountingPlayer = context.player().level().getEntity(dismountingPlayerId);
            if (dismountingPlayer == null) {
                Apoli.LOGGER.warn("Unknown player tried to dismount");
            } else {
                if(dismountingPlayer.getVehicle() instanceof Player) {
                    dismountingPlayer.removeVehicle();
                }
            }
        });
    }

    public static void onPowerSync(SyncPowerPacket payload, IPayloadContext context) {
        int entityId = payload.entityId();
        ResourceLocation powerId = payload.powerId();
        CompoundTag powerNbtContainer = payload.powerNbtContainer();
        Tag powerNbt = powerNbtContainer.get("Data");
        context.enqueueWork(() -> {
            if(!PowerTypeRegistry.contains(powerId)) {
                Apoli.LOGGER.warn("Received sync packet for unknown power type: " + powerId);
                return;
            }
            Entity entity = context.player().level().getEntity(entityId);
            if (entity == null && entityId == context.player().getId()) {
                entity = context.player();
            }
            if (entity == null) {
                Apoli.LOGGER.warn("Received sync packet for unknown power holder.");
                return;
            }
            PowerType<?> powerType = PowerTypeRegistry.get(powerId);
            PowerHolderComponent.KEY.maybeGet(entity).ifPresentOrElse(phc -> {
                Power power = phc.getPower(powerType);
                if (power != null) {
                    power.fromTag(powerNbt, context.player().level().registryAccess());
                } else {
                    Apoli.LOGGER.warn("Received sync packet for entity without power: " + powerId);
                }
            }, () -> Apoli.LOGGER.warn("Received sync packet for entity without power holder."));
        });
    }

    public static void onPowerComponentSync(SyncPowerComponentPacket payload, IPayloadContext context) {
        int entityId = payload.entityId();
        CompoundTag componentNbt = payload.componentNbt();
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(entityId);
            if (entity == null && entityId == context.player().getId()) {
                entity = context.player();
            }
            if (entity instanceof LivingEntity) {
                PowerHolderComponent.KEY.maybeGet(entity).ifPresent(phc -> {
                    if (phc instanceof io.github.apace100.apoli.component.PowerHolderComponentImpl impl) {
                        impl.readFromNbt(componentNbt, context.player().level().registryAccess());
                    }
                });
            } else if (entity == null) {
                Apoli.LOGGER.warn("Received component sync packet for unknown entity.");
            }
        });
    }
}

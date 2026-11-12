package io.github.apace100.apoli.util;

import io.github.apace100.apoli.networking.SyncStatusEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

public class SyncStatusEffectsUtil {

    public static void sendStatusEffectUpdatePacket(LivingEntity target, UpdateType updateType, MobEffectInstance instance) {
        if (target.level().isClientSide()) return;
        SyncStatusEffectPacket packet = new SyncStatusEffectPacket(target.getId(), (byte) updateType.ordinal(), java.util.Optional.ofNullable(instance));
        PacketDistributor.sendToPlayersTrackingEntity(target, packet);
    }

    public enum UpdateType {
        CLEAR, APPLY, UPGRADE, REMOVE
    }
}

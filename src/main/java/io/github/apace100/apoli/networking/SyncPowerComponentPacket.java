package io.github.apace100.apoli.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncPowerComponentPacket(
    int entityId,
    CompoundTag componentNbt
) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, SyncPowerComponentPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, SyncPowerComponentPacket::entityId,
        ByteBufCodecs.COMPOUND_TAG, SyncPowerComponentPacket::componentNbt,
        SyncPowerComponentPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ModPackets.SYNC_POWER_COMPONENT;
    }
}

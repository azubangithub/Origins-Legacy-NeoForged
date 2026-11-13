package io.github.apace100.origins.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import io.github.apace100.origins.Origins;

public record SyncOriginComponentPacket(int entityId, CompoundTag componentNbt) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncOriginComponentPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, SyncOriginComponentPacket::entityId,
        ByteBufCodecs.COMPOUND_TAG, SyncOriginComponentPacket::componentNbt,
        SyncOriginComponentPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ModPackets.SYNC_ORIGIN_COMPONENT;
    }
}

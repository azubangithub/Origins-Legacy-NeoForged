package io.github.apace100.apoli.networking;

import io.github.apace100.apoli.Apoli;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = io.github.apace100.origins.Origins.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModPackets {

    public static final ResourceLocation HANDSHAKE = Apoli.identifier("handshake");

    public static final CustomPacketPayload.Type<UseActivePowersPacket> USE_ACTIVE_POWERS = new CustomPacketPayload.Type<>(Apoli.identifier("use_active_powers")); // C -> S
    public static final CustomPacketPayload.Type<PowerListPacket> POWER_LIST = new CustomPacketPayload.Type<>(Apoli.identifier("power_list")); // S -> C
    public static final CustomPacketPayload.Type<SyncPowerPacket> SYNC_POWER = new CustomPacketPayload.Type<>(Apoli.identifier("sync_power")); // S -> C
    public static final CustomPacketPayload.Type<SyncPowerComponentPacket> SYNC_POWER_COMPONENT = new CustomPacketPayload.Type<>(Apoli.identifier("sync_power_component")); // S -> C

    public static final CustomPacketPayload.Type<PlayerLandedPacket> PLAYER_LANDED = new CustomPacketPayload.Type<>(Apoli.identifier("player_landed")); // C -> S

    public static final CustomPacketPayload.Type<PlayerMountPacket> PLAYER_MOUNT = new CustomPacketPayload.Type<>(Apoli.identifier("player_mount")); // S -> C
    public static final CustomPacketPayload.Type<PlayerDismountPacket> PLAYER_DISMOUNT = new CustomPacketPayload.Type<>(Apoli.identifier("player_dismount")); // S -> C

    public static final CustomPacketPayload.Type<PreventedEntityUsePacket> PREVENTED_ENTITY_USE = new CustomPacketPayload.Type<>(Apoli.identifier("prevented_entity_use")); // C -> S

    public static final CustomPacketPayload.Type<SetAttackerPacket> SET_ATTACKER = new CustomPacketPayload.Type<>(Apoli.identifier("set_attacker")); // S -> C

    public static final CustomPacketPayload.Type<SyncStatusEffectPacket> SYNC_STATUS_EFFECT = new CustomPacketPayload.Type<>(Apoli.identifier("sync_status_effect")); // S -> C

    public static void init() {
        ModPacketsC2S.register();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Apoli.MODID);

        registrar.playToServer((CustomPacketPayload.Type) USE_ACTIVE_POWERS, (net.minecraft.network.codec.StreamCodec) UseActivePowersPacket.CODEC, (payload, context) -> ModPacketsC2S.onUseActivePowers((UseActivePowersPacket) payload, context));
        registrar.playToServer((CustomPacketPayload.Type) PLAYER_LANDED, (net.minecraft.network.codec.StreamCodec) PlayerLandedPacket.CODEC, (payload, context) -> ModPacketsC2S.onPlayerLanded((PlayerLandedPacket) payload, context));
        registrar.playToServer((CustomPacketPayload.Type) PREVENTED_ENTITY_USE, (net.minecraft.network.codec.StreamCodec) PreventedEntityUsePacket.CODEC, (payload, context) -> ModPacketsC2S.onPreventedEntityUse((PreventedEntityUsePacket) payload, context));

        registrar.playToClient((CustomPacketPayload.Type) POWER_LIST, (net.minecraft.network.codec.StreamCodec) PowerListPacket.CODEC, (payload, context) -> ModPacketsS2C.receivePowerList((PowerListPacket) payload, context));
        registrar.playToClient((CustomPacketPayload.Type) SYNC_POWER, (net.minecraft.network.codec.StreamCodec) SyncPowerPacket.CODEC, (payload, context) -> ModPacketsS2C.onPowerSync((SyncPowerPacket) payload, context));
        registrar.playToClient((CustomPacketPayload.Type) SYNC_POWER_COMPONENT, (net.minecraft.network.codec.StreamCodec) SyncPowerComponentPacket.CODEC, (payload, context) -> ModPacketsS2C.onPowerComponentSync((SyncPowerComponentPacket) payload, context));
        registrar.playToClient((CustomPacketPayload.Type) PLAYER_MOUNT, (net.minecraft.network.codec.StreamCodec) PlayerMountPacket.CODEC, (payload, context) -> ModPacketsS2C.onPlayerMount((PlayerMountPacket) payload, context));
        registrar.playToClient((CustomPacketPayload.Type) PLAYER_DISMOUNT, (net.minecraft.network.codec.StreamCodec) PlayerDismountPacket.CODEC, (payload, context) -> ModPacketsS2C.onPlayerDismount((PlayerDismountPacket) payload, context));
        registrar.playToClient((CustomPacketPayload.Type) SET_ATTACKER, (net.minecraft.network.codec.StreamCodec) SetAttackerPacket.CODEC, (payload, context) -> ModPacketsS2C.onSetAttacker((SetAttackerPacket) payload, context));
        registrar.playToClient((CustomPacketPayload.Type) SYNC_STATUS_EFFECT, (net.minecraft.network.codec.StreamCodec) SyncStatusEffectPacket.CODEC, (payload, context) -> ModPacketsS2C.onStatusEffectSync((SyncStatusEffectPacket) payload, context));
    }
}

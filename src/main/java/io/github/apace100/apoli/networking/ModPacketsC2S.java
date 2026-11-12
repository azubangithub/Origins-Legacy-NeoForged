package io.github.apace100.apoli.networking;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ModPacketsC2S {

    public static void register() {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ModPacketsC2S::onPlayerJoin);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ModPacketsC2S::onPlayerRespawn);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ModPacketsC2S::onPlayerChangedDimension);
    }

    private static void onPlayerJoin(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
            sync(player);
        }
    }

    private static void onPlayerRespawn(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
            sync(player);
        }
    }

    private static void onPlayerChangedDimension(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
            sync(player);
        }
    }

    private static void sync(net.minecraft.server.level.ServerPlayer player) {
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new io.github.apace100.apoli.networking.PowerListPacket(PowerTypeRegistry.get()));
        PowerHolderComponent.sync(player);
    }

    public static void onPlayerLanded(PlayerLandedPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> PowerHolderComponent.getPowers(context.player(), ActionOnLandPower.class).forEach(ActionOnLandPower::executeAction));
    }

    public static void onPreventedEntityUse(PreventedEntityUsePacket payload, IPayloadContext context) {
        int otherEntityId = payload.otherEntityId();
        int handOrdinal = payload.handOrdinal();
        context.enqueueWork(() -> {
            Entity otherEntity = context.player().level().getEntity(otherEntityId);
            InteractionHand hand = InteractionHand.values()[handOrdinal];
            if(otherEntity == null) {
                Apoli.LOGGER.warn("Received unknown entity for prevented interaction");
            } else {
                boolean prevented = false;
                for(PreventEntityUsePower peup : PowerHolderComponent.getPowers(context.player(), PreventEntityUsePower.class)) {
                    if(peup.doesApply(otherEntity, hand, context.player().getItemInHand(hand))) {
                        peup.executeAction(otherEntity, hand);
                        prevented = true;
                        break;
                    }
                }
                if(!prevented) {
                    for(PreventBeingUsedPower pbup : PowerHolderComponent.getPowers(otherEntity, PreventBeingUsedPower.class)) {
                        if(pbup.doesApply(context.player(), hand, context.player().getItemInHand(hand))) {
                            pbup.executeAction(context.player(), hand);
                            prevented = true;
                            break;
                        }
                    }
                    if(!prevented) {
                        Apoli.LOGGER.warn("Couldn't find corresponding entity use preventing power");
                    }
                }
            }
        });
    }

    public static void onUseActivePowers(UseActivePowersPacket payload, IPayloadContext context) {
        var powerTypes = payload.powers();
        context.enqueueWork(() -> {
            PowerHolderComponent component = PowerHolderComponent.KEY.get(context.player());
            for(PowerType<?> type : powerTypes) {
                Power power = component.getPower(type);
                if(power instanceof Active) {
                    ((Active) power).onUse();
                }
            }
        });
    }
}

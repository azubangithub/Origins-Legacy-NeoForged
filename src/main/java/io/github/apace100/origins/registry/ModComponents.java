package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.component.PlayerOriginComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

public class ModComponents {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Origins.MODID);

    public static final Supplier<AttachmentType<PlayerOriginComponent>> ORIGIN = ATTACHMENT_TYPES.register("origin", () -> 
        AttachmentType.builder((holder) -> new PlayerOriginComponent((Player) holder))
            .serialize(new IAttachmentSerializer<CompoundTag, PlayerOriginComponent>() {
                @Override
                public PlayerOriginComponent read(net.neoforged.neoforge.attachment.IAttachmentHolder holder, CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
                    PlayerOriginComponent component = new PlayerOriginComponent((Player) holder);
                    component.deserializeNBT(provider, tag);
                    return component;
                }

                @Override
                public CompoundTag write(PlayerOriginComponent attachment, net.minecraft.core.HolderLookup.Provider provider) {
                    return attachment.serializeNBT(provider);
                }
            })
            .copyOnDeath()
            .copyHandler((attachment, holder, provider) -> {
                Player newPlayer = (Player) holder;
                PlayerOriginComponent newComponent = new PlayerOriginComponent(newPlayer);
                CompoundTag tag = attachment.serializeNBT(provider);
                newComponent.deserializeNBT(provider, tag);
                return newComponent;
            })
            .build()
    );

    public static PlayerOriginComponent get(net.minecraft.world.entity.Entity entity) {
        return entity.getData(ORIGIN);
    }

    public static java.util.Optional<OriginComponent> maybeGet(net.minecraft.world.entity.Entity entity) {
        if (entity instanceof Player) {
            return java.util.Optional.of(entity.getData(ORIGIN));
        }
        return java.util.Optional.empty();
    }

    public static void sync(Player player) {
        if (!player.level().isClientSide) {
            maybeGet(player).ifPresent(component -> {
                if (component instanceof PlayerOriginComponent poc) {
                    net.minecraft.nbt.CompoundTag nbt = poc.serializeNBT(player.level().registryAccess());
                    io.github.apace100.origins.networking.SyncOriginComponentPacket packet = 
                        new io.github.apace100.origins.networking.SyncOriginComponentPacket(player.getId(), nbt);
                    net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, packet);
                }
            });
        }
    }
}

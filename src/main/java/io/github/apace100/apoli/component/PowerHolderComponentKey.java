package io.github.apace100.apoli.component;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A drop-in replacement for CCA's ComponentKey to minimize refactoring across 100+ power classes.
 * Wraps NeoForge's Data Attachment system.
 */
public class PowerHolderComponentKey {

    private final ResourceLocation id;
    private Supplier<AttachmentType<PowerHolderComponentImpl>> attachmentTypeSupplier;

    public PowerHolderComponentKey(ResourceLocation id) {
        this.id = id;
    }

    public void setAttachmentSupplier(Supplier<AttachmentType<PowerHolderComponentImpl>> supplier) {
        this.attachmentTypeSupplier = supplier;
    }

    public PowerHolderComponent get(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            throw new IllegalArgumentException("PowerHolderComponent can only be attached to LivingEntity");
        }
        if (attachmentTypeSupplier == null) {
            throw new IllegalStateException("PowerHolderComponentKey attachment supplier not initialized!");
        }
        return entity.getData(attachmentTypeSupplier.get());
    }

    public Optional<PowerHolderComponent> maybeGet(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return Optional.empty();
        }
        if (attachmentTypeSupplier == null) {
            return Optional.empty();
        }
        // In NeoForge, getData will initialize it if it's missing, which is the desired behavior for Origin's component
        // because every LivingEntity should have a PowerHolderComponent.
        return Optional.of(entity.getData(attachmentTypeSupplier.get()));
    }

    public void sync(Entity entity) {
        if (!entity.level().isClientSide) {
            maybeGet(entity).ifPresent(phc -> {
                if (phc instanceof io.github.apace100.apoli.component.PowerHolderComponentImpl impl) {
                    net.minecraft.nbt.CompoundTag nbt = new net.minecraft.nbt.CompoundTag();
                    impl.writeToNbt(nbt, entity.level().registryAccess());
                    net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                        entity,
                        new io.github.apace100.apoli.networking.SyncPowerComponentPacket(entity.getId(), nbt)
                    );
                }
            });
        }
    }
}

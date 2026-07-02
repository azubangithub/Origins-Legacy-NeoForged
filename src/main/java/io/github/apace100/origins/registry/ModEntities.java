package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.entity.EnderianPearlEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Origins.MODID);

    public static final Supplier<EntityType<EnderianPearlEntity>> ENDERIAN_PEARL = ENTITY_TYPES.register("enderian_pearl", () ->
        EntityType.Builder.<EnderianPearlEntity>of(EnderianPearlEntity::new, MobCategory.MISC)
            .sized(0.25f, 0.25f)
            .clientTrackingRange(64)
            .updateInterval(10)
            .build("enderian_pearl")
    );

    public static void register() {
        // Static init is enough; DeferredRegister is registered in Origins constructor
    }
}

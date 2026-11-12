package io.github.apace100.origins.registry;

import com.mojang.serialization.MapCodec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.util.OriginLootCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModLoot {

    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_CONDITION_TYPES = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Origins.MODID);

    public static final Supplier<MapCodec<? extends LootItemCondition>> ORIGIN_LOOT_CONDITION = LOOT_CONDITION_TYPES.register("origin",
        () -> OriginLootCondition.CODEC);
}

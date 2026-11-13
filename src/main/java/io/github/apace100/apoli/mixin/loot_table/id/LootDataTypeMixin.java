package io.github.apace100.apoli.mixin.loot_table.id;

import net.minecraft.world.level.storage.loot.LootDataType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LootDataType.class)
public abstract class LootDataTypeMixin<T> {
}

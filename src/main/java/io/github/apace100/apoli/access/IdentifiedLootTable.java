package io.github.apace100.apoli.access;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;

public interface IdentifiedLootTable {

    void apoli$setId(Identifier id, HolderGetter.Provider lootManager);

    Identifier apoli$getId();

    default void setId(Identifier id, HolderGetter.Provider lootManager) {
        apoli$setId(id, lootManager);
    }

    default Identifier getId() {
        return apoli$getId();
    }
}

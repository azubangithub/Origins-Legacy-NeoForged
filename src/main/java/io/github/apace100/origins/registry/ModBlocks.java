package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.content.TemporaryCobwebBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Origins.MODID);

    public static final DeferredBlock<Block> TEMPORARY_COBWEB = BLOCKS.registerBlock("temporary_cobweb",
        TemporaryCobwebBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOL)
            .forceSolidOn()
            .noCollission()
            .requiresCorrectToolForDrops()
            .strength(4.0F)
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

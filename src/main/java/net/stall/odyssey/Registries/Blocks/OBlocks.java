package net.stall.odyssey.Registries.Blocks;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.stall.odyssey.Odyssey;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class OBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Odyssey.MODID);

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return (lit) -> (Boolean)lit.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    /*public static final DeferredRegister<Block> NIXIE_TUBE = registerBlock("nixie_tube",
            () -> new NixieTubeBlock(BlockBehaviour.Properties.of()
                    .lightLevel(litBlockEmission(15))
                    .strength(0.3F).sound(SoundType.GLASS)
                    .isValidSpawn(Blocks::always)
            )
    );*/

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        //CItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static Boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> entity) {
        return true;
    }
}
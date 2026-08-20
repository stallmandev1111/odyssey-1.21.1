package net.stall.odyssey.Registries.Worldgen.Carvers.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;

import java.util.function.Function;

public class RotfulCavernsCarver extends WorldCarver<CaveCarverConfiguration> {

    private static final int MIN_Y = -74;
    private static final int MAX_Y = -65;

    public RotfulCavernsCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean isStartChunk(
            CaveCarverConfiguration config,
            RandomSource random
    ) {
        // Every chunk gets the cavern.
        return true;
    }

    @Override
    public boolean carve(
            CarvingContext context,
            CaveCarverConfiguration config,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeAccessor,
            RandomSource random,
            Aquifer aquifer,
            ChunkPos chunkPos,
            CarvingMask carvingMask
    ) {
        boolean carved = false;

        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();

        /*
         * Make one continuous cavern layer.
         *
         * Every block in this chunk from MIN_Y to MAX_Y
         * is carved if it belongs to the carver's replaceable tag.
         */
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                for (int y = MIN_Y; y <= MAX_Y; y++) {

                    if (carvingMask.get(x, y, z)) {
                        continue;
                    }

                    BlockPos pos = new BlockPos(
                            minX + x,
                            y,
                            minZ + z
                    );

                    /*
                     * Only carve blocks allowed by the configured
                     * replaceable tag.
                     */
                    if (!canReplaceBlock(config, chunk.getBlockState(pos))) {
                        continue;
                    }

                    carvingMask.set(x, y, z);

                    /*
                     * Cave air gives the layer normal cave behavior.
                     */
                    chunk.setBlockState(
                            pos,
                            Blocks.CAVE_AIR.defaultBlockState(),
                            false
                    );

                    carved = true;
                }
            }
        }

        return carved;
    }
}
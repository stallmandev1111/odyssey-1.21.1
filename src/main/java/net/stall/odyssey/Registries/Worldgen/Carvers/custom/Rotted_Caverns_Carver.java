package net.stall.odyssey.Registries.Worldgen.Carvers.custom;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.stall.odyssey.Registries.Worldgen.Biome.Biomesources.ModBiomeSources;


public class Rotted_Caverns_Carver extends WorldCarver<CarverConfiguration> {

    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

    private static final int MIN_Y = -74; // inclusive
    private static final int MAX_Y = -65; // inclusive

    public Rotted_Caverns_Carver(Codec<CarverConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean isStartChunk(RandomSource random, CarverConfiguration config) {
        // no randomness, no probability roll - this chunk always gets carved
        return true;
    }

    @Override
    protected int getRange() {
        // 0 = only the chunk currently being generated is considered,
        // no spreading in from neighboring chunks. Keeps this fully deterministic.
        return 0;
    }

    @Override
    public boolean carve(CarvingContext context, CarverConfiguration config, ChunkAccess chunk,
                          Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random,
                          Aquifer aquifer, ChunkPos chunkPos, CarvingMask carvingMask) {

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minGen = context.getMinGenY();
        int maxGen = minGen + context.getGenDepth() - 1;

        int loMin = Math.max(MIN_Y, minGen);
        int loMax = Math.min(MAX_Y, maxGen);

        for (int x = 0; x < 16; x++) {
            int worldX = chunkPos.getMinBlockX() + x;
            for (int z = 0; z < 16; z++) {
                int worldZ = chunkPos.getMinBlockZ() + z;
                for (int y = loMin; y <= loMax; y++) {
                    pos.set(worldX, y, worldZ);

                    // only carve columns that are actually your underworld biome,
                    // so this doesn't bleed into neighboring biomes at the edges
                    if (!biomeAccessor.apply(pos).is()) {
                        continue;
                    }

                    chunk.setBlockState(pos, AIR, false);
                    carvingMask.set(x, y, z);
                }
            }
        }
        return true;
    }
}
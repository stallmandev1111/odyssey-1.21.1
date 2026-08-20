package net.stall.odyssey.Registries.Worldgen.Carvers.custom;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.ChunkPos;

import java.util.function.Function;

public class RottedDepthsCarver extends WorldCarver<CarverConfiguration> {

    public RottedDepthsCarver() {
        super((Codec<CarverConfiguration>) CarverConfiguration.CODEC);
    }

    @Override
    public boolean isStartChunk(CarverConfiguration config, RandomSource random) {
        return true;
    }

    @Override
    public boolean carve(
            CarvingContext context,
            CarverConfiguration config,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeAccessor,
            RandomSource random,
            Aquifer aquifer,
            ChunkPos chunkPos,
            CarvingMask carvingMask
    ) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int minY = -75;
        int maxY = -65;

        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                for (int y = minY; y <= maxY; y++) {

                    pos.set(
                            minX + x,
                            y,
                            minZ + z
                    );

                    chunk.setBlockState(
                            pos,
                            AIR,
                            false
                    );

                    carvingMask.set(x, y, z);
                }
            }
        }

        return true;
    }
}

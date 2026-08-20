package net.stall.odyssey.Registries.Biome.Carvers.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;

import java.util.function.Function;

public class RotfulCavernsCarver extends CaveWorldCarver {

    private static final int CAVERN_BOTTOM = -73;
    private static final int CAVERN_TOP = -71;
    private static final int LAVA_Y = -73;

    public RotfulCavernsCarver(
            Codec<CaveCarverConfiguration> codec
    ) {
        super(codec);
    }

    @Override
    public boolean isStartChunk(
            CaveCarverConfiguration config,
            RandomSource random
    ) {
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
        int minX = chunk.getPos().getMinBlockX();
        int maxX = chunk.getPos().getMaxBlockX();

        int minZ = chunk.getPos().getMinBlockZ();
        int maxZ = chunk.getPos().getMaxBlockZ();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                carveColumn(
                        chunk,
                        carvingMask,
                        x,
                        z
                );
            }
        }

        return true;
    }

    private void carveColumn(
            ChunkAccess chunk,
            CarvingMask carvingMask,
            int x,
            int z
    ) {
        for (int y = CAVERN_BOTTOM; y <= CAVERN_TOP; y++) {
            BlockPos pos = new BlockPos(x, y, z);

            if (carvingMask.get(
                    x & 15,
                    y,
                    z & 15
            )) {
                continue;
            }

            if (y == LAVA_Y) {
                if (chunk.getBlockState(pos).isAir()) {
                    chunk.setBlockState(
                            pos,
                            Blocks.LAVA.defaultBlockState(),
                            false
                    );
                }
            } else {
                chunk.setBlockState(
                        pos,
                        Blocks.AIR.defaultBlockState(),
                        false
                );
            }

            carvingMask.set(
                    x & 15,
                    y,
                    z & 15
            );
        }
    }
}
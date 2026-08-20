package net.stall.odyssey.Registries.Biome.Carvers.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;

import java.util.function.Function;

public class RotfulCavernsCarver extends CaveWorldCarver {

    public RotfulCavernsCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean isStartChunk(
            CaveCarverConfiguration config,
            RandomSource random
    ) {
        return random.nextFloat() < config.probability;
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
        double x = chunkPos.getMiddleBlockX();
        double y = -72.0D;
        double z = chunkPos.getMiddleBlockZ();

        float radius = 8.0F;

        // Carve a simple room.
        carveEllipsoid(
                context,
                config,
                chunk,
                biomeAccessor,
                aquifer,
                x,
                y,
                z,
                radius,
                radius / 2.0F,
                carvingMask
        );

        return true;
    }

    private void carveEllipsoid(
            CarvingContext context,
            CaveCarverConfiguration config,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeAccessor,
            Aquifer aquifer,
            double centerX,
            double centerY,
            double centerZ,
            double radiusX,
            double radiusY,
            CarvingMask carvingMask
    ) {
        int minX = (int) Math.floor(centerX - radiusX);
        int maxX = (int) Math.ceil(centerX + radiusX);

        int minY = (int) Math.floor(centerY - radiusY);
        int maxY = (int) Math.ceil(centerY + radiusY);

        int minZ = (int) Math.floor(centerZ - radiusX);
        int maxZ = (int) Math.ceil(centerZ + radiusX);

        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {

                    double dx = (x + 0.5D - centerX) / radiusX;
                    double dy = (y + 0.5D - centerY) / radiusY;
                    double dz = (z + 0.5D - centerZ) / radiusX;

                    if (dx * dx + dy * dy + dz * dz > 1.0D) {
                        continue;
                    }

                    BlockPos pos = new BlockPos(x, y, z);

                    if (!chunk.getPos().equals(new ChunkPos(pos))) {
                        continue;
                    }

                    if (carvingMask.get(x & 15, y, z & 15)) {
                        continue;
                    }

                    carvingMask.set(x & 15, y, z & 15);

                    chunk.setBlockState(
                            pos,
                            net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),
                            false
                    );
                }
            }
        }
    }
}
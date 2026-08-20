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

    private static final int MIN_Y = -73;
    private static final int MAX_Y = -66;

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
        double centerX =
                chunkPos.getMiddleBlockX();

        double centerZ =
                chunkPos.getMiddleBlockZ();

        double radius =
                12.0D + random.nextDouble() * 8.0D;

        int minX =
                (int) Math.floor(
                        centerX - radius
                );

        int maxX =
                (int) Math.ceil(
                        centerX + radius
                );

        int minZ =
                (int) Math.floor(
                        centerZ - radius
                );

        int maxZ =
                (int) Math.ceil(
                        centerZ + radius
                );

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {

                if (
                        x < chunk.getPos().getMinBlockX()
                                || x > chunk.getPos().getMaxBlockX()
                                || z < chunk.getPos().getMinBlockZ()
                                || z > chunk.getPos().getMaxBlockZ()
                ) {
                    continue;
                }

                double dx =
                        (x + 0.5D - centerX)
                                / radius;

                double dz =
                        (z + 0.5D - centerZ)
                                / radius;

                double distance =
                        Math.sqrt(
                                dx * dx
                                        + dz * dz
                        );

                double wallNoise =
                        Math.sin(
                                x * 0.35D
                        ) * 0.18D
                                + Math.cos(
                                z * 0.31D
                        ) * 0.18D
                                + Math.sin(
                                (x + z) * 0.17D
                        ) * 0.12D;

                if (
                        distance
                                > 0.82D
                                + wallNoise
                ) {
                    continue;
                }

                double floorNoise =
                        Math.sin(
                                x * 0.12D
                        ) * 0.7D
                                + Math.cos(
                                z * 0.10D
                        ) * 0.5D;

                double ceilingNoise =
                        Math.sin(
                                x * 0.09D
                        ) * 0.6D
                                + Math.cos(
                                z * 0.13D
                        ) * 0.5D;

                int floor =
                        clamp(
                                -73
                                        + (int) Math.round(
                                        floorNoise
                                ),
                                -73,
                                -71
                        );

                int ceiling =
                        clamp(
                                -66
                                        + (int) Math.round(
                                        ceilingNoise
                                ),
                                -68,
                                -66
                        );

                for (int y = floor; y <= ceiling; y++) {

                    if (
                            y < MIN_Y
                                    || y > MAX_Y
                    ) {
                        continue;
                    }

                    if (
                            isPillar(
                                    x,
                                    y,
                                    z
                            )
                    ) {
                        continue;
                    }

                    BlockPos pos =
                            new BlockPos(
                                    x,
                                    y,
                                    z
                            );

                    if (
                            carvingMask.get(
                                    x & 15,
                                    y,
                                    z & 15
                            )
                    ) {
                        continue;
                    }

                    if (y == LAVA_Y) {
                        if (
                                chunk.getBlockState(pos)
                                        .isAir()
                        ) {
                            chunk.setBlockState(
                                    pos,
                                    Blocks.LAVA.defaultBlockState(),
                                    false
                            );
                        }

                        carvingMask.set(
                                x & 15,
                                y,
                                z & 15
                        );

                        continue;
                    }

                    chunk.setBlockState(
                            pos,
                            Blocks.AIR.defaultBlockState(),
                            false
                    );

                    carvingMask.set(
                            x & 15,
                            y,
                            z & 15
                    );
                }
            }
        }

        return true;
    }

    private boolean isPillar(
            int x,
            int y,
            int z
    ) {
        double cellX =
                Math.floor(
                        x / 24.0D
                );

        double cellZ =
                Math.floor(
                        z / 24.0D
                );

        double pillarX =
                cellX * 24.0D
                        + 12.0D
                        + hash(
                        (int) cellX,
                        (int) cellZ,
                        1
                ) * 6.0D;

        double pillarZ =
                cellZ * 24.0D
                        + 12.0D
                        + hash(
                        (int) cellX,
                        (int) cellZ,
                        2
                ) * 6.0D;

        double progress =
                (y - MIN_Y)
                        / (double) (
                        MAX_Y - MIN_Y
                );

        double leanX =
                Math.sin(
                        pillarZ * 0.08D
                ) * 3.0D;

        double leanZ =
                Math.cos(
                        pillarX * 0.08D
                ) * 3.0D;

        pillarX +=
                leanX * progress;

        pillarZ +=
                leanZ * progress;

        double dx =
                x + 0.5D - pillarX;

        double dz =
                z + 0.5D - pillarZ;

        double distance =
                Math.sqrt(
                        dx * dx
                                + dz * dz
                );

        double radius =
                3.0D
                        + Math.sin(
                        x * 0.18D
                                + z * 0.12D
                ) * 0.8D;

        return distance < radius;
    }

    private double hash(
            int x,
            int z,
            int seed
    ) {
        long value =
                x * 341873128712L
                        + z * 132897987541L
                        + seed * 42317861L;

        value ^=
                value >> 13;

        value *=
                1274126177L;

        value ^=
                value >> 16;

        return (
                (value & 0xFFFFFFL)
                        / (double) 0xFFFFFFL
                        * 2.0D
                        - 1.0D
        );
    }

    private int clamp(
            int value,
            int min,
            int max
    ) {
        return Math.max(
                min,
                Math.min(
                        max,
                        value
                )
        );
    }
}
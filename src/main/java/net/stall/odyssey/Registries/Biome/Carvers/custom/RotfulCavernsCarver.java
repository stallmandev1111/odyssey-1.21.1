package net.stall.odyssey.Registries.Biome.Carvers.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
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

    private static final int MIN_Y = -74;
    private static final int MAX_Y = -68;

    private static final int LAVA_Y = -79;

    private static final int MIN_TUNNELS = 3;
    private static final int MAX_TUNNELS = 6;

    private static final double MIN_TUNNEL_LENGTH = 40.0D;
    private static final double MAX_TUNNEL_LENGTH = 90.0D;

    private static final double MIN_RADIUS = 3.0D;
    private static final double MAX_RADIUS = 6.0D;

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
        int tunnelCount = Mth.nextInt(
                random,
                MIN_TUNNELS,
                MAX_TUNNELS
        );

        for (int i = 0; i < tunnelCount; i++) {
            generateTunnel(
                    chunk,
                    carvingMask,
                    random,
                    chunkPos
            );
        }

        return true;
    }

    private void generateTunnel(
            ChunkAccess chunk,
            CarvingMask carvingMask,
            RandomSource random,
            ChunkPos chunkPos
    ) {
        double x =
                chunkPos.getMinBlockX()
                        + random.nextDouble() * 16.0D;

        double z =
                chunkPos.getMinBlockZ()
                        + random.nextDouble() * 16.0D;

        double y =
                -72.0D
                        + (random.nextDouble() - 0.5D);

        double angle =
                random.nextDouble()
                        * Math.PI
                        * 2.0D;

        double length =
                MIN_TUNNEL_LENGTH
                        + random.nextDouble()
                        * (
                        MAX_TUNNEL_LENGTH
                                - MIN_TUNNEL_LENGTH
                );

        double baseRadius =
                MIN_RADIUS
                        + random.nextDouble()
                        * (
                        MAX_RADIUS
                                - MIN_RADIUS
                );

        int steps =
                (int) (length * 2.0D);

        double noise1 =
                random.nextDouble() * 1000.0D;

        double noise2 =
                random.nextDouble() * 1000.0D;

        double noise3 =
                random.nextDouble() * 1000.0D;

        for (int step = 0; step <= steps; step++) {
            double progress =
                    step / (double) steps;

            x += Math.cos(angle) * 0.5D;
            z += Math.sin(angle) * 0.5D;

            angle +=
                    Math.sin(
                            progress * Math.PI * 2.5D
                                    + noise1
                    ) * 0.045D;

            angle +=
                    Math.sin(
                            progress * Math.PI * 6.0D
                                    + noise2
                    ) * 0.025D;

            y +=
                    Math.sin(
                            progress * Math.PI * 2.0D
                                    + noise3
                    ) * 0.035D;

            y = Mth.clamp(
                    y,
                    -72.5D,
                    -69.5D
            );

            double radiusNoise =
                    1.0D
                            + Math.sin(
                            x * 0.16D
                                    + noise1
                    ) * 0.25D
                            + Math.sin(
                            z * 0.21D
                                    + noise2
                    ) * 0.20D
                            + Math.sin(
                            (x + z) * 0.08D
                                    + noise3
                    ) * 0.15D;

            double lengthNoise =
                    0.85D
                            + Math.sin(
                            progress * Math.PI * 4.0D
                                    + noise2
                    ) * 0.20D;

            double radius =
                    baseRadius
                            * radiusNoise
                            * lengthNoise;

            double floorNoise =
                    Math.sin(
                            x * 0.20D
                                    + noise1
                    ) * 0.65D
                            + Math.sin(
                            z * 0.15D
                                    + noise2
                    ) * 0.55D
                            + Math.sin(
                            (x - z) * 0.08D
                                    + noise3
                    ) * 0.45D;

            double ceilingNoise =
                    Math.sin(
                            x * 0.16D
                                    + noise3
                    ) * 0.75D
                            + Math.sin(
                            z * 0.19D
                                    + noise1
                    ) * 0.55D
                            + Math.sin(
                            (x + z) * 0.07D
                                    + noise2
                    ) * 0.65D;

            double slope =
                    Math.sin(
                            progress * Math.PI * 4.0D
                                    + noise1
                    );

            double floor =
                    -74.0D
                            + floorNoise * 0.35D
                            + slope * 0.35D;

            double ceiling =
                    -68.0D
                            + ceilingNoise * 0.35D
                            - slope * 0.25D;

            floor =
                    Mth.clamp(
                            floor,
                            -74.0D,
                            -70.0D
                    );

            ceiling =
                    Mth.clamp(
                            ceiling,
                            -72.0D,
                            -68.0D
                    );

            if (ceiling <= floor + 2.0D) {
                ceiling = floor + 2.0D;
            }

            carveSection(
                    chunk,
                    carvingMask,
                    x,
                    z,
                    floor,
                    ceiling,
                    radius,
                    noise1,
                    noise2,
                    noise3
            );
        }
    }

    private void carveSection(
            ChunkAccess chunk,
            CarvingMask carvingMask,
            double centerX,
            double centerZ,
            double floor,
            double ceiling,
            double radius,
            double noise1,
            double noise2,
            double noise3
    ) {
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

        for (int y = MIN_Y; y <= MAX_Y; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int x = minX; x <= maxX; x++) {

                    if (!isInsideChunk(chunk, x, z)) {
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

                    if (distance > 1.0D) {
                        continue;
                    }

                    double erosion =
                            Math.sin(
                                    x * 0.45D
                                            + noise1
                            ) * 0.16D
                                    + Math.sin(
                                    z * 0.50D
                                            + noise2
                            ) * 0.16D
                                    + Math.sin(
                                    (x + z) * 0.22D
                                            + noise3
                            ) * 0.12D;

                    double edge =
                            0.82D + erosion;

                    if (distance > edge) {
                        continue;
                    }

                    double vertical =
                            (y - floor)
                                    / Math.max(
                                    0.1D,
                                    ceiling - floor
                            );

                    double horizontalSlope =
                            Math.sin(
                                    distance
                                            * Math.PI
                            );

                    double localFloor =
                            floor
                                    + horizontalSlope * 0.45D;

                    double localCeiling =
                            ceiling
                                    - horizontalSlope * 0.45D;

                    double pillarBias =
                            Math.sin(
                                    x * 0.11D
                                            + noise1
                            )
                                    * Math.sin(
                                    z * 0.13D
                                            + noise2
                            );

                    double pillarThreshold =
                            0.45D
                                    + pillarBias * 0.20D;

                    double pillarShape =
                            Math.abs(
                                    Math.sin(
                                            x * 0.055D
                                                    + z * 0.035D
                                                    + noise3
                                    )
                            );

                    if (
                            pillarShape > pillarThreshold
                                    && distance > 0.25D
                                    && vertical > 0.05D
                                    && vertical < 0.95D
                    ) {
                        continue;
                    }

                    if (
                            y + 0.5D <= localFloor
                                    || y + 0.5D >= localCeiling
                    ) {
                        continue;
                    }

                    if (
                            carvingMask.get(
                                    x & 15,
                                    y,
                                    z & 15
                            )
                    ) {
                        continue;
                    }

                    BlockPos pos =
                            new BlockPos(x, y, z);

                    if (y == LAVA_Y) {
                        chunk.setBlockState(
                                pos,
                                Blocks.LAVA.defaultBlockState(),
                                false
                        );
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
    }

    private boolean isInsideChunk(
            ChunkAccess chunk,
            int x,
            int z
    ) {
        return x >= chunk.getPos().getMinBlockX()
                && x <= chunk.getPos().getMaxBlockX()
                && z >= chunk.getPos().getMinBlockZ()
                && z <= chunk.getPos().getMaxBlockZ();
    }
}
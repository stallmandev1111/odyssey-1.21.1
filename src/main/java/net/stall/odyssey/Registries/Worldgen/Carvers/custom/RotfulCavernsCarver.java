package net.stall.odyssey.Registries.Worldgen.Carvers.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import java.util.function.Function;

public class RotfulCavernsCarver extends WorldCarver<CaveCarverConfiguration> {

    private static final int FLOOR_MIN_Y = -74;
    private static final int FLOOR_MAX_Y = -72;
    private static final int CEILING_Y = -65;

    // Smaller = larger floor shapes
    private static final double NOISE_SCALE = 0.035D;

    public RotfulCavernsCarver(Codec<CaveCarverConfiguration> codec) {
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
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();

        /*
         * Carve each X/Z column independently.
         *
         * The floor can only be:
         *
         * -74
         * -73
         * -72
         *
         * The ceiling is always -65.
         */
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                int worldX = minX + x;
                int worldZ = minZ + z;

                double noise = getNoise(
                        worldX * NOISE_SCALE,
                        worldZ * NOISE_SCALE
                );

                int floorY;

                if (noise < -0.33D) {
                    floorY = -74;
                } else if (noise < 0.33D) {
                    floorY = -73;
                } else {
                    floorY = -72;
                }

                /*
                 * Center the ellipsoid between the floor
                 * and the fixed ceiling.
                 */
                double centerY = (floorY + CEILING_Y) / 2.0D;

                double verticalRadius =
                        (CEILING_Y - floorY) / 2.0D;

                /*
                 * One ellipsoid per column.
                 *
                 * Horizontal radius is intentionally fairly large
                 * so neighboring columns overlap into one cavern.
                 */
                carveEllipsoid(
                        context,
                        config,
                        chunk,
                        biomeAccessor,
                        aquifer,

                        worldX,
                        centerY,
                        worldZ,

                        1.5D,
                        verticalRadius,

                        carvingMask,

                        (carvingContext, relativeX, relativeY, relativeZ, blockY) ->
                                blockY < floorY || blockY > CEILING_Y
                );
            }
        }

        return true;
    }

    /*
     * Smooth deterministic 2D noise.
     * Same X/Z always gives the same result.
     */
    private static double getNoise(double x, double z) {

        int x0 = Mth.floor(x);
        int z0 = Mth.floor(z);

        double fx = x - x0;
        double fz = z - z0;

        fx = smoothStep(fx);
        fz = smoothStep(fz);

        double n00 = hashNoise(x0, z0);
        double n10 = hashNoise(x0 + 1, z0);
        double n01 = hashNoise(x0, z0 + 1);
        double n11 = hashNoise(x0 + 1, z0 + 1);

        double nx0 = Mth.lerp(fx, n00, n10);
        double nx1 = Mth.lerp(fx, n01, n11);

        return Mth.lerp(fz, nx0, nx1);
    }

    private static double smoothStep(double value) {
        return value * value * (3.0D - 2.0D * value);
    }

    private static double hashNoise(int x, int z) {

        long seed =
                x * 341873128712L +
                        z * 132897987541L;

        seed ^= seed >> 13;
        seed *= 1274126177L;
        seed ^= seed >> 16;

        return ((seed & 0x7FFFFFFFL) /
                (double) 0x7FFFFFFFL) * 2.0D - 1.0D;
    }
}
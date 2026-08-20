package net.stall.odyssey.Registries.Worldgen.Carvers.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;

import java.util.function.Function;

public class RottedCavernsCarver extends WorldCarver<CaveCarverConfiguration> {

    private static final int MIN_Y = -74;
    private static final int MAX_Y = -65;

    public RottedCavernsCarver(Codec<CaveCarverConfiguration> codec) {
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
        double x = chunkPos.getMinBlockX() + 8.0D;
        double y = -69.5D;
        double z = chunkPos.getMinBlockZ() + 8.0D;

        int tunnelCount = 1 + random.nextInt(3);

        for (int tunnel = 0; tunnel < tunnelCount; tunnel++) {

            double tunnelX = x + random.nextDouble() * 8.0D - 4.0D;
            double tunnelY = -69.5D + random.nextDouble() * 4.0D;
            double tunnelZ = z + random.nextDouble() * 8.0D - 4.0D;

            float yaw = random.nextFloat() * Mth.TWO_PI;

            float pitch = (random.nextFloat() - 0.5F) * 0.12F;

            int length = 18 + random.nextInt(28);

            for (int step = 0; step < length; step++) {

                yaw += (random.nextFloat() - 0.5F) * 0.12F;
                pitch += (random.nextFloat() - 0.5F) * 0.035F;

                pitch = Mth.clamp(pitch, -0.22F, 0.22F);

                tunnelX += Math.cos(yaw) * 1.35D;
                tunnelZ += Math.sin(yaw) * 1.35D;

                tunnelY += Math.sin(pitch) * 0.55D;

                tunnelY = Mth.clamp(
                        tunnelY,
                        MIN_Y + 1.0D,
                        MAX_Y - 1.0D
                );

                double radius = 1.8D + random.nextDouble() * 1.4D;


                double verticalRadius = 1.35D + random.nextDouble() * 0.65D;

                carveTunnel(
                        context,
                        config,
                        chunk,
                        biomeAccessor,
                        aquifer,
                        carvingMask,
                        tunnelX,
                        tunnelY,
                        tunnelZ,
                        radius,
                        verticalRadius
                );
            }
        }

        return true;
    }

    private void carveTunnel(
            CarvingContext context,
            CaveCarverConfiguration config,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> biomeAccessor,
            Aquifer aquifer,
            CarvingMask carvingMask,
            double x,
            double y,
            double z,
            double horizontalRadius,
            double verticalRadius
    ) {
        WorldCarver.CarveSkipChecker skipChecker =
                (carvingContext, relativeX, relativeY, relativeZ, blockY) ->
                        blockY < MIN_Y || blockY > MAX_Y;

        carveEllipsoid(
                context,
                config,
                chunk,
                biomeAccessor,
                aquifer,
                x,
                y,
                z,
                horizontalRadius,
                verticalRadius,
                carvingMask,
                skipChecker
        );
    }
}
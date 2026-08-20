package net.stall.odyssey.Registries.Worldgen.Carvers.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import java.util.function.Function;

public class RotfulCavernsCarver extends CaveWorldCarver {

    private static final int MIN_Y = -74;
    private static final int MAX_Y = -65;

    public RotfulCavernsCarver(
            com.mojang.serialization.Codec<CaveCarverConfiguration> codec
    ) {
        super(codec);
    }

    @Override
    public boolean isStartChunk(
            CaveCarverConfiguration config,
            RandomSource random
    ) {
        // Every chunk can start a cavern.
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

        // Every chunk creates multiple tunnels.
        int tunnelCount = 2 + random.nextInt(3);

        for (int i = 0; i < tunnelCount; i++) {

            double x =
                    chunkPos.getMinBlockX()
                            + 4
                            + random.nextDouble() * 8.0D;

            double z =
                    chunkPos.getMinBlockZ()
                            + 4
                            + random.nextDouble() * 8.0D;

            /*
             * Keep the tunnels in the middle of the layer.
             *
             * The skip checker below provides the absolute safety
             * boundary of -74 through -65.
             */
            double y = -69.5D;

            float yaw = random.nextFloat() * Mth.TWO_PI;

            // Keep the tunnels mostly horizontal.
            float pitch = (random.nextFloat() - 0.5F) * 0.10F;

            float thickness =
                    1.8F + random.nextFloat() * 1.5F;

            double horizontalRadius =
                    0.8D + random.nextDouble() * 0.7D;

            double verticalRadius =
                    0.8D + random.nextDouble() * 0.4D;

            int branchCount = 1 + random.nextInt(2);

            /*
             * THIS is the important part.
             *
             * WorldCarver's normal tunnel code is used, but any block
             * outside -74..-65 is rejected.
             */
            WorldCarver.CarveSkipChecker skipChecker =
                    (carvingContext, relativeX, relativeY, relativeZ, blockY) ->
                            blockY < MIN_Y || blockY > MAX_Y;

            createTunnel(
                    context,
                    config,
                    chunk,
                    biomeAccessor,
                    random.nextLong(),
                    aquifer,
                    x,
                    y,
                    z,
                    horizontalRadius,
                    verticalRadius,
                    thickness,
                    yaw,
                    pitch,
                    0,
                    branchCount,
                    1.0D,
                    carvingMask,
                    skipChecker
            );
        }

        return true;
    }
}
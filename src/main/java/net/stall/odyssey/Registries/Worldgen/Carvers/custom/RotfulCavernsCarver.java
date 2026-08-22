package net.stall.odyssey.Registries.Worldgen.Carvers.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class RotfulCavernsCarver extends WorldCarver<CaveCarverConfiguration> {

    private static final int MIN_Y = -74;
    private static final int MAX_FLOOR_Y = -71;

    private static final int MIN_CEILING_Y = -68;
    private static final int MAX_Y = -65;

    private static final double ISLAND_FREQUENCY = 0.022;

    private static final double ISLAND_THRESHOLD = 0.15;

    private static final double ISLAND_EDGE_SOFTNESS = 0.55;

    private static final double CEILING_FREQUENCY = 0.02;
    private static final double CEILING_THRESHOLD = 0.1;
    private static final double CEILING_EDGE_SOFTNESS = 0.5;

    private static final double BUMP_FREQUENCY = 0.14;
    private static final int BUMP_HEIGHT = 3;

    private static final double CRACK_FREQUENCY = 0.07;
    private static final double CRACK_WIDTH = 0.05;
    private static final double CRACK_BEVEL_WIDTH = 0.08;
    private static final int CRACK_FLOOR_Y = MIN_Y - 1;

    private static final double CEILING_SPIKE_FREQUENCY = 0.13;
    private static final int CEILING_SPIKE_HEIGHT = 3;
    private static final double CEILING_SPIKE_SHARPNESS = 4.0;

    private static final double PILLAR_FREQUENCY = 0.045;
    private static final double PILLAR_THRESHOLD = 0.6;
    private static final double PILLAR_TWIST_RADIUS = 3.5;
    private static final double PILLAR_TWIST_RATE = 12.0;
    private static final double TENDRIL_FREQUENCY = 0.075;
    private static final double TENDRIL_THRESHOLD = 0.78;
    private static final double TENDRIL_TWIST_RADIUS = 2.25;
    private static final double TENDRIL_TWIST_RATE = 8.0;
    private static final double TENDRIL_WIDTH = 0.55;

    private static final AtomicReference<NormalNoise> ISLAND_NOISE = new AtomicReference<>();

    private static final AtomicReference<NormalNoise> CEILING_NOISE = new AtomicReference<>();

    private static final AtomicReference<NormalNoise> BUMP_NOISE = new AtomicReference<>();

    private static final AtomicReference<NormalNoise> CEILING_SPIKE_NOISE = new AtomicReference<>();

    private static final AtomicReference<NormalNoise> PILLAR_NOISE = new AtomicReference<>();
    private static final AtomicReference<NormalNoise> TENDRIL_NOISE = new AtomicReference<>();

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

        if (!chunkPos.equals(chunk.getPos())) {
            return false;
        }

        boolean carved = false;

        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();

        NormalNoise islandNoise = getOrCreateIslandNoise(random);
        NormalNoise ceilingNoise = getOrCreateCeilingNoise(random);
        NormalNoise bumpNoise = getOrCreateBumpNoise(random);
        NormalNoise ceilingSpikeNoise = getOrCreateCeilingSpikeNoise(random);
        NormalNoise pillarNoise = getOrCreatePillarNoise(random);
        NormalNoise tendrilNoise = getOrCreateTendrilNoise(random);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                int worldX = minX + x;
                int worldZ = minZ + z;

                int floorY = computeFloorY(islandNoise, bumpNoise, worldX, worldZ);
                int ceilingY = computeCeilingY(ceilingNoise, ceilingSpikeNoise, worldX, worldZ);

                for (int y = floorY; y <= ceilingY; y++) {

                    if (carvingMask.get(x, y, z)) {
                        continue;
                    }

                    if (isPillar(pillarNoise, worldX, y, worldZ)) {

                        continue;
                    }

                    BlockPos pos = new BlockPos(worldX, y, worldZ);

                    if (!canReplaceBlock(config, chunk.getBlockState(pos))) {
                        continue;
                    }

                    carvingMask.set(x, y, z);

                    chunk.setBlockState(
                            pos,
                            Blocks.CAVE_AIR.defaultBlockState(),
                            false
                    );

                    carved = true;
                }

                for (int waterY = CRACK_FLOOR_Y; waterY <= MIN_Y; waterY++) {
                    BlockPos waterPos = new BlockPos(worldX, waterY, worldZ);
                    if (chunk.getBlockState(waterPos).isAir()) {
                        chunk.setBlockState(
                                waterPos,
                                Blocks.WATER.defaultBlockState(),
                                false
                        );
                    }
                }
            }
        }

        return carved;
    }

    private NormalNoise getOrCreateIslandNoise(RandomSource random) {
        NormalNoise existing = ISLAND_NOISE.get();
        if (existing != null) {
            return existing;
        }

        NormalNoise created = NormalNoise.create(random, 0, 1.0);
        return ISLAND_NOISE.compareAndSet(null, created) ? created : ISLAND_NOISE.get();
    }

    private NormalNoise getOrCreateCeilingNoise(RandomSource random) {
        NormalNoise existing = CEILING_NOISE.get();
        if (existing != null) {
            return existing;
        }

        NormalNoise created = NormalNoise.create(random, 0, 1.0);
        return CEILING_NOISE.compareAndSet(null, created) ? created : CEILING_NOISE.get();
    }

    private NormalNoise getOrCreateBumpNoise(RandomSource random) {
        NormalNoise existing = BUMP_NOISE.get();
        if (existing != null) {
            return existing;
        }

        NormalNoise created = NormalNoise.create(random, 0, 1.0);
        return BUMP_NOISE.compareAndSet(null, created) ? created : BUMP_NOISE.get();
    }

    private NormalNoise getOrCreateCeilingSpikeNoise(RandomSource random) {
        NormalNoise existing = CEILING_SPIKE_NOISE.get();
        if (existing != null) {
            return existing;
        }

        NormalNoise created = NormalNoise.create(random, 0, 1.0);
        return CEILING_SPIKE_NOISE.compareAndSet(null, created) ? created : CEILING_SPIKE_NOISE.get();
    }

    private NormalNoise getOrCreatePillarNoise(RandomSource random) {
        NormalNoise existing = PILLAR_NOISE.get();
        if (existing != null) {
            return existing;
        }

        NormalNoise created = NormalNoise.create(random, 0, 1.0);
        return PILLAR_NOISE.compareAndSet(null, created) ? created : PILLAR_NOISE.get();
    }

    private NormalNoise getOrCreateTendrilNoise(RandomSource random) {
        NormalNoise existing = TENDRIL_NOISE.get();
        if (existing != null) {
            return existing;
        }

        NormalNoise created = NormalNoise.create(random, 0, 1.0);
        return TENDRIL_NOISE.compareAndSet(null, created) ? created : TENDRIL_NOISE.get();
    }

    private boolean isTendril(NormalNoise noise, int worldX, int y, int worldZ) {
        double angle = y / TENDRIL_TWIST_RATE;
        double offsetX = Math.cos(angle) * TENDRIL_TWIST_RADIUS;
        double offsetZ = Math.sin(angle) * TENDRIL_TWIST_RADIUS;

        double n = noise.getValue(
                (worldX + offsetX) * TENDRIL_FREQUENCY,
                400.0,
                (worldZ + offsetZ) * TENDRIL_FREQUENCY
        );

        return n > TENDRIL_THRESHOLD;
    }

    private int computeFloorY(NormalNoise islandNoise, NormalNoise bumpNoise, int worldX, int worldZ) {
        double n = islandNoise.getValue(
                worldX * ISLAND_FREQUENCY,
                0.0,
                worldZ * ISLAND_FREQUENCY
        );

        double t = (n - ISLAND_THRESHOLD) / ISLAND_EDGE_SOFTNESS;
        t = Mth.clamp(t, 0.0, 1.0);

        double islandFactor = t * t * (3.0 - 2.0 * t);

        int range = MAX_FLOOR_Y - MIN_Y;
        int baseFloorY = MIN_Y + (int) Math.round(islandFactor * range);

        if (baseFloorY == MIN_Y) {
            double crackDistance = crackDistance(bumpNoise, worldX, worldZ);

            if (crackDistance < CRACK_WIDTH + CRACK_BEVEL_WIDTH) {

                return crackFloorY(crackDistance);
            }

            baseFloorY = MIN_Y + 1;
        }

        return baseFloorY + computeBump(bumpNoise, worldX, worldZ);
    }

    private double crackDistance(NormalNoise noise, int worldX, int worldZ) {
        double n = noise.getValue(
                worldX * CRACK_FREQUENCY,
                200.0,
                worldZ * CRACK_FREQUENCY
        );

        return Math.abs(n);
    }

    private int crackFloorY(double distance) {
        if (distance <= CRACK_WIDTH) {
            return CRACK_FLOOR_Y;
        }

        double t = (distance - CRACK_WIDTH) / CRACK_BEVEL_WIDTH;
        t = Mth.clamp(t, 0.0, 1.0);

        double smoothed = t * t * (3.0 - 2.0 * t);

        int solidFloorY = MIN_Y + 1;
        return CRACK_FLOOR_Y + (int) Math.round(smoothed * (solidFloorY - CRACK_FLOOR_Y));
    }

    private int computeBump(NormalNoise noise, int worldX, int worldZ) {
        double n = noise.getValue(
                worldX * BUMP_FREQUENCY,
                50.0,
                worldZ * BUMP_FREQUENCY
        );

        double normalized = Mth.clamp((n + 1.0) / 2.0, 0.0, 1.0);
        return (int) Math.round(normalized * BUMP_HEIGHT);
    }

    private boolean isPillar(NormalNoise noise, int worldX, int y, int worldZ) {
        double angle = y / PILLAR_TWIST_RATE;
        double offsetX = Math.cos(angle) * PILLAR_TWIST_RADIUS;
        double offsetZ = Math.sin(angle) * PILLAR_TWIST_RADIUS;

        double n = noise.getValue(
                (worldX + offsetX) * PILLAR_FREQUENCY,
                0.0,
                (worldZ + offsetZ) * PILLAR_FREQUENCY
        );

        return n > PILLAR_THRESHOLD;
    }

    private int computeCeilingY(NormalNoise noise, NormalNoise spikeNoise, int worldX, int worldZ) {
        double n = noise.getValue(
                worldX * CEILING_FREQUENCY,
                100.0,
                worldZ * CEILING_FREQUENCY
        );

        double t = (n - CEILING_THRESHOLD) / CEILING_EDGE_SOFTNESS;
        t = Mth.clamp(t, 0.0, 1.0);

        double sagFactor = t * t * (3.0 - 2.0 * t);

        int range = MAX_Y - MIN_CEILING_Y;
        int baseCeilingY = MAX_Y - (int) Math.round(sagFactor * range);

        return baseCeilingY - computeCeilingSpike(spikeNoise, worldX, worldZ);
    }

    private int computeCeilingSpike(NormalNoise noise, int worldX, int worldZ) {
        double n = noise.getValue(
                worldX * CEILING_SPIKE_FREQUENCY,
                300.0,
                worldZ * CEILING_SPIKE_FREQUENCY
        );

        double ridge = Mth.clamp(1.0 - Math.abs(n), 0.0, 1.0);
        double sharpened = Math.pow(ridge, CEILING_SPIKE_SHARPNESS);

        return (int) Math.round(sharpened * CEILING_SPIKE_HEIGHT);
    }
}
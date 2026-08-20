package net.stall.odyssey.Registries.Biome.Carvers.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.ChunkPos;

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
        return random.nextFloat() <= config.probability;
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

        createRoom(
                context,
                config,
                chunk,
                biomeAccessor,
                aquifer,
                x,
                y,
                z,
                10.0F,
                1.0D,
                carvingMask,
                (context1, relativeX, relativeY, relativeZ, y1) -> false
        );

        return true;
    }
}
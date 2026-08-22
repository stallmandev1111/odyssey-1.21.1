package net.stall.odyssey.Registries.Worldgen.Features.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.stall.odyssey.Registries.Worldgen.Features.config.RotfulBlobConfiguration;

public class RotfulBlobFeature extends Feature<RotfulBlobConfiguration> {

    private static final int MIN_Y = -74;
    private static final int MAX_Y = -65;

    public RotfulBlobFeature() {
        super(RotfulBlobConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<RotfulBlobConfiguration> context) {

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        RotfulBlobConfiguration config = context.config();

        int radius = config.radius();

        boolean placedAny = false;

        // Get a random replacement block from the configured set.
        Holder<Block> replacementHolder =
                config.replace().getRandomElement(random).orElse(null);

        if (replacementHolder == null) {
            return false;
        }

        Block replacement = replacementHolder.value();

        // Search for the cavern floor.
        for (int y = MAX_Y; y >= MIN_Y; y--) {

            BlockPos center = new BlockPos(
                    origin.getX(),
                    y,
                    origin.getZ()
            );

            // Must have air above.
            if (!level.isEmptyBlock(center.above())) {
                continue;
            }

            // Must be one of the configured target blocks.
            if (!config.target().contains(level.getBlockState(center).getBlock().builtInRegistryHolder())) {
                continue;
            }

            // Generate the blob.
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {

                    if ((x * x) + (z * z) > radius * radius) {
                        continue;
                    }

                    // Roughen the blob edges.
                    if (random.nextFloat() > 0.75F) {
                        continue;
                    }

                    BlockPos pos = center.offset(x, 0, z);

                    // Must still be a target block.
                    if (!config.target().contains(
                            level.getBlockState(pos).getBlock().builtInRegistryHolder())) {
                        continue;
                    }

                    // Must have air above it.
                    if (!level.isEmptyBlock(pos.above())) {
                        continue;
                    }

                    level.setBlock(
                            pos,
                            replacement.defaultBlockState(),
                            2
                    );

                    placedAny = true;
                }
            }

            // Only one blob per feature attempt.
            break;
        }

        return placedAny;
    }
}
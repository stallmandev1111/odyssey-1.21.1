package net.stall.odyssey.Registries.Worldgen.Features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.resources.HolderSetCodec;

public record RotfulBlobConfiguration(
        int radius,
        HolderSet<Block> target,
        HolderSet<Block> replace
) implements FeatureConfiguration {

    public static final Codec<RotfulBlobConfiguration> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(

                    Codec.INT
                            .fieldOf("radius")
                            .forGetter(RotfulBlobConfiguration::radius),

                    HolderSetCodec.create(
                                    BuiltInRegistries.BLOCK.key(),
                                    BuiltInRegistries.BLOCK.holderByNameCodec(),
                                    false
                            )
                            .fieldOf("target")
                            .forGetter(RotfulBlobConfiguration::target),

                    HolderSetCodec.create(
                                    BuiltInRegistries.BLOCK.key(),
                                    BuiltInRegistries.BLOCK.holderByNameCodec(),
                                    false
                            )
                            .fieldOf("replace")
                            .forGetter(RotfulBlobConfiguration::replace)

            ).apply(instance, RotfulBlobConfiguration::new));
}
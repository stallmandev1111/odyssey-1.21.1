package net.stall.odyssey.Registries.Biome.Biomesources.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

import java.util.stream.Stream;

public final class RotfulCavernsBiomeSource extends BiomeSource {

    public static final MapCodec<RotfulCavernsBiomeSource> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    MultiNoiseBiomeSourceParameterList.CODEC
                            .fieldOf("overworld_params")
                            .forGetter(source -> source.overworldParams),

                    Biome.CODEC
                            .fieldOf("cavern_biome")
                            .forGetter(source -> source.cavernBiome)

            ).apply(instance, RotfulCavernsBiomeSource::new));

    private static final int CAVERN_MIN_Y = -80;
    private static final int CAVERN_MAX_Y = -64;

    private final Holder<MultiNoiseBiomeSourceParameterList> overworldParams;
    private final Holder<Biome> cavernBiome;
    private final MultiNoiseBiomeSource delegate;

    public RotfulCavernsBiomeSource(
            Holder<MultiNoiseBiomeSourceParameterList> overworldParams,
            Holder<Biome> cavernBiome
    ) {
        this.overworldParams = overworldParams;
        this.cavernBiome = cavernBiome;

        this.delegate = MultiNoiseBiomeSource.createFromList(
                overworldParams.value().parameters()
        );
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.concat(
                delegate.possibleBiomes().stream(),
                Stream.of(cavernBiome)
        );
    }

    @Override
    public Holder<Biome> getNoiseBiome(
            int quartX,
            int quartY,
            int quartZ,
            Climate.Sampler sampler
    ) {
        final int blockY = QuartPos.toBlock(quartY);

        if (blockY >= CAVERN_MIN_Y && blockY <= CAVERN_MAX_Y) {
            return cavernBiome;
        }

        return delegate.getNoiseBiome(
                quartX,
                quartY,
                quartZ,
                sampler
        );
    }
}

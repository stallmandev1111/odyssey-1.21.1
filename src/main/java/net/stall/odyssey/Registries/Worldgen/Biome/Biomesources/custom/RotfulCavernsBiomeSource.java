package net.stall.odyssey.Registries.Worldgen.Biome.Biomesources.custom;

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

    /*
     * Rotful Caverns occupies the deep underground layer.
     *
     * Y -60 through Y -20:
     *     Rotful Caverns
     *
     * Above Y -20:
     *     Normal Overworld biomes
     */
    private static final int CAVERN_MIN_Y = -80;
    private static final int CAVERN_MAX_Y = -64;

    private final Holder<MultiNoiseBiomeSourceParameterList> overworldParams;
    private final Holder<Biome> cavernBiome;
    private final MultiNoiseBiomeSource overworldDelegate;

    public RotfulCavernsBiomeSource(
            Holder<MultiNoiseBiomeSourceParameterList> overworldParams,
            Holder<Biome> cavernBiome
    ) {
        this.overworldParams = overworldParams;
        this.cavernBiome = cavernBiome;

        this.overworldDelegate = MultiNoiseBiomeSource.createFromList(
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
                overworldDelegate.possibleBiomes().stream(),
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

        return overworldDelegate.getNoiseBiome(
                quartX,
                quartY,
                quartZ,
                sampler
        );
    }
}
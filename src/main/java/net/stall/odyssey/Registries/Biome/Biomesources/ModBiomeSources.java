package net.stall.odyssey.Registries.Biome.Biomesources;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.stall.odyssey.Odyssey;
import net.stall.odyssey.Registries.Biome.Biomesources.custom.RotfulCavernsBiomeSource;

public final class ModBiomeSources {

    private ModBiomeSources() {
    }

    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(
                    Registries.BIOME_SOURCE,
                    Odyssey.MODID
            );

    public static final DeferredHolder<
            MapCodec<? extends BiomeSource>,
            MapCodec<RotfulCavernsBiomeSource>
            > ROTFUL_CAVERNS_BIOME_SOURCE =
            BIOME_SOURCES.register(
                    "rotful_caverns_biome_source",
                    () -> RotfulCavernsBiomeSource.CODEC
            );

    public static void register(IEventBus eventBus) {
        BIOME_SOURCES.register(eventBus);
    }
}



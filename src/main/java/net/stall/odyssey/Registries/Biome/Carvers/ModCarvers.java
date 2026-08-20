package net.stall.odyssey.Registries.Biome.Carvers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.stall.odyssey.Odyssey;
import net.stall.odyssey.Registries.Biome.Carvers.custom.RotfulCavernsCarver;

public final class ModCarvers {

    public static final DeferredRegister<WorldCarver<?>> CARVERS =
            DeferredRegister.create(
                    Registries.CARVER,
                    Odyssey.MODID
            );

    public static final DeferredHolder<
            WorldCarver<?>,
            RotfulCavernsCarver
            > ROTFUL_CAVERNS_CARVER =
            CARVERS.register(
                    "rotful_caverns_carver",
                    () -> new RotfulCavernsCarver(
                            CaveCarverConfiguration.CODEC
                    )
            );

    public static void register(IEventBus eventBus) {
        CARVERS.register(eventBus);
    }
}
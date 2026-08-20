package net.stall.odyssey.Registries.Worldgen.Carvers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.stall.odyssey.Registries.Worldgen.Carvers.custom.RottedDepthsCarver;

public class ModCarvers {

    public static final DeferredRegister<WorldCarver<?>> CARVERS =
            DeferredRegister.create(Registries.CARVER, "odyssey");

    public static final DeferredHolder<WorldCarver<?>, RottedDepthsCarver> UNDERWORLD_VOID =
            CARVERS.register(
                    "rotted_depths",
                    RottedDepthsCarver::new
            );

    public static void register(IEventBus eventBus) {
        CARVERS.register(eventBus);
    }
}

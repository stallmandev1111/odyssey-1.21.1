package net.stall.odyssey.Registries.Worldgen.Carvers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.stall.odyssey.Odyssey;

public class ModCarvers {
    public static final DeferredRegister<WorldCarver<?>> CARVERS =
        DeferredRegister.create(BuiltInRegistries.CARVER, Odyssey.MODID);
    public static final DeferredHolder<WorldCarver<?>, FullAirCarver> ROTTED_CAVERNS_CARVER =
        CARVERS.register("rotted_caverns_carver", () -> new FullAirCarver(CarverConfiguration.CODEC));
}
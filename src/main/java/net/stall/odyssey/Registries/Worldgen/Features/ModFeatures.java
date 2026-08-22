package net.stall.odyssey.Registries.Worldgen.Features;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.stall.odyssey.Registries.Worldgen.Features.custom.RotfulBlobFeature;


public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE, "odyssey");

    public static final DeferredHolder<Feature<?>, RotfulBlobFeature> ROTFUL_BLOB =
            FEATURES.register("rotful_blob", RotfulBlobFeature::new);

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
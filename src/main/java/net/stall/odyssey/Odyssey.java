package net.stall.odyssey;


import net.stall.odyssey.Registries.Worldgen.Biome.Biomesources.ModBiomeSources;
import net.stall.odyssey.Registries.Worldgen.Carvers.ModCarvers;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;


@Mod(Odyssey.MODID)
public class Odyssey {
    public static final String MODID = "odyssey";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Odyssey(IEventBus modEventBus, ModContainer modContainer) {
        ModBiomeSources.register(modEventBus);

    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }


    /*private void addCreative(BuildCreativeModeTabContentsEvent event) {
        OItems.setupTabEditors(event);
    }*/

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}

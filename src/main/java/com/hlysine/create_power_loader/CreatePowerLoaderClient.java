package com.hlysine.create_power_loader;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLModContainer;

@Mod(value = CreatePowerLoader.MODID, dist = Dist.CLIENT)
public class CreatePowerLoaderClient {
    public CreatePowerLoaderClient(FMLModContainer container, IEventBus modBus, Dist dist) {
        CPLPartialModels.register();
        modBus.addListener(CreatePowerLoaderClient::init);
    }

    public static void init(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new CPLPonders());
    }
}

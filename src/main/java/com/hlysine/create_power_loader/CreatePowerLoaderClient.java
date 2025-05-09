package com.hlysine.create_power_loader;


import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLLoader;

@EventBusSubscriber(Dist.CLIENT)
public class CreatePowerLoaderClient {
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        if (FMLLoader.getDist() != Dist.CLIENT) return;

        CPLPartialModels.register();
        modEventBus.addListener(CreatePowerLoaderClient::init);
    }

    public static void init(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new CPLPonders());
    }
}

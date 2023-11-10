package com.hlysine.create_power_loader;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class CreatePowerLoaderClient {
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(CreatePowerLoaderClient::init);
    }
    public static void init(final FMLClientSetupEvent event) {
        CPLPartialModels.register();
        CPLPonders.register();
    }
}

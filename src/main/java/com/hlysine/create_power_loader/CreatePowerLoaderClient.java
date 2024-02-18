package com.hlysine.create_power_loader;


import net.fabricmc.api.ClientModInitializer;

public class CreatePowerLoaderClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CPLPartialModels.register();
        CPLPonders.register();
    }
}

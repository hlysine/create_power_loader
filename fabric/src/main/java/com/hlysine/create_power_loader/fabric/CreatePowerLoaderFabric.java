package com.hlysine.create_power_loader.fabric;

import com.hlysine.create_power_loader.CreatePowerLoader;
import net.fabricmc.api.ModInitializer;

public class CreatePowerLoaderFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CreatePowerLoader.init();
    }
}

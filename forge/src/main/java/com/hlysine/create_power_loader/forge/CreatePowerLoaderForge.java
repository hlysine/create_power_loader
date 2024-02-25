package com.hlysine.create_power_loader.forge;

import com.hlysine.create_power_loader.CreatePowerLoader;
import net.minecraftforge.fml.common.Mod;

@Mod(CreatePowerLoader.MOD_ID)
public class CreatePowerLoaderForge {
    public CreatePowerLoaderForge() {
        CreatePowerLoader.init();
    }
}

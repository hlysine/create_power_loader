package com.hlysine.create_power_loader;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CPLCreativeTab {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "create_power_loader" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreatePowerLoader.MODID);
    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.REDSTONE_BLOCKS)
            .icon(CPLBlocks.MECHANICAL_CHUNK_LOADER::asStack)
            .build());
}

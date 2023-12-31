package com.hlysine.create_power_loader;

import com.simibubi.create.AllCreativeModeTabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CPLCreativeTabs {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "create_power_loader" namespace
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreatePowerLoader.MODID);
    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_power_loader.main"))
            .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
            .icon(CPLBlocks.BRASS_CHUNK_LOADER::asStack)
            .displayItems((params, output) -> {
                output.accept(CPLBlocks.EMPTY_ANDESITE_CHUNK_LOADER.asStack());
                output.accept(CPLBlocks.ANDESITE_CHUNK_LOADER.asStack());
                output.accept(CPLBlocks.EMPTY_BRASS_CHUNK_LOADER.asStack());
                output.accept(CPLBlocks.BRASS_CHUNK_LOADER.asStack());
            })
            .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}

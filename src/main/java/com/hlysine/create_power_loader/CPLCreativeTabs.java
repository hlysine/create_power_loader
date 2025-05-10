package com.hlysine.create_power_loader;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CPLCreativeTabs {
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "create_power_loader" namespace
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreatePowerLoader.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_power_loader.main"))
            .withTabsBefore(ResourceLocation.fromNamespaceAndPath("create", "palettes"))
            .icon(() -> new ItemStack(CPLBlocks.BRASS_CHUNK_LOADER.get()))
            .displayItems((params, output) -> {
                output.accept(new ItemStack(CPLBlocks.EMPTY_ANDESITE_CHUNK_LOADER), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
                output.accept(new ItemStack(CPLBlocks.EMPTY_BRASS_CHUNK_LOADER), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
                output.accept(new ItemStack(CPLBlocks.ANDESITE_CHUNK_LOADER), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
                output.accept(new ItemStack(CPLBlocks.BRASS_CHUNK_LOADER), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
            })
            .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}

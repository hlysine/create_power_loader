package com.hlysine.create_power_loader;

import com.simibubi.create.AllCreativeModeTabs;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

public class CPLCreativeTabs {
    public static final AllCreativeModeTabs.TabInfo MAIN = register("main", () -> FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.create_power_loader.main"))
            .icon(CPLBlocks.BRASS_CHUNK_LOADER::asStack)
            .displayItems((params, output) -> {
                output.accept(CPLBlocks.EMPTY_ANDESITE_CHUNK_LOADER.asStack());
                output.accept(CPLBlocks.ANDESITE_CHUNK_LOADER.asStack());
                output.accept(CPLBlocks.EMPTY_BRASS_CHUNK_LOADER.asStack());
                output.accept(CPLBlocks.BRASS_CHUNK_LOADER.asStack());
            })
            .build());

    private static AllCreativeModeTabs.TabInfo register(String name, Supplier<CreativeModeTab> supplier) {
        ResourceLocation id = CreatePowerLoader.asResource(name);
        ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, id);
        CreativeModeTab tab = supplier.get();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab);
        return new AllCreativeModeTabs.TabInfo(key, tab);
    }

    public static void register() {

    }
}

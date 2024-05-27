package com.hlysine.create_power_loader;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import io.github.fabricators_of_create.porting_lib.util.ItemGroupUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CPLCreativeTabs {
    public static final CreativeModeTab MAIN = new MainCreativeModeTab();

    public static final List<ItemProviderEntry<?>> ITEMS = List.of(
            CPLBlocks.EMPTY_ANDESITE_CHUNK_LOADER,
            CPLBlocks.ANDESITE_CHUNK_LOADER,
            CPLBlocks.EMPTY_BRASS_CHUNK_LOADER,
            CPLBlocks.BRASS_CHUNK_LOADER
    );

    public static void register() {
    }

    public static class MainCreativeModeTab extends CreativeModeTab {

        public MainCreativeModeTab() {
            super(ItemGroupUtil.expandArrayAndGetId(), CreatePowerLoader.MODID + ".main");
        }

        @Override
        public @NotNull ItemStack makeIcon() {
            return CPLBlocks.BRASS_CHUNK_LOADER.asStack();
        }

        @Override
        public void fillItemList(@NotNull NonNullList<ItemStack> pItems) {
            for (ItemProviderEntry<?> item : ITEMS) {
                item.get().asItem().fillItemCategory(this, pItems);
            }
        }
    }
}

package com.hlysine.create_power_loader.content.andesitechunkloader;

import com.hlysine.create_power_loader.CPLBlockEntityTypes;
import com.hlysine.create_power_loader.content.AbstractChunkLoaderBlock;
import com.hlysine.create_power_loader.content.LoaderType;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AndesiteChunkLoaderBlock extends AbstractChunkLoaderBlock implements IBE<AndesiteChunkLoaderBlockEntity> {
    public AndesiteChunkLoaderBlock(Properties properties) {
        super(properties, LoaderType.ANDESITE);
    }

    @Override
    public Class<AndesiteChunkLoaderBlockEntity> getBlockEntityClass() {
        return AndesiteChunkLoaderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AndesiteChunkLoaderBlockEntity> getBlockEntityType() {
        return CPLBlockEntityTypes.ANDESITE_CHUNK_LOADER.get();
    }
}

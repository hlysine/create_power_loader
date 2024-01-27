package com.hlysine.create_power_loader.content.brasschunkloader;

import com.hlysine.create_power_loader.CPLBlockEntityTypes;
import com.hlysine.create_power_loader.content.AbstractChunkLoaderBlock;
import com.hlysine.create_power_loader.content.LoaderType;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BrassChunkLoaderBlock extends AbstractChunkLoaderBlock implements IBE<BrassChunkLoaderBlockEntity> {
    public BrassChunkLoaderBlock(Properties properties) {
        super(properties, LoaderType.BRASS);
    }

    @Override
    public Class<BrassChunkLoaderBlockEntity> getBlockEntityClass() {
        return BrassChunkLoaderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BrassChunkLoaderBlockEntity> getBlockEntityType() {
        return CPLBlockEntityTypes.BRASS_CHUNK_LOADER.get();
    }
}

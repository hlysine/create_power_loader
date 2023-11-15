package com.hlysine.create_power_loader.content.emptybrasschunkloader.brasschunkloader;

import com.hlysine.create_power_loader.CPLBlockEntityTypes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class EmptyBrassChunkLoaderBlock extends DirectionalKineticBlock implements IBE<EmptyBrassChunkLoaderBlockEntity> {

    public EmptyBrassChunkLoaderBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING).getOpposite();
    }

    @Override
    public Class<EmptyBrassChunkLoaderBlockEntity> getBlockEntityClass() {
        return EmptyBrassChunkLoaderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends EmptyBrassChunkLoaderBlockEntity> getBlockEntityType() {
        return CPLBlockEntityTypes.EMPTY_BRASS_CHUNK_LOADER.get();
    }

}

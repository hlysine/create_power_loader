package com.hlysine.create_power_loader.content.emptychunkloader;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class EmptyChunkLoaderBlock extends DirectionalKineticBlock implements IBE<EmptyChunkLoaderBlockEntity> {

    protected final BlockEntityEntry<EmptyChunkLoaderBlockEntity> entityEntry;

    public EmptyChunkLoaderBlock(Properties properties, BlockEntityEntry<EmptyChunkLoaderBlockEntity> entityEntry) {
        super(properties);
        this.entityEntry = entityEntry;
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
    public Class<EmptyChunkLoaderBlockEntity> getBlockEntityClass() {
        return EmptyChunkLoaderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends EmptyChunkLoaderBlockEntity> getBlockEntityType() {
        return entityEntry.get();
    }
}

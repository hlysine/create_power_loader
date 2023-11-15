package com.hlysine.create_power_loader.content.emptychunkloader;


import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@MethodsReturnNonnullByDefault
public class EmptyChunkLoaderBlockEntity extends KineticBlockEntity {

    public EmptyChunkLoaderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}

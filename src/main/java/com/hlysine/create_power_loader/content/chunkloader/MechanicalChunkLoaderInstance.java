package com.hlysine.create_power_loader.content.chunkloader;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MechanicalChunkLoaderInstance extends SingleRotatingInstance<MechanicalChunkLoaderBlockEntity> {

    public MechanicalChunkLoaderInstance(MaterialManager materialManager, MechanicalChunkLoaderBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        BlockState referenceState = blockEntity.getBlockState();
        Direction facing = referenceState.getValue(BlockStateProperties.FACING).getOpposite();
        return getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, referenceState, facing);
    }
}

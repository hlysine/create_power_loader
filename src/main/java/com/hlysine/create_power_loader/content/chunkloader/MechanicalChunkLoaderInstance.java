package com.hlysine.create_power_loader.content.chunkloader;

import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class MechanicalChunkLoaderInstance extends KineticBlockEntityInstance<MechanicalChunkLoaderBlockEntity> {

    protected final RotatingData shaft;
    final Direction direction;
    private final Direction opposite;

    public MechanicalChunkLoaderInstance(MaterialManager materialManager, MechanicalChunkLoaderBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        direction = blockState.getValue(FACING);

        opposite = direction.getOpposite();
        shaft = getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, blockState, opposite).createInstance();

        setup(shaft);
    }

    @Override
    public void update() {
        updateRotation(shaft);
    }

    @Override
    public void updateLight() {
        BlockPos behind = pos.relative(opposite);
        relight(behind, shaft);
    }

    @Override
    public void remove() {
        shaft.delete();
    }
}

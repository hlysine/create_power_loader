package com.hlysine.create_power_loader.content.brasschunkloader;


import com.hlysine.create_power_loader.CPLIcons;
import com.hlysine.create_power_loader.CreatePowerLoader;
import com.hlysine.create_power_loader.config.CPLConfigs;
import com.hlysine.create_power_loader.content.AbstractChunkLoaderBlockEntity;
import com.hlysine.create_power_loader.content.AbstractChunkLoaderBlock;
import com.hlysine.create_power_loader.content.LoaderType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.hlysine.create_power_loader.content.AbstractChunkLoaderBlock.ATTACHED;

@MethodsReturnNonnullByDefault
public class BrassChunkLoaderBlockEntity extends AbstractChunkLoaderBlockEntity {

    protected ScrollOptionBehaviour<LoadingRange> loadingRange;

    public BrassChunkLoaderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, LoaderType.BRASS);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

        loadingRange = new ScrollOptionBehaviour<>(LoadingRange.class,
                Component.translatable(CreatePowerLoader.MODID + ".brass_chunk_loader.loading_range"), this, new LoadingRangeValueBox());
        loadingRange.value = 0;
        loadingRange.withCallback(i -> {
            boolean server = (!level.isClientSide || isVirtual()) && (level instanceof ServerLevel);
            if (server)
                updateForcedChunks();
        });
        loadingRange.onlyActiveWhen(() -> !getBlockState().getValue(ATTACHED));
        behaviours.add(loadingRange);
    }

    @Override
    public int getLoadingRange() {
        return loadingRange.getValue() + 1;
    }

    public void setLoadingRange(int range) {
        loadingRange.setValue(range - 1);
    }

    @Override
    protected double getSpeedMultiplierConfig() {
        return CPLConfigs.server().brassSpeedMultiplier.get();
    }

    private static class LoadingRangeValueBox extends CenteredSideValueBoxTransform {
        public LoadingRangeValueBox() {
            super((blockState, direction) -> {
                Direction facing = blockState.getValue(AbstractChunkLoaderBlock.FACING);
                return facing.getAxis() != direction.getAxis();
            });
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 15.5);
        }

        @Override
        public Vec3 getLocalOffset(BlockState state) {
            Direction facing = state.getValue(AbstractChunkLoaderBlock.FACING);
            return super.getLocalOffset(state).add(Vec3.atLowerCornerOf(facing.getNormal())
                    .scale(-4 / 16f));
        }


        @Override
        public float getScale() {
            return super.getScale();
        }
    }

    public enum LoadingRange implements INamedIconOptions {
        LOAD_1x1(CPLIcons.I_1x1),
        LOAD_3x3(CPLIcons.I_3x3),
        LOAD_5x5(CPLIcons.I_5x5),
        ;

        private final String translationKey;
        private final AllIcons icon;

        LoadingRange(AllIcons icon) {
            this.icon = icon;
            this.translationKey = CreatePowerLoader.MODID + ".brass_chunk_loader." + Lang.asId(name());
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return translationKey;
        }
    }
}

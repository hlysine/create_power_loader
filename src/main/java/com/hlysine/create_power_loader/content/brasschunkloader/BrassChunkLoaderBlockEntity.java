package com.hlysine.create_power_loader.content.brasschunkloader;


import com.hlysine.create_power_loader.CPLIcons;
import com.hlysine.create_power_loader.CreatePowerLoader;
import com.hlysine.create_power_loader.config.CPLConfigs;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@MethodsReturnNonnullByDefault
public class BrassChunkLoaderBlockEntity extends KineticBlockEntity {

    protected int chunkUpdateCooldown;
    protected int chunkUnloadCooldown;
    protected BlockPos lastBlockPos;
    protected boolean lastSpeedRequirement;
    protected int lastRange;

    protected Set<ChunkPos> forcedChunks = new HashSet<>();

    protected ScrollOptionBehaviour<LoadingRange> loadingRange;

    public BrassChunkLoaderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
        behaviours.add(loadingRange);
    }

    @Override
    public void tick() {
        super.tick();

        boolean server = (!level.isClientSide || isVirtual()) && (level instanceof ServerLevel);

        if (!server) {
            spawnParticles();
        }

        if (server && chunkUpdateCooldown-- <= 0) {
            chunkUpdateCooldown = CPLConfigs.server().chunkUpdateInterval.get();
            if (needsUpdate()) {
                setChanged();
                updateForcedChunks();
            }
        }
    }

    @Override
    public boolean isSpeedRequirementFulfilled() {
        if (!super.isSpeedRequirementFulfilled())
            return false;

        BlockState state = getBlockState();
        if (!(getBlockState().getBlock() instanceof IRotate))
            return true;
        IRotate def = (IRotate) state.getBlock();
        IRotate.SpeedLevel minimumRequiredSpeedLevel = def.getMinimumRequiredSpeedLevel();
        float minSpeed = minimumRequiredSpeedLevel.getSpeedValue();

        double requirement = minSpeed * (float) Math.pow(2, getLoadingRange()) * CPLConfigs.server().brassSpeedMultiplier.get();
        return Math.abs(getSpeed()) >= requirement;
    }

    public int getLoadingRange() {
        return loadingRange.getValue() + 1;
    }

    public void setLoadingRange(int range) {
        loadingRange.setValue(range - 1);
    }

    private boolean needsUpdate() {
        if (lastBlockPos == null) return true;
        return !lastBlockPos.equals(getBlockPos()) || lastSpeedRequirement != isSpeedRequirementFulfilled() || lastRange != getLoadingRange() || chunkUnloadCooldown > 0;
    }

    private void updateForcedChunks() {
        boolean resetStates = true;
        if (isSpeedRequirementFulfilled()) {
            ChunkLoadingUtils.updateForcedChunks((ServerLevel) level, new ChunkPos(getBlockPos()), getBlockPos(), getLoadingRange(), forcedChunks);
        } else if (chunkUnloadCooldown >= CPLConfigs.server().unloadGracePeriod.get()) {
            ChunkLoadingUtils.unforceAllChunks((ServerLevel) level, getBlockPos(), forcedChunks);
        } else {
            chunkUnloadCooldown += CPLConfigs.server().chunkUpdateInterval.get();
            resetStates = false;
        }
        if (resetStates) {
            chunkUnloadCooldown = 0;
            lastBlockPos = getBlockPos().immutable();
            lastSpeedRequirement = isSpeedRequirementFulfilled();
            lastRange = getLoadingRange();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        boolean server = (!level.isClientSide || isVirtual()) && (level instanceof ServerLevel);
        if (server)
            ChunkLoadingUtils.unforceAllChunks((ServerLevel) level, getBlockPos(), forcedChunks);
    }

    @Override
    public void remove() {
        super.remove();
        boolean server = (!level.isClientSide || isVirtual()) && (level instanceof ServerLevel);
        if (server)
            ChunkLoadingUtils.unforceAllChunks((ServerLevel) level, getBlockPos(), forcedChunks);
    }

    protected void spawnParticles() {
        if (level == null)
            return;
        if (!isSpeedRequirementFulfilled())
            return;

        RandomSource r = level.getRandom();

        Vec3 c = VecHelper.getCenterOf(worldPosition);

        if (r.nextInt(4) != 0)
            return;

        double speed = .0625f;
        Vec3 normal = Vec3.atLowerCornerOf(getBlockState().getValue(BrassChunkLoaderBlock.FACING).getNormal());
        Vec3 v2 = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .5f)
                        .multiply(1, 1, 1)
                        .normalize()
                        .scale((.25f) + r.nextDouble() * .125f))
                .add(normal.scale(0.5f));

        Vec3 motion = normal.scale(speed);
        level.addParticle(ParticleTypes.PORTAL, v2.x, v2.y, v2.z, motion.x, motion.y, motion.z);
    }

    private static class LoadingRangeValueBox extends CenteredSideValueBoxTransform {
        public LoadingRangeValueBox() {
            super((blockState, direction) -> {
                Direction facing = blockState.getValue(BrassChunkLoaderBlock.FACING);
                return facing.getAxis() != direction.getAxis();
            });
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 8, 15.5);
        }

        @Override
        public Vec3 getLocalOffset(BlockState state) {
            Direction facing = state.getValue(BrassChunkLoaderBlock.FACING);
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

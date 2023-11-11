package com.hlysine.create_power_loader.content.andesitechunkloader;


import com.hlysine.create_power_loader.CPLIcons;
import com.hlysine.create_power_loader.CreatePowerLoader;
import com.hlysine.create_power_loader.config.CPLConfigs;
import com.hlysine.create_power_loader.content.brasschunkloader.ChunkLoadingUtils;
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
public class AndesiteChunkLoaderBlockEntity extends KineticBlockEntity {
    private static final int LOADING_RANGE = 1;

    protected int chunkUpdateCooldown;
    protected BlockPos lastBlockPos;
    protected boolean lastSpeedRequirement;

    protected Set<ChunkPos> forcedChunks = new HashSet<>();

    public AndesiteChunkLoaderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        boolean server = (!level.isClientSide || isVirtual()) && (level instanceof ServerLevel);

        if (!server) {
            spawnParticles();
        }

        if (server && chunkUpdateCooldown-- <= 0) {
            chunkUpdateCooldown = 10;
            if (needsUpdate()) {
                setChanged();
                lastBlockPos = getBlockPos().immutable();
                lastSpeedRequirement = isSpeedRequirementFulfilled();
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

        double requirement = minSpeed * 2 * CPLConfigs.server().andesiteSpeedMultiplier.get();
        return Math.abs(getSpeed()) >= requirement;
    }

    public int getLoadingRange() {
        return LOADING_RANGE;
    }

    private boolean needsUpdate() {
        if (lastBlockPos == null) return true;
        return !lastBlockPos.equals(getBlockPos()) || lastSpeedRequirement != isSpeedRequirementFulfilled();
    }

    private void updateForcedChunks() {
        if (isSpeedRequirementFulfilled()) {
            ChunkLoadingUtils.updateForcedChunks((ServerLevel) level, new ChunkPos(getBlockPos()), getBlockPos(), getLoadingRange(), forcedChunks);
        } else {
            ChunkLoadingUtils.unforceAllChunks((ServerLevel) level, getBlockPos(), forcedChunks);
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
        Vec3 normal = Vec3.atLowerCornerOf(getBlockState().getValue(AndesiteChunkLoaderBlock.FACING).getNormal());
        Vec3 v2 = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .5f)
                        .multiply(1, 1, 1)
                        .normalize()
                        .scale((.25f) + r.nextDouble() * .125f))
                .add(normal.scale(0.5f));

        Vec3 motion = normal.scale(speed);
        level.addParticle(ParticleTypes.PORTAL, v2.x, v2.y, v2.z, motion.x, motion.y, motion.z);
    }
}

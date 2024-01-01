package com.hlysine.create_power_loader.content.andesitechunkloader;


import com.hlysine.create_power_loader.config.CPLConfigs;
import com.hlysine.create_power_loader.content.ChunkLoadManager;
import com.hlysine.create_power_loader.content.IChunkLoaderBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

import static com.hlysine.create_power_loader.content.ChunkLoadManager.*;

@MethodsReturnNonnullByDefault
public class AndesiteChunkLoaderBlockEntity extends KineticBlockEntity implements IChunkLoaderBlockEntity {
    private static final int LOADING_RANGE = 1;

    protected int chunkUpdateCooldown;
    protected int chunkUnloadCooldown;
    protected BlockPos lastBlockPos;
    protected boolean lastSpeedRequirement;

    protected Set<LoadedChunkPos> forcedChunks = new HashSet<>();

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

        double requirement = minSpeed * 2 * CPLConfigs.server().andesiteSpeedMultiplier.get();
        return Math.abs(getSpeed()) >= requirement;
    }

    @Override
    public int getLoadingRange() {
        return LOADING_RANGE;
    }

    private boolean needsUpdate() {
        if (lastBlockPos == null) return true;
        return !lastBlockPos.equals(getBlockPos()) || lastSpeedRequirement != isSpeedRequirementFulfilled() || chunkUnloadCooldown > 0;
    }

    private void updateForcedChunks() {
        boolean resetStates = true;
        if (isSpeedRequirementFulfilled()) {
            ChunkLoadManager.updateForcedChunks((ServerLevel) level, new ChunkPos(getBlockPos()), getBlockPos(), getLoadingRange(), forcedChunks);
        } else if (chunkUnloadCooldown >= CPLConfigs.server().unloadGracePeriod.get()) {
            unforceAllChunks((ServerLevel) level, getBlockPos(), forcedChunks);
        } else {
            chunkUnloadCooldown += CPLConfigs.server().chunkUpdateInterval.get();
            resetStates = false;
        }
        if (resetStates) {
            chunkUnloadCooldown = 0;
            lastBlockPos = getBlockPos().immutable();
            lastSpeedRequirement = isSpeedRequirementFulfilled();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        boolean server = (!level.isClientSide || isVirtual()) && (level instanceof ServerLevel);
        if (server)
            unforceAllChunks((ServerLevel) level, getBlockPos(), forcedChunks);
    }

    @Override
    public void remove() {
        super.remove();
        boolean server = (!level.isClientSide || isVirtual()) && (level instanceof ServerLevel);
        if (server)
            unforceAllChunks((ServerLevel) level, getBlockPos(), forcedChunks);
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

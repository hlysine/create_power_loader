package com.hlysine.create_power_loader.content;

import com.hlysine.create_power_loader.config.CPLConfigs;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

import static com.hlysine.create_power_loader.content.ChunkLoadManager.LoadedChunkPos;
import static com.hlysine.create_power_loader.content.ChunkLoadManager.unforceAllChunks;

public abstract class AbstractChunkLoaderBlockEntity extends KineticBlockEntity {

    protected BlockPos lastBlockPos;
    protected boolean lastSpeedRequirement;
    protected int lastRange;
    protected int chunkUpdateCooldown;
    protected int chunkUnloadCooldown;
    protected Set<LoadedChunkPos> forcedChunks = new HashSet<>();

    public AbstractChunkLoaderBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
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

    private boolean needsUpdate() {
        if (lastBlockPos == null) return true;
        return !lastBlockPos.equals(getBlockPos()) || lastSpeedRequirement != isSpeedRequirementFulfilled() || lastRange != getLoadingRange() || chunkUnloadCooldown > 0;
    }

    protected void updateForcedChunks() {
        boolean resetStates = true;
        if (isSpeedRequirementFulfilled()) {
            ChunkLoadManager.updateForcedChunks(level.getServer(), new LoadedChunkPos(getLevel(), getBlockPos()), getBlockPos(), getLoadingRange(), forcedChunks);
        } else if (chunkUnloadCooldown >= CPLConfigs.server().unloadGracePeriod.get()) {
            unforceAllChunks(level.getServer(), getBlockPos(), forcedChunks);
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
    public boolean isSpeedRequirementFulfilled() {
        if (!super.isSpeedRequirementFulfilled())
            return false;

        BlockState state = getBlockState();
        if (!(getBlockState().getBlock() instanceof IRotate))
            return true;
        IRotate def = (IRotate) state.getBlock();
        IRotate.SpeedLevel minimumRequiredSpeedLevel = def.getMinimumRequiredSpeedLevel();
        float minSpeed = minimumRequiredSpeedLevel.getSpeedValue();

        double requirement = minSpeed * (float) Math.pow(2, getLoadingRange()) * getSpeedMultiplierConfig();
        return Math.abs(getSpeed()) >= requirement;
    }

    @Override
    public void destroy() {
        super.destroy();
        boolean server = (!level.isClientSide || isVirtual()) && (level instanceof ServerLevel);
        if (server)
            unforceAllChunks(level.getServer(), getBlockPos(), forcedChunks);
    }

    @Override
    public void remove() {
        super.remove();
        boolean server = (!level.isClientSide || isVirtual()) && (level instanceof ServerLevel);
        if (server)
            unforceAllChunks(level.getServer(), getBlockPos(), forcedChunks);
    }

    public abstract int getLoadingRange();

    protected abstract double getSpeedMultiplierConfig();

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
        Vec3 normal = Vec3.atLowerCornerOf(getBlockState().getValue(BlockStateProperties.FACING).getNormal());
        Vec3 v2 = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .5f)
                        .multiply(1, 1, 1)
                        .normalize()
                        .scale((.25f) + r.nextDouble() * .125f))
                .add(normal.scale(0.5f));

        Vec3 motion = normal.scale(speed);
        level.addParticle(ParticleTypes.PORTAL, v2.x, v2.y, v2.z, motion.x, motion.y, motion.z);
    }
}

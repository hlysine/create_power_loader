package com.hlysine.create_power_loader.content;

import com.hlysine.create_power_loader.config.CPLConfigs;
import com.hlysine.create_power_loader.content.trains.CPLGlobalStation;
import com.hlysine.create_power_loader.content.trains.StationChunkLoader;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static com.hlysine.create_power_loader.content.AbstractChunkLoaderBlock.ATTACHED;
import static com.hlysine.create_power_loader.content.ChunkLoadManager.LoadedChunkPos;
import static com.hlysine.create_power_loader.content.ChunkLoadManager.unforceAllChunks;
import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;

public abstract class AbstractChunkLoaderBlockEntity extends KineticBlockEntity {

    public final LoaderType type;
    protected BlockPos lastBlockPos;
    protected boolean lastEnabled;
    protected int lastRange;
    protected int chunkUpdateCooldown;
    protected int chunkUnloadCooldown;
    protected Set<LoadedChunkPos> forcedChunks = new HashSet<>();
    @Nullable
    private StationBlockEntity attachedStation = null;
    public boolean isLoaderActive = false;

    public AbstractChunkLoaderBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state, LoaderType type) {
        super(typeIn, pos, state);
        this.type = type;
    }

    public void updateAttachedStation(StationBlockEntity be) {
        if (attachedStation != null) {
            if (attachedStation.getStation() instanceof CPLGlobalStation station) {
                station.getLoader().removeAttachment(getBlockPos());
            }
        }
        attachedStation = be;
        if (attachedStation != null) {
            if (attachedStation.getStation() instanceof CPLGlobalStation station) {
                station.getLoader().addAttachment(type, getBlockPos());
            }
        }
    }

    public StationBlockEntity getAttachedStation() {
        return attachedStation;
    }

    @Override
    public void initialize() {
        super.initialize();
        if (getLevel() != null && getBlockState().getValue(ATTACHED)) {
            BlockEntity be = getLevel().getBlockEntity(getBlockPos().relative(getBlockState().getValue(FACING).getOpposite()));
            if (!(be instanceof StationBlockEntity sbe)) return;
            updateAttachedStation(sbe);
        }
    }

    public void reclaimChunks(Set<LoadedChunkPos> forcedChunks) {
        this.forcedChunks.addAll(forcedChunks);
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

        if (server) {
            boolean wasLoaderActive = isLoaderActive;
            isLoaderActive = StationChunkLoader.isEnabledForStation(type) &&
                    attachedStation != null &&
                    attachedStation.getStation() != null &&
                    attachedStation.getStation().getPresentTrain() != null;
            if (wasLoaderActive != isLoaderActive) {
                notifyUpdate();
            }
        }
    }

    private boolean needsUpdate() {
        if (lastBlockPos == null) return true;
        return !lastBlockPos.equals(getBlockPos()) || lastEnabled != isSpeedRequirementFulfilled() || lastRange != getLoadingRange() || chunkUnloadCooldown > 0;
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
            lastEnabled = isSpeedRequirementFulfilled();
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
        updateAttachedStation(null);
    }

    @Override
    public void remove() {
        super.remove();
        boolean server = (!level.isClientSide || isVirtual()) && (level instanceof ServerLevel);
        if (server)
            unforceAllChunks(level.getServer(), getBlockPos(), forcedChunks);
        updateAttachedStation(null);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        isLoaderActive = compound.getBoolean("CoreActive");
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.putBoolean("CoreActive", isLoaderActive);
        super.write(compound, clientPacket);
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

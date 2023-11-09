package com.hlysine.create_power_loader.content.chunkloader;


import com.hlysine.create_power_loader.CreatePowerLoader;
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
import net.minecraftforge.common.world.ForgeChunkManager;

@MethodsReturnNonnullByDefault
public class MechanicalChunkLoaderBlockEntity extends KineticBlockEntity {

    protected int chunkUpdateCooldown;

    public MechanicalChunkLoaderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        boolean server = !level.isClientSide || isVirtual();

        if (!server) {
            spawnParticles();
        }

        if (server && chunkUpdateCooldown-- <= 0) {
            chunkUpdateCooldown = 10;
            ChunkPos chunkPos = new ChunkPos(getBlockPos());
            if (isSpeedRequirementFulfilled()) {
                ForgeChunkManager.forceChunk((ServerLevel) level, CreatePowerLoader.MODID, getBlockPos(), chunkPos.x, chunkPos.z, true, true);
            } else {
                ForgeChunkManager.forceChunk((ServerLevel) level, CreatePowerLoader.MODID, getBlockPos(), chunkPos.x, chunkPos.z, false, true);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        ChunkPos chunkPos = new ChunkPos(getBlockPos());
        ForgeChunkManager.forceChunk((ServerLevel) level, CreatePowerLoader.MODID, getBlockPos(), chunkPos.x, chunkPos.z, false, true);
    }

    protected void spawnParticles() {
        if (level == null)
            return;
        if (!isSpeedRequirementFulfilled())
            return;

        RandomSource r = level.getRandom();

        Vec3 c = VecHelper.getCenterOf(worldPosition);
        Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f)
                .multiply(1, 0, 1));

        if (r.nextInt(4) != 0)
            return;

        double yMotion = .0625f;
        Vec3 v2 = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .5f)
                        .multiply(1, .25f, 1)
                        .normalize()
                        .scale((.25f) + r.nextDouble() * .125f))
                .add(0, .5, 0);

        level.addParticle(ParticleTypes.PORTAL, v2.x, v2.y, v2.z, 0, yMotion, 0);
    }

}

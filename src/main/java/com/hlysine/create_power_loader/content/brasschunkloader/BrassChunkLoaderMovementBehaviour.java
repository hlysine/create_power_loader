package com.hlysine.create_power_loader.content.brasschunkloader;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.mojang.logging.LogUtils;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class BrassChunkLoaderMovementBehaviour implements MovementBehaviour {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        if (context.world.isClientSide)
            return;
        if (context.contraption.entity == null)
            return;

        ChunkPos entityChunkPosition = context.contraption.entity.chunkPosition();
        Object tempState = context.temporaryData;

        if (!(tempState instanceof SavedState)) {
            tempState = new SavedState(entityChunkPosition, new HashSet<>());
            context.temporaryData = tempState;
        }

        SavedState savedState = (SavedState) tempState;

        if (entityChunkPosition.equals(savedState.chunkPos))
            return;

        ChunkLoadingUtils.updateForcedChunks((ServerLevel) context.world, entityChunkPosition, context.contraption.entity.getUUID(), 2, savedState.forcedChunks);
        savedState.chunkPos = entityChunkPosition;
        LOGGER.debug("CPL: Entity {} at new chunk {}, loaded {} chunks", context.contraption.entity, entityChunkPosition, savedState.forcedChunks.size());

        context.temporaryData = savedState;
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world.isClientSide)
            return;
        if (context.contraption.entity == null)
            return;

        ChunkPos entityChunkPosition = context.contraption.entity.chunkPosition();
        Object tempState = context.temporaryData;

        if (!(tempState instanceof SavedState)) {
            tempState = new SavedState(entityChunkPosition, new HashSet<>());
            context.temporaryData = tempState;
        }

        SavedState savedState = (SavedState) tempState;

        if (savedState.forcedChunks.size() == 0) {
            Set<ChunkPos> savedForcedChunks = ChunkLoadingUtils.getSavedForcedChunks(context.contraption.entity.getUUID());
            if (savedForcedChunks != null) {
                savedState.forcedChunks.addAll(savedForcedChunks);
                LOGGER.debug("CPL: Entity {} reclaimed {} chunks", context.contraption.entity, savedForcedChunks.size());
            }
            ChunkLoadingUtils.updateForcedChunks((ServerLevel) context.world, entityChunkPosition, context.contraption.entity.getUUID(), 2, savedState.forcedChunks);
            savedState.chunkPos = entityChunkPosition;
            LOGGER.debug("CPL: Entity {} starts moving at chunk {}, loaded {} chunks", context.contraption.entity, entityChunkPosition, savedState.forcedChunks.size());
        }
    }

    @Override
    public void stopMoving(MovementContext context) {
        if (context.world.isClientSide)
            return;
        if (context.contraption.entity == null)
            return;

        Object tempState = context.temporaryData;

        if (!(tempState instanceof SavedState))
            return;

        SavedState savedState = (SavedState) tempState;

        LOGGER.debug("CPL: Entity {} stops moving, unloaded {} chunks", context.contraption.entity, savedState.forcedChunks.size());
        ChunkLoadingUtils.unforceAllChunks((ServerLevel) context.world, context.contraption.entity.getUUID(), savedState.forcedChunks);

        context.temporaryData = null;
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        BrassChunkLoaderRenderer.renderInContraption(context, renderWorld, matrices, buffer);
    }

    static class SavedState {
        public ChunkPos chunkPos;
        public Set<ChunkPos> forcedChunks;

        public SavedState(ChunkPos chunkPos, Set<ChunkPos> forcedChunks) {
            this.chunkPos = chunkPos;
            this.forcedChunks = forcedChunks;
        }
    }
}

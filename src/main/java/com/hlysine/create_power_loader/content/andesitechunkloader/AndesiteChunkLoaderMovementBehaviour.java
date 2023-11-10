package com.hlysine.create_power_loader.content.andesitechunkloader;

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

public class AndesiteChunkLoaderMovementBehaviour implements MovementBehaviour {

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        AndesiteChunkLoaderRenderer.renderInContraption(context, renderWorld, matrices, buffer);
    }
}

package com.hlysine.create_power_loader.content;

import com.hlysine.create_power_loader.content.andesitechunkloader.AndesiteChunkLoaderRenderer;
import com.hlysine.create_power_loader.content.brasschunkloader.BrassChunkLoaderRenderer;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import net.minecraft.client.renderer.MultiBufferSource;

public class ContraptionRenderer {

    private static final AndesiteChunkLoaderRenderer ANDESITE_RENDERER = new AndesiteChunkLoaderRenderer(null);
    private static final BrassChunkLoaderRenderer BRASS_RENDERER = new BrassChunkLoaderRenderer(null);

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer, LoaderType type) {
        if (type == LoaderType.ANDESITE) {
            ANDESITE_RENDERER.renderInContraption(context, renderWorld, matrices, buffer);
        } else if (type == LoaderType.BRASS) {
            BRASS_RENDERER.renderInContraption(context, renderWorld, matrices, buffer);
        } else {
            throw new RuntimeException("Unknown block.");
        }
    }
}

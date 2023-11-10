package com.hlysine.create_power_loader.content.chunkloader;


import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.hlysine.create_power_loader.CPLPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class BrassChunkLoaderRenderer extends KineticBlockEntityRenderer<BrassChunkLoaderBlockEntity> {

    public BrassChunkLoaderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(BrassChunkLoaderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {

        Direction direction = be.getBlockState()
                .getValue(FACING);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

        int lightBehind = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(direction.getOpposite()));
        int lightInFront = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(direction));

        SuperByteBuffer shaftHalf =
                CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, be.getBlockState(), direction.getOpposite());
        SuperByteBuffer core =
                CachedBufferer.partialFacing(
                        be.isSpeedRequirementFulfilled() ? CPLPartialModels.CHUNK_LOADER_CORE_ACTIVE : CPLPartialModels.CHUNK_LOADER_CORE_INACTIVE,
                        be.getBlockState(),
                        direction
                );

        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        float speed = be.getSpeed() / 16f;
        if (!be.isSpeedRequirementFulfilled())
            speed = Mth.clamp(speed, -0.5f, 0.5f);
        if (speed > 0)
            speed = Mth.clamp(speed, 0.1f, 8);
        if (speed < 0)
            speed = Mth.clamp(speed, -8, 0.1f);
        float angle = (time * speed * 3 / 10f) % 360;
        angle = angle / 180f * (float) Math.PI;

        standardKineticRotationTransform(shaftHalf, be, lightBehind).renderInto(ms, vb);
        kineticRotationTransform(core, be, direction.getAxis(), angle, lightInFront).renderInto(ms, vb);
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                           ContraptionMatrices matrices, MultiBufferSource buffer) {
        BlockState state = context.state;
        Direction direction = state.getValue(BrassChunkLoaderBlock.FACING);
        int light = ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld);

        SuperByteBuffer core =
                CachedBufferer.partialFacing(
                        CPLPartialModels.CHUNK_LOADER_CORE_ACTIVE,
                        state,
                        direction
                );

        float speed = context.getAnimationSpeed();
        float time = AnimationTickHolder.getRenderTime() / 40f;
        float angle = ((time * speed) % 360);

        core
                .transform(matrices.getModel())
                .centre()
                .rotateY(AngleHelper.horizontalAngle(direction))
                .rotateX(AngleHelper.verticalAngle(direction))
                .rotateZ(angle)
                .unCentre()
                .light(matrices.getWorld(), light)
                .renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.solid()));
    }
}
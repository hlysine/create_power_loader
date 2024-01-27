package com.hlysine.create_power_loader.content;

import com.hlysine.create_power_loader.CPLPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.ATTACHED;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public abstract class AbstractChunkLoaderRenderer extends KineticBlockEntityRenderer<AbstractChunkLoaderBlockEntity> {

    public AbstractChunkLoaderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected abstract PartialModel getCorePartial(boolean attached, boolean active);

    protected abstract boolean shouldFunctionOnContraption(MovementContext context);

    @Override
    protected void renderSafe(AbstractChunkLoaderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {

        Direction direction = be.getBlockState()
                .getValue(FACING);
        boolean attached = be.getBlockState().getValue(ATTACHED);
        boolean active = attached ? be.isLoaderActive : be.canLoadChunks();
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

        SuperByteBuffer shaftHalf =
                CachedBufferer.partialFacing(
                        attached ? CPLPartialModels.STATION_ATTACHMENT : AllPartialModels.SHAFT_HALF,
                        be.getBlockState(),
                        direction.getOpposite()
                );
        SuperByteBuffer core =
                CachedBufferer.partialFacing(
                        getCorePartial(attached, active),
                        be.getBlockState(),
                        direction
                );

        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        float speed = be.getSpeed() / 16f;
        if (!active)
            speed = Mth.clamp(speed, -0.5f, 0.5f);
        if (speed > 0)
            speed = Mth.clamp(speed, 0.1f, 8);
        if (speed < 0)
            speed = Mth.clamp(speed, -8, 0.1f);
        float angle = (time * speed * 3 / 10f) % 360;
        angle = angle / 180f * (float) Math.PI;

        standardKineticRotationTransform(shaftHalf, be, light).renderInto(ms, vb);
        kineticRotationTransform(core, be, direction.getAxis(), angle, light).renderInto(ms, vb);
    }

    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                    ContraptionMatrices matrices, MultiBufferSource buffer) {
        BlockState state = context.state;
        Direction direction = state.getValue(AbstractChunkLoaderBlock.FACING);
        int light = ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld);

        boolean shouldFunction = shouldFunctionOnContraption(context);

        SuperByteBuffer core =
                CachedBufferer.partialFacing(
                        getCorePartial(false, shouldFunction),
                        state,
                        direction
                );

        float speed = context.getAnimationSpeed();
        float time = AnimationTickHolder.getRenderTime() / 40f;
        float angle = ((time * speed) % 360);

        core
                .transform(matrices.getModel())
                .centre()
                .rotateZ(shouldFunction ? angle : 0)
                .unCentre()
                .light(matrices.getWorld(), light)
                .renderInto(matrices.getViewProjection(), buffer.getBuffer(RenderType.cutoutMipped()));
    }
}

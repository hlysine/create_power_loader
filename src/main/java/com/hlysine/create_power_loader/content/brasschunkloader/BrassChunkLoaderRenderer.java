package com.hlysine.create_power_loader.content.brasschunkloader;


import com.hlysine.create_power_loader.CPLBlocks;
import com.hlysine.create_power_loader.CPLPartialModels;
import com.hlysine.create_power_loader.config.CPLConfigs;
import com.hlysine.create_power_loader.content.AbstractChunkLoaderRenderer;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class BrassChunkLoaderRenderer extends AbstractChunkLoaderRenderer {

    public BrassChunkLoaderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected PartialModel getCorePartial(boolean attached, boolean active) {
        if (attached) {
            return active
                    ? CPLPartialModels.BRASS_CORE_ATTACHED_ACTIVE
                    : CPLPartialModels.BRASS_CORE_ATTACHED_INACTIVE;
        } else {
            return active
                    ? CPLPartialModels.BRASS_CORE_ACTIVE
                    : CPLPartialModels.BRASS_CORE_INACTIVE;
        }
    }

    @Override
    protected boolean shouldFunctionOnContraption(MovementContext context) {
        if (context.contraption instanceof CarriageContraption) {
            if (!CPLConfigs.server().brass.enableTrain.get()) return false;
        } else {
            if (!CPLConfigs.server().brass.enableContraption.get()) return false;
        }
        return !context.contraption.isActorTypeDisabled(CPLBlocks.BRASS_CHUNK_LOADER.asStack()) &&
                !context.contraption.isActorTypeDisabled(ItemStack.EMPTY);
    }
}

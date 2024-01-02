package com.hlysine.create_power_loader.mixin;

import com.hlysine.create_power_loader.content.trains.CPLTrain;
import com.hlysine.create_power_loader.content.trains.TrainChunkLoader;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(value = Train.class, remap = false)
public class TrainMixin implements CPLTrain {
    @Unique
    public TrainChunkLoader cpl$chunkLoader;

    @Override
    @Unique
    public TrainChunkLoader getLoader() {
        return cpl$chunkLoader;
    }

    @Override
    @Unique
    public void setLoader(TrainChunkLoader loader) {
        cpl$chunkLoader = loader;
    }

    @Inject(
            at = @At("RETURN"),
            method = "write(Lcom/simibubi/create/content/trains/graph/DimensionPalette;)Lnet/minecraft/nbt/CompoundTag;",
            cancellable = true
    )
    private void cpl$write(DimensionPalette dimensions, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag nbt = cir.getReturnValue();
        nbt.put("CPLData", cpl$chunkLoader.write());
        cir.setReturnValue(nbt);
    }

    @Inject(
            at = @At("RETURN"),
            method = "read(Lnet/minecraft/nbt/CompoundTag;Ljava/util/Map;Lcom/simibubi/create/content/trains/graph/DimensionPalette;)Lcom/simibubi/create/content/trains/entity/Train;",
            cancellable = true
    )
    private static void cpl$read(CompoundTag tag, Map<UUID, TrackGraph> trackNetworks, DimensionPalette dimensions, CallbackInfoReturnable<Train> cir) {
        Train train = cir.getReturnValue();
        ((CPLTrain) train).setLoader(TrainChunkLoader.read(train, tag.getCompound("CPLData")));
        cir.setReturnValue(train);
    }

    @Inject(
            at = @At("RETURN"),
            method = "tick(Lnet/minecraft/world/level/Level;)V"
    )
    private void cpl$tick(Level level, CallbackInfo ci) {
        if (cpl$chunkLoader == null)
            cpl$chunkLoader = new TrainChunkLoader((Train) (Object) this);
        cpl$chunkLoader.tick(level);
    }
}
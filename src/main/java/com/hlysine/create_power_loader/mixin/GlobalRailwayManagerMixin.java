package com.hlysine.create_power_loader.mixin;

import com.hlysine.create_power_loader.content.trains.CPLTrain;
import com.hlysine.create_power_loader.content.trains.TrainChunkLoader;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.entity.Train;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = GlobalRailwayManager.class, remap = false)
public class GlobalRailwayManagerMixin {
    @Inject(
            at = @At("HEAD"),
            method = "removeTrain(Ljava/util/UUID;)V"
    )
    private void cpl$removeTrain(UUID id, CallbackInfo ci) {
        Train train = Create.RAILWAYS.trains.get(id);
        if (train == null) return;
        TrainChunkLoader loader = ((CPLTrain) train).getLoader();
        if (loader != null)
            loader.onRemove();
    }
}

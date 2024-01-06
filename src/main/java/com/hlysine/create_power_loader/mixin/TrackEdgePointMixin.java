package com.hlysine.create_power_loader.mixin;

import com.hlysine.create_power_loader.content.trains.CPLGlobalStation;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrackEdgePoint.class, remap = false)
public class TrackEdgePointMixin {
    @Inject(
            at = @At("HEAD"),
            method = "tick(Lcom/simibubi/create/content/trains/graph/TrackGraph;Z)V"
    )
    public void cpl$tick(TrackGraph graph, boolean preTrains, CallbackInfo ci) {
        if (this instanceof CPLGlobalStation station) {
            station.getLoader().tick(graph, preTrains);
        }
    }

    @Inject(
            at = @At("HEAD"),
            method = "removeFromAllGraphs()V"
    )
    public void cpl$remove(CallbackInfo ci) {
        if (this instanceof CPLGlobalStation station) {
            station.getLoader().onRemove();
        }
    }
}

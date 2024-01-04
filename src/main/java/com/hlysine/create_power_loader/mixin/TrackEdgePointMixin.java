package com.hlysine.create_power_loader.mixin;

import com.hlysine.create_power_loader.content.trains.CPLGlobalStation;
import com.hlysine.create_power_loader.content.trains.StationChunkLoader;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.signal.TrackEdgePoint;
import com.simibubi.create.content.trains.station.GlobalStation;
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
            if (station.getLoader() == null)
                //noinspection DataFlowIssue
                station.setLoader(new StationChunkLoader((GlobalStation) station));
            station.getLoader().tick(graph, preTrains);
        }
    }
}

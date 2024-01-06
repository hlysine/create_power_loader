package com.hlysine.create_power_loader.content.trains;

import org.jetbrains.annotations.NotNull;

public interface CPLGlobalStation {
    @NotNull
    StationChunkLoader getLoader();

    void setLoader(StationChunkLoader loader);
}

package com.hlysine.create_power_loader.content;

public interface IChunkLoaderBlockEntity {
    boolean isSpeedRequirementFulfilled();

    int getLoadingRange();
}

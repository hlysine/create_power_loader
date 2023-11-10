package com.hlysine.create_power_loader;

import com.jozufozu.flywheel.core.PartialModel;

public class CPLPartialModels {
    public static final PartialModel ANDESITE_CHUNK_LOADER_CORE_ACTIVE = block("andesite_chunk_loader/core_active");

    public static final PartialModel ANDESITE_CHUNK_LOADER_CORE_INACTIVE = block("andesite_chunk_loader/core_inactive");

    public static final PartialModel BRASS_CHUNK_LOADER_CORE_ACTIVE = block("brass_chunk_loader/core_active");

    public static final PartialModel BRASS_CHUNK_LOADER_CORE_INACTIVE = block("brass_chunk_loader/core_inactive");

    private static PartialModel block(String path) {
        return new PartialModel(CreatePowerLoader.asResource("block/" + path));
    }

    public static void register() {

    }
}

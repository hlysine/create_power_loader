package com.hlysine.create_power_loader;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class CPLPartialModels {
    public static final PartialModel ANDESITE_CORE_ACTIVE = block("andesite_chunk_loader/core_active");

    public static final PartialModel ANDESITE_CORE_INACTIVE = block("andesite_chunk_loader/core_inactive");
    public static final PartialModel ANDESITE_CORE_ATTACHED_ACTIVE = block("andesite_chunk_loader/core_attached_active");

    public static final PartialModel ANDESITE_CORE_ATTACHED_INACTIVE = block("andesite_chunk_loader/core_attached_inactive");

    public static final PartialModel BRASS_CORE_ACTIVE = block("brass_chunk_loader/core_active");

    public static final PartialModel BRASS_CORE_INACTIVE = block("brass_chunk_loader/core_inactive");

    public static final PartialModel BRASS_CORE_ATTACHED_ACTIVE = block("brass_chunk_loader/core_attached_active");

    public static final PartialModel BRASS_CORE_ATTACHED_INACTIVE = block("brass_chunk_loader/core_attached_inactive");
    public static final PartialModel STATION_ATTACHMENT = block("station_attachment");

    private static PartialModel block(String path) {
        return PartialModel.of(CreatePowerLoader.asResource("block/" + path));
    }

    public static void register() {

    }
}

package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.content.chunkloader.MechanicalChunkLoaderBlockEntity;
import com.hlysine.create_power_loader.content.chunkloader.MechanicalChunkLoaderRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CPLBlockEntityTypes {
    private static final CreateRegistrate REGISTRATE = CreatePowerLoader.getRegistrate();

    public static final BlockEntityEntry<MechanicalChunkLoaderBlockEntity> MECHANICAL_CHUNK_LOADER = REGISTRATE
            .blockEntity("mechanical_chunk_loader", MechanicalChunkLoaderBlockEntity::new)
            .validBlocks(CPLBlocks.MECHANICAL_CHUNK_LOADER)
            .renderer(() -> MechanicalChunkLoaderRenderer::new)
            .register();

    public static void register() {
    }
}

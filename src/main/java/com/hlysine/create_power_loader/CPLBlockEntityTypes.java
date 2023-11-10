package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.content.chunkloader.BrassChunkLoaderBlockEntity;
import com.hlysine.create_power_loader.content.chunkloader.BrassChunkLoaderRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CPLBlockEntityTypes {
    private static final CreateRegistrate REGISTRATE = CreatePowerLoader.getRegistrate();

    public static final BlockEntityEntry<BrassChunkLoaderBlockEntity> BRASS_CHUNK_LOADER = REGISTRATE
            .blockEntity("brass_chunk_loader", BrassChunkLoaderBlockEntity::new)
            .validBlocks(CPLBlocks.BRASS_CHUNK_LOADER)
            .renderer(() -> BrassChunkLoaderRenderer::new)
            .register();

    public static void register() {
    }
}

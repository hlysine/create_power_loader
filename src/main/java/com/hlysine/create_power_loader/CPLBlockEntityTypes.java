package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.content.andesitechunkloader.AndesiteChunkLoaderBlockEntity;
import com.hlysine.create_power_loader.content.andesitechunkloader.AndesiteChunkLoaderRenderer;
import com.hlysine.create_power_loader.content.brasschunkloader.BrassChunkLoaderBlockEntity;
import com.hlysine.create_power_loader.content.brasschunkloader.BrassChunkLoaderRenderer;
import com.hlysine.create_power_loader.content.emptyandesitechunkloader.EmptyAndesiteChunkLoaderBlockEntity;
import com.hlysine.create_power_loader.content.emptyandesitechunkloader.EmptyAndesiteChunkLoaderRenderer;
import com.hlysine.create_power_loader.content.emptybrasschunkloader.EmptyBrassChunkLoaderBlockEntity;
import com.hlysine.create_power_loader.content.emptybrasschunkloader.EmptyBrassChunkLoaderRenderer;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CPLBlockEntityTypes {
    private static final CreateRegistrate REGISTRATE = CreatePowerLoader.getRegistrate();

    public static final BlockEntityEntry<EmptyAndesiteChunkLoaderBlockEntity> EMPTY_ANDESITE_CHUNK_LOADER = REGISTRATE
            .blockEntity("empty_andesite_chunk_loader", EmptyAndesiteChunkLoaderBlockEntity::new)
            .validBlocks(CPLBlocks.EMPTY_ANDESITE_CHUNK_LOADER)
            .renderer(() -> EmptyAndesiteChunkLoaderRenderer::new)
            .register();

    public static final BlockEntityEntry<AndesiteChunkLoaderBlockEntity> ANDESITE_CHUNK_LOADER = REGISTRATE
            .blockEntity("andesite_chunk_loader", AndesiteChunkLoaderBlockEntity::new)
            .validBlocks(CPLBlocks.ANDESITE_CHUNK_LOADER)
            .renderer(() -> AndesiteChunkLoaderRenderer::new)
            .register();

    public static final BlockEntityEntry<EmptyBrassChunkLoaderBlockEntity> EMPTY_BRASS_CHUNK_LOADER = REGISTRATE
            .blockEntity("empty_brass_chunk_loader", EmptyBrassChunkLoaderBlockEntity::new)
            .validBlocks(CPLBlocks.EMPTY_BRASS_CHUNK_LOADER)
            .renderer(() -> EmptyBrassChunkLoaderRenderer::new)
            .register();

    public static final BlockEntityEntry<BrassChunkLoaderBlockEntity> BRASS_CHUNK_LOADER = REGISTRATE
            .blockEntity("brass_chunk_loader", BrassChunkLoaderBlockEntity::new)
            .validBlocks(CPLBlocks.BRASS_CHUNK_LOADER)
            .renderer(() -> BrassChunkLoaderRenderer::new)
            .register();

    public static void register() {
    }
}

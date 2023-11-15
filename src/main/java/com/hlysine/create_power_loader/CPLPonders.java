package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.ponder.AndesiteChunkLoaderScenes;
import com.hlysine.create_power_loader.ponder.BrassChunkLoaderScenes;
import com.hlysine.create_power_loader.ponder.EmptyChunkLoaderScenes;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

public class CPLPonders {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreatePowerLoader.MODID);

    public static void register() {

        HELPER.forComponents(CPLBlocks.ANDESITE_CHUNK_LOADER)
                .addStoryBoard("andesite_chunk_loader/basic_usage", AndesiteChunkLoaderScenes::basicUsage, AllPonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("andesite_chunk_loader/redstone", AndesiteChunkLoaderScenes::redstone, AllPonderTags.KINETIC_APPLIANCES);
        HELPER.forComponents(CPLBlocks.BRASS_CHUNK_LOADER)
                .addStoryBoard("brass_chunk_loader/basic_usage", BrassChunkLoaderScenes::basicUsage, AllPonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("brass_chunk_loader/redstone", BrassChunkLoaderScenes::redstone, AllPonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("brass_chunk_loader/loading_contraptions", BrassChunkLoaderScenes::loadingContraptions, AllPonderTags.KINETIC_APPLIANCES);
        HELPER.forComponents(CPLBlocks.EMPTY_ANDESITE_CHUNK_LOADER, CPLBlocks.EMPTY_BRASS_CHUNK_LOADER)
                .addStoryBoard("empty_chunk_loader/usage", EmptyChunkLoaderScenes::usage);

        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_APPLIANCES)
                .add(CPLBlocks.ANDESITE_CHUNK_LOADER)
                .add(CPLBlocks.BRASS_CHUNK_LOADER);
    }
}

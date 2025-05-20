package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.ponder.AndesiteChunkLoaderScenes;
import com.hlysine.create_power_loader.ponder.BrassChunkLoaderScenes;
import com.hlysine.create_power_loader.ponder.EmptyChunkLoaderScenes;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import com.tterrag.registrate.util.entry.RegistryEntry;

public class CPLPonders implements PonderPlugin {

    @Override
    public String getModId() {
        return CreatePowerLoader.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        register(helper);
    }

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
    }

    @Override
    public void onPonderLevelRestore(PonderLevel ponderLevel) {}

    @Override
    public void indexExclusions(IndexExclusionHelper helper) {
    }

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(CPLBlocks.ANDESITE_CHUNK_LOADER)
                .addStoryBoard("andesite_chunk_loader/basic_usage", AndesiteChunkLoaderScenes::basicUsage, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("andesite_chunk_loader/redstone", AndesiteChunkLoaderScenes::redstone, AllCreatePonderTags.KINETIC_APPLIANCES);
        HELPER.forComponents(CPLBlocks.BRASS_CHUNK_LOADER)
                .addStoryBoard("brass_chunk_loader/basic_usage", BrassChunkLoaderScenes::basicUsage, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("brass_chunk_loader/redstone", BrassChunkLoaderScenes::redstone, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("brass_chunk_loader/loading_contraptions", BrassChunkLoaderScenes::loadingContraptions, AllCreatePonderTags.KINETIC_APPLIANCES)
                .addStoryBoard("brass_chunk_loader/attach_station", BrassChunkLoaderScenes::attachStation, AllCreatePonderTags.KINETIC_APPLIANCES);
        HELPER.forComponents(CPLBlocks.EMPTY_ANDESITE_CHUNK_LOADER, CPLBlocks.EMPTY_BRASS_CHUNK_LOADER)
                .addStoryBoard("empty_chunk_loader/usage", EmptyChunkLoaderScenes::usage);

    }

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES)
                .add(CPLBlocks.ANDESITE_CHUNK_LOADER)
                .add(CPLBlocks.BRASS_CHUNK_LOADER);
    }
}

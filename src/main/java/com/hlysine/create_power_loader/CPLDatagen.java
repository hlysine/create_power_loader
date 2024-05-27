package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.CPLTags.AllEntityTags;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CPLDatagen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        ExistingFileHelper helper = ExistingFileHelper.withResourcesFromArg();
        CreatePowerLoader.getRegistrate().setupDatagen(generator, helper);
        gatherData(generator, helper);
    }

    public static void gatherData(FabricDataGenerator generator, ExistingFileHelper helper) {
        CreatePowerLoader.getRegistrate().addDataGenerator(ProviderType.ENTITY_TAGS, CPLDatagen::genEntityTags);
    }

    private static void genEntityTags(RegistrateTagsProvider<EntityType<?>> prov) {
        prov.tag(AllEntityTags.CHUNK_LOADER_CAPTURABLE.tag).add(EntityType.GHAST);
    }
}

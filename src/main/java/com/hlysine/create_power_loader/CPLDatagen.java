package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.CPLTags.AllEntityTags;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.world.entity.EntityType;

public class CPLDatagen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        ExistingFileHelper helper = ExistingFileHelper.withResourcesFromArg();
        FabricDataGenerator.Pack pack = generator.createPack();
        CreatePowerLoader.getRegistrate().setupDatagen(pack, helper);
        gatherData(pack, helper);
    }

    public static void gatherData(FabricDataGenerator.Pack pack, ExistingFileHelper helper) {
        CreatePowerLoader.getRegistrate().addDataGenerator(ProviderType.ENTITY_TAGS, CPLDatagen::genEntityTags);
    }

    private static void genEntityTags(RegistrateTagsProvider<EntityType<?>> prov) {
        prov.addTag(AllEntityTags.CHUNK_LOADER_CAPTURABLE.tag).add(EntityType.GHAST);
    }
}

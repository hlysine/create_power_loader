package com.hlysine.create_power_loader;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateDataProvider;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.entity.EntityType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.hlysine.create_power_loader.CPLTags.AllEntityTags;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class CPLDatagen {
    public static void gatherData(GatherDataEvent event) {
        CreatePowerLoader.getRegistrate().addDataGenerator(ProviderType.ENTITY_TAGS, CPLDatagen::genEntityTags);
        // todo: this line shouldn't be necessary because registrate should add the data provider itself
        event.getGenerator().addProvider(true, CreatePowerLoader.getRegistrate().setDataProvider(new RegistrateDataProvider(CreatePowerLoader.getRegistrate(), CreatePowerLoader.MODID, event)));
    }

    private static void genEntityTags(RegistrateTagsProvider<EntityType<?>> prov) {
        prov.addTag(AllEntityTags.CHUNK_LOADER_CAPTURABLE.tag).add(TagEntry.element(EntityType.getKey(EntityType.GHAST)));
    }
}

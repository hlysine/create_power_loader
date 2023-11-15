package com.hlysine.create_power_loader;

import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.data.event.GatherDataEvent;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.hlysine.create_power_loader.CPLTags.AllEntityTags;

public class CPLDatagen {
    public static void gatherData(GatherDataEvent event) {
        CreatePowerLoader.getRegistrate().addDataGenerator(ProviderType.ENTITY_TAGS, CPLDatagen::genEntityTags);
    }

    private static void genEntityTags(RegistrateTagsProvider<EntityType<?>> prov) {
        prov.addTag(AllEntityTags.CHUNK_LOADER_CAPTURABLE.tag).add(TagEntry.element(EntityType.getKey(EntityType.GHAST)));
    }
}

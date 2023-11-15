package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.CPLTags.AllEntityTags;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.data.event.GatherDataEvent;

public class CPLDatagen {
    public static void gatherData(GatherDataEvent event) {
        CreatePowerLoader.getRegistrate().addDataGenerator(ProviderType.ENTITY_TAGS, CPLDatagen::genEntityTags);
    }

    private static void genEntityTags(RegistrateTagsProvider<EntityType<?>> prov) {
        prov.tag(AllEntityTags.CHUNK_LOADER_CAPTURABLE.tag).add(EntityType.GHAST);
    }
}

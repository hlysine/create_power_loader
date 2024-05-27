package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.compat.Mods;
import com.hlysine.create_power_loader.config.CPLConfigs;
import com.hlysine.create_power_loader.content.ChunkLoadManager;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import io.github.fabricators_of_create.porting_lib.chunk.loading.PortingLibChunkManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class CreatePowerLoader implements ModInitializer {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_power_loader";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> {
            return new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        });
    }

    @Override
    public void onInitialize() {
        REGISTRATE.creativeModeTab(() -> CPLCreativeTabs.MAIN);
        CPLCreativeTabs.register();
        CPLTags.register();
        CPLBlocks.register();
        CPLBlockEntityTypes.register();
        REGISTRATE.register();

        CPLConfigs.register();

        Mods.JEI.executeIfInstalled(() -> CPLRecipes::register);
        PortingLibChunkManager.setForcedChunkLoadingCallback(MODID, ChunkLoadManager::validateAllForcedChunks);

        CommonEvents.register();
    }

    public static CreateRegistrate getRegistrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}

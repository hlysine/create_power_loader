package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.compat.Mods;
import com.hlysine.create_power_loader.config.CPLConfigs;
import com.hlysine.create_power_loader.content.ChunkLoadManager;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreatePowerLoader.MODID)
public class CreatePowerLoader {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_power_loader";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static IEventBus modEventBus;
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> {
            return new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        });
    }

    public CreatePowerLoader(IEventBus eventBus, ModContainer modContainer) {
        modEventBus = modContainer.getEventBus();
        IEventBus forgeEventBus = NeoForge.EVENT_BUS;
        REGISTRATE.registerEventListeners(modEventBus);

        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ChunkLoadManager::registerTicketControllers);
        forgeEventBus.addListener(this::registerCommands);

        REGISTRATE.setCreativeTab(CPLCreativeTabs.MAIN);
        CPLTags.register();
        CPLBlocks.register();
        CPLBlockEntityTypes.register();
        CPLCreativeTabs.register(modEventBus);

        CPLConfigs.register(modContainer);

        modEventBus.addListener(EventPriority.LOWEST, CPLDatagen::gatherData);
        forgeEventBus.addListener(ChunkLoadManager::onServerWorldTick);
        CreatePowerLoaderClient.onCtorClient(modEventBus, forgeEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Mods.JEI.executeIfInstalled(() -> CPLRecipes::register);
        });
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CPLCommands.register(event.getDispatcher());
    }

    public static CreateRegistrate getRegistrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}

package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.content.ChunkLoadManager;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.Level;

public class CommonEvents {
    public static void register() {

        CommandRegistrationCallback.EVENT.register(CommonEvents::registerCommands);
        ServerTickEvents.START_WORLD_TICK.register(CommonEvents::onLevelTick);
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
        CPLCommands.register(dispatcher);
    }

    public static void onLevelTick(Level level) {
        ChunkLoadManager.onServerWorldTick(level);
    }
}

package com.hlysine.create_power_loader.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CServer extends ConfigBase {

    public final ConfigInt chunkUpdateInterval = i(10, 0, 200, "chunkUpdateInterval", Comments.chunkUpdateInterval);

    public final ConfigInt unloadGracePeriod = i(20, 0, 20 * 60, "unloadGracePeriod", Comments.unloadGracePeriod);

    public final ConfigFloat andesiteSpeedMultiplier = f(1, 0, 128, "andesiteSpeedMultiplier", Comments.andesiteSpeedMultiplier);

    public final ConfigFloat brassSpeedMultiplier = f(1, 0, 128, "brassSpeedMultiplier", Comments.brassSpeedMultiplier);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String chunkUpdateInterval = "Number of ticks between chunk loading checks. Does not affect contraptions.";
        static String unloadGracePeriod = "Minimum number of ticks between loss of power and chunk unloading. Rounds up to multiples of update interval.";
        static String andesiteSpeedMultiplier = "A multiplier for the speed requirements for andesite chunk loaders";
        static String brassSpeedMultiplier = "A multiplier for the speed requirements for brass chunk loaders";
    }
}

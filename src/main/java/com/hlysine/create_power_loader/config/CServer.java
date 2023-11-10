package com.hlysine.create_power_loader.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CServer extends ConfigBase {

    public final ConfigGroup misc = group(0, "misc", Comments.misc);
    public final ConfigFloat brassSpeedMultiplier = f(1, 0, 128, "brassSpeedMultiplier", Comments.brassSpeedMultiplier);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String misc = "Miscellaneous";
        static String brassSpeedMultiplier = "A multiplier for the speed requirements for brass chunk loaders";
    }
}

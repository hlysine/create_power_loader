package com.hlysine.create_power_loader.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CServer extends ConfigBase {

    public final ConfigGroup andesite = group(0, "andesite", Comments.andesite);
    public final ConfigFloat andesiteSpeedMultiplier = f(1, 0, 128, "andesiteSpeedMultiplier", Comments.andesiteSpeedMultiplier);

    public final ConfigGroup brass = group(0, "brass", Comments.brass);
    public final ConfigFloat brassSpeedMultiplier = f(1, 0, 128, "brassSpeedMultiplier", Comments.brassSpeedMultiplier);

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String andesite = "Andesite Chunk Loader";
        static String andesiteSpeedMultiplier = "A multiplier for the speed requirements for andesite chunk loaders";
        static String brass = "Brass Chunk Loader";
        static String brassSpeedMultiplier = "A multiplier for the speed requirements for brass chunk loaders";
    }
}

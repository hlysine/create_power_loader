package com.hlysine.create_power_loader.config;

import com.hlysine.create_power_loader.content.AbstractChunkLoaderBlock;
import com.hlysine.create_power_loader.content.LoaderType;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.DoubleSupplier;

public class CServer extends ConfigBase {
    public final CLoader andesite = nested(0, () -> new CLoader(LoaderType.ANDESITE), Comments.andesite);

    public final CLoader brass = nested(0, () -> new CLoader(LoaderType.BRASS), Comments.brass);

    public CLoader getFor(LoaderType type) {
        return switch (type) {
            case ANDESITE -> andesite;
            case BRASS -> brass;
        };
    }

    @Nullable
    public DoubleSupplier getImpact(Block block) {
        if (!(block instanceof AbstractChunkLoaderBlock loader)) return null;
        return getFor(loader.loaderType).stressImpact::get;
    }

    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String andesite = "Configure the Andesite Chunk Loader";
        static String brass = "Configure the Brass Chunk Loader";
    }
}

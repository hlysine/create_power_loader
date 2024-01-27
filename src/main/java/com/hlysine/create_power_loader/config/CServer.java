package com.hlysine.create_power_loader.config;

import com.hlysine.create_power_loader.content.AbstractChunkLoaderBlock;
import com.hlysine.create_power_loader.content.LoaderType;
import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class CServer extends ConfigBase implements BlockStressValues.IStressValueProvider {

    public final CLoader andesite = nested(0, () -> new CLoader(LoaderType.ANDESITE), Comments.andesite);

    public final CLoader brass = nested(0, () -> new CLoader(LoaderType.BRASS), Comments.brass);

    public CLoader getFor(LoaderType type) {
        return switch (type) {
            case ANDESITE -> andesite;
            case BRASS -> brass;
        };
    }

    @Override
    public double getImpact(Block block) {
        if (!(block instanceof AbstractChunkLoaderBlock loader)) return 0;
        return getFor(loader.loaderType).stressImpact.get();
    }

    @Override
    public double getCapacity(Block block) {
        return 0;
    }

    @Override
    public boolean hasImpact(Block block) {
        return block instanceof AbstractChunkLoaderBlock loader;
    }

    @Override
    public boolean hasCapacity(Block block) {
        return false;
    }

    @Nullable
    @Override
    public Couple<Integer> getGeneratedRPM(Block block) {
        return null;
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

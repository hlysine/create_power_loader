package com.hlysine.create_power_loader.content.trains;

import com.hlysine.create_power_loader.content.ChunkLoadManager;
import com.hlysine.create_power_loader.content.ChunkLoader;
import com.hlysine.create_power_loader.content.LoaderMode;
import com.hlysine.create_power_loader.content.LoaderType;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.hlysine.create_power_loader.content.ChunkLoadManager.LoadedChunkPos;
import static com.hlysine.create_power_loader.content.Helper.blockPosContaining;

public class TrainChunkLoader implements ChunkLoader {
    private final Train train;
    public final List<CarriageChunkLoader> carriageLoaders = new LinkedList<>();
    private final Map<ResourceKey<Level>, Set<LoadedChunkPos>> reclaimedChunks = new HashMap<>();
    private boolean registered = false;

    public TrainChunkLoader(Train train) {
        this.train = train;
    }

    @Override
    public @NotNull Set<LoadedChunkPos> getForcedChunks() {
        Set<LoadedChunkPos> allForced = new HashSet<>();
        for (CarriageChunkLoader loader : carriageLoaders) {
            allForced.addAll(loader.getForcedChunks());
        }
        return allForced;
    }

    @Override
    public LoaderMode getLoaderMode() {
        return LoaderMode.TRAIN;
    }

    @Override
    public LoaderType getLoaderType() {
        for (CarriageChunkLoader carriageLoader : carriageLoaders) {
            if (carriageLoader.getLoaderType() == LoaderType.BRASS) return LoaderType.BRASS;
        }
        return LoaderType.ANDESITE;
    }

    @Override
    public Pair<ResourceLocation, BlockPos> getLocation() {
        if (train.graph == null) return null;
        return train.carriages.stream().findFirst()
                .map(carriage -> Pair.of(
                        carriage.leadingBogey().trailing().node1.getLocation().getDimension().location(),
                        blockPosContaining(carriage.leadingBogey().trailing().getPosition(train.graph))
                ))
                .orElse(null);
    }

    @Override
    public void addToManager() {
        if (!registered) {
            ChunkLoader.super.addToManager();
            registered = true;
        }
    }

    public void tick(Level level) {
        if (level.isClientSide()) return;
        addToManager();

        // Make sure carriage information is up-to-date
        if (carriageLoaders.size() != train.carriages.size()) {
            List<CarriageChunkLoader> newLoaders = new LinkedList<>();
            for (Carriage carriage : train.carriages) {
                CarriageChunkLoader loader = carriageLoaders.stream()
                        .filter(x -> x.carriage == carriage)
                        .findFirst()
                        .orElseGet(() -> new CarriageChunkLoader(carriage, false, false, false));
                newLoaders.add(loader);
            }
            carriageLoaders.clear();
            carriageLoaders.addAll(newLoaders);
        }

        ChunkLoadManager.reclaimChunks(level, train.id, reclaimedChunks);

        for (CarriageChunkLoader loader : carriageLoaders) {
            loader.tick(level);
        }
    }

    public void onRemove() {
        for (CarriageChunkLoader loader : carriageLoaders) {
            loader.onRemove();
        }
        removeFromManager();
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("CarriageLoaders", NBTHelper.writeCompoundList(carriageLoaders, CarriageChunkLoader::write));
        return nbt;
    }

    public static TrainChunkLoader read(Train train, CompoundTag nbt) {
        TrainChunkLoader loader = new TrainChunkLoader(train);
        ListTag list = nbt.getList("CarriageLoaders", Tag.TAG_COMPOUND);
        // do not use saved data if sizes don't match,
        // because we have no idea which saved tag corresponds to which carriage
        if (list.size() == train.carriages.size()) {
            for (int i = 0; i < list.size(); i++) {
                CompoundTag tag = (CompoundTag) list.get(i);
                loader.carriageLoaders.add(CarriageChunkLoader.read(train.carriages.get(i), tag));
            }
        }
        return loader;
    }
}

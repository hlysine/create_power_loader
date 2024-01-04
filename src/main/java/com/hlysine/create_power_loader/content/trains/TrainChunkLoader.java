package com.hlysine.create_power_loader.content.trains;

import com.hlysine.create_power_loader.content.ChunkLoadManager;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.*;

import static com.hlysine.create_power_loader.content.ChunkLoadManager.LoadedChunkPos;

public class TrainChunkLoader {
    private final Train train;
    private final List<CarriageChunkLoader> carriageLoaders = new LinkedList<>();
    private final Map<ResourceKey<Level>, Set<LoadedChunkPos>> reclaimedChunks = new HashMap<>();

    public TrainChunkLoader(Train train) {
        this.train = train;
    }

    public void tick(Level level) {
        if (level.isClientSide()) return;

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

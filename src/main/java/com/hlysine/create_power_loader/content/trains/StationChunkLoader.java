package com.hlysine.create_power_loader.content.trains;

import com.hlysine.create_power_loader.content.ChunkLoadManager;
import com.hlysine.create_power_loader.content.ChunkLoadManager.LoadedChunkPos;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.*;

public class StationChunkLoader {
    private final GlobalStation station;
    public final Set<BlockPos> attachments = new HashSet<>();

    private final Map<ResourceKey<Level>, Set<LoadedChunkPos>> reclaimedChunks = new HashMap<>();
    public final Set<LoadedChunkPos> forcedChunks = new HashSet<>();


    public StationChunkLoader(GlobalStation station) {
        this.station = station;
    }

    public void tick(TrackGraph graph, boolean preTrains) {
        if (preTrains) return;
        Level level = ChunkLoadManager.tickLevel;
        if (level == null || level.isClientSide()) return;

        ChunkLoadManager.reclaimChunks(level, station.id, reclaimedChunks);

        if (attachments.isEmpty() || station.getPresentTrain() == null) {
            if (!forcedChunks.isEmpty())
                ChunkLoadManager.unforceAllChunks(level.getServer(), station.id, forcedChunks);
            return;
        }

        Set<LoadedChunkPos> loadTargets = new HashSet<>();
        for (BlockPos attachment : attachments) {
            loadTargets.add(new LoadedChunkPos(station.blockEntityDimension.location(), new ChunkPos(attachment)));
        }
        ChunkLoadManager.updateForcedChunks(level.getServer(), loadTargets, station.id, 2, forcedChunks);
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("Attachments", NBTHelper.writeCompoundList(attachments, NbtUtils::writeBlockPos));
        return nbt;
    }

    public static StationChunkLoader read(GlobalStation station, CompoundTag nbt) {
        StationChunkLoader loader = new StationChunkLoader(station);
        loader.attachments.clear();
        loader.attachments.addAll(NBTHelper.readCompoundList(nbt.getList("Attachments", Tag.TAG_COMPOUND), NbtUtils::readBlockPos));
        return loader;
    }
}

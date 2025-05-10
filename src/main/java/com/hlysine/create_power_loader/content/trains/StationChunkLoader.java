package com.hlysine.create_power_loader.content.trains;

import com.hlysine.create_power_loader.config.CPLConfigs;
import com.hlysine.create_power_loader.content.ChunkLoadManager;
import com.hlysine.create_power_loader.content.ChunkLoadManager.LoadedChunkPos;
import com.hlysine.create_power_loader.content.ChunkLoader;
import com.hlysine.create_power_loader.content.LoaderMode;
import com.hlysine.create_power_loader.content.LoaderType;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.station.GlobalStation;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StationChunkLoader implements ChunkLoader {
    private final GlobalStation station;
    public final Set<AttachedLoader> attachments = new HashSet<>();

    private final Map<ResourceKey<Level>, Set<LoadedChunkPos>> reclaimedChunks = new HashMap<>();
    public final Set<LoadedChunkPos> forcedChunks = new HashSet<>();
    private boolean registered = false;


    public StationChunkLoader(GlobalStation station) {
        this.station = station;
    }

    @Override
    public @NotNull Set<LoadedChunkPos> getForcedChunks() {
        return forcedChunks;
    }

    @Override
    public LoaderMode getLoaderMode() {
        return LoaderMode.STATION;
    }

    @Override
    public LoaderType getLoaderType() {
        for (AttachedLoader attachment : attachments) {
            if (attachment.type() == LoaderType.BRASS) return LoaderType.BRASS;
        }
        return LoaderType.ANDESITE;
    }

    @Override
    public @Nullable Pair<ResourceLocation, BlockPos> getLocation() {
        return Pair.of(
                station.edgeLocation.getFirst().dimension.location(),
                BlockPos.containing(station.edgeLocation.getFirst().getLocation().add(station.edgeLocation.getSecond().getLocation()).scale(0.5))
        );
    }

    @Override
    public void addToManager() {
        if (!registered) {
            ChunkLoader.super.addToManager();
            registered = true;
        }
    }

    public void tick(TrackGraph graph, boolean preTrains) {
        if (preTrains) return;
        Level level = ChunkLoadManager.tickLevel;
        if (level == null || level.isClientSide()) return;
        addToManager();

        ChunkLoadManager.reclaimChunks(level, station.id, reclaimedChunks);

        if (attachments.isEmpty() || station.getPresentTrain() == null) {
            if (!forcedChunks.isEmpty())
                ChunkLoadManager.unforceAllChunks(level.getServer(), station.id, forcedChunks);
            return;
        }

        // sanitize in case of read/write errors
        attachments.removeIf(a -> a.pos.distManhattan(station.blockEntityPos) > 1);

        Set<LoadedChunkPos> loadTargets = new HashSet<>();
        for (AttachedLoader attachment : attachments) {
            if (isEnabledForStation(attachment.type()))
                loadTargets.add(new LoadedChunkPos(station.blockEntityDimension.location(), new ChunkPos(attachment.pos())));
        }
        ChunkLoadManager.updateForcedChunks(level.getServer(), loadTargets, station.id, CPLConfigs.server().getFor(getLoaderType()).rangeOnStation.get(), forcedChunks);
    }

    public static boolean isEnabledForStation(LoaderType type) {
        return CPLConfigs.server().getFor(type).enableStation.get();
    }

    public void removeAttachment(BlockPos pos) {
        attachments.removeIf(t -> t.pos.equals(pos));
    }

    public void addAttachment(LoaderType type, BlockPos pos) {
        removeAttachment(pos);
        attachments.add(new AttachedLoader(type, pos));
    }

    public void onRemove() {
        ChunkLoadManager.enqueueUnforceAll(station.id, forcedChunks);
        removeFromManager();
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("Attachments", NBTHelper.writeCompoundList(attachments, AttachedLoader::write));
        return nbt;
    }

    public static StationChunkLoader read(GlobalStation station, CompoundTag nbt) {
        StationChunkLoader loader = new StationChunkLoader(station);
        loader.attachments.clear();
        loader.attachments.addAll(NBTHelper.readCompoundList(nbt.getList("Attachments", Tag.TAG_COMPOUND), AttachedLoader::read));
        return loader;
    }

    public record AttachedLoader(LoaderType type, BlockPos pos) {
        public CompoundTag write() {
            CompoundTag nbt = new CompoundTag();
            NBTHelper.writeEnum(nbt, "Type", type);
            nbt.put("Pos", NbtUtils.writeBlockPos(pos));
            return nbt;
        }

        public static AttachedLoader read(CompoundTag nbt) {
            return new AttachedLoader(
                    NBTHelper.readEnum(nbt, "Type", LoaderType.class),
                    NbtUtils.readBlockPos(nbt, "Pos").orElseThrow()
            );
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof AttachedLoader loader)) return false;
            if (this.type != loader.type) return false;
            if (!Objects.equals(this.pos, loader.pos)) return false;
            return true;
        }
    }
}

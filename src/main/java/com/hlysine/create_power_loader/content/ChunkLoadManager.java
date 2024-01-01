package com.hlysine.create_power_loader.content;

import com.hlysine.create_power_loader.CPLBlockEntityTypes;
import com.hlysine.create_power_loader.CreatePowerLoader;
import com.hlysine.create_power_loader.content.brasschunkloader.BrassChunkLoaderBlockEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;

public class ChunkLoadManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<UUID, Set<LoadedChunkPos>> savedForcedChunks = new HashMap<>();

    public static <T extends Comparable<? super T>> void updateForcedChunks(ServerLevel level, ChunkPos center, T owner, int loadingRange, Set<LoadedChunkPos> forcedChunks) {
        Set<LoadedChunkPos> targetChunks = new HashSet<>();
        for (int i = center.x - loadingRange + 1; i <= center.x + loadingRange - 1; i++) {
            for (int j = center.z - loadingRange + 1; j <= center.z + loadingRange - 1; j++) {
                targetChunks.add(new LoadedChunkPos(level, i, j));
            }
        }

        Set<LoadedChunkPos> unforcedChunks = new HashSet<>();
        for (LoadedChunkPos chunk : forcedChunks) {
            if (targetChunks.contains(chunk)) {
                targetChunks.remove(chunk);
            } else {
                forceChunk(level, owner, chunk.x(), chunk.z(), false);
                unforcedChunks.add(chunk);
            }
        }
        forcedChunks.removeAll(unforcedChunks);
        for (LoadedChunkPos chunk : targetChunks) {
            forceChunk(level, owner, chunk.x(), chunk.z(), true);
            forcedChunks.add(chunk);
        }
    }

    public static <T extends Comparable<? super T>> void unforceAllChunks(ServerLevel level, T owner, Set<LoadedChunkPos> forcedChunks) {
        for (LoadedChunkPos chunk : forcedChunks) {
            forceChunk(level, owner, chunk.x(), chunk.z(), false);
        }
        LOGGER.debug("CPL: unload all, unloaded {} chunks.", forcedChunks.size());
        forcedChunks.clear();
    }

    private static <T extends Comparable<? super T>> void forceChunk(ServerLevel level, T owner, int chunkX, int chunkZ, boolean add) {
        if (owner instanceof BlockPos) {
            ForgeChunkManager.forceChunk(level, CreatePowerLoader.MODID, (BlockPos) owner, chunkX, chunkZ, add, true);
        } else {
            ForgeChunkManager.forceChunk(level, CreatePowerLoader.MODID, (UUID) owner, chunkX, chunkZ, add, true);
        }
    }

    public static Set<LoadedChunkPos> getSavedForcedChunks(UUID entityUUID) {
        return savedForcedChunks.remove(entityUUID);
    }

    public static void validateAllForcedChunks(ServerLevel level, ForgeChunkManager.TicketHelper helper) {
        helper.getBlockTickets().forEach((blockPos, tickets) -> {
            LOGGER.debug("CPL: Inspecting level {} position {} which has {} non-ticking tickets and {} ticking tickets.",
                    level.dimension().location(),
                    blockPos.toShortString(),
                    tickets.getFirst().size(),
                    tickets.getSecond().size());
            IChunkLoaderBlockEntity blockEntity = level.getBlockEntity(blockPos, CPLBlockEntityTypes.BRASS_CHUNK_LOADER.get()).orElse(null);
            if (blockEntity == null)
                blockEntity = level.getBlockEntity(blockPos, CPLBlockEntityTypes.ANDESITE_CHUNK_LOADER.get()).orElse(null);
            if (blockEntity == null) {
                helper.removeAllTickets(blockPos);
                LOGGER.debug("CPL: level {} position {} unforced: Cannot find block entity.", level.dimension().location(), blockPos.toShortString());
                return;
            }
            if (!blockEntity.isSpeedRequirementFulfilled()) {
                helper.removeAllTickets(blockPos);
                LOGGER.debug("CPL: level {} position {} unforced: Speed requirement not fulfilled.", level.dimension().location(), blockPos.toShortString());
                return;
            }
            int range = blockEntity.getLoadingRange();
            ChunkPos center = new ChunkPos(blockPos);
            for (Long chunk : tickets.getFirst()) {
                ChunkPos chunkPos = new ChunkPos(chunk);
                if (Mth.absMax(chunkPos.x - center.x, chunkPos.z - center.z) >= range) {
                    helper.removeTicket(blockPos, chunk, false);
                    LOGGER.debug("CPL: level {} position {} unforced non-ticking {}: Out of range.", level.dimension().location(), blockPos.toShortString(), chunkPos);
                }
            }
            for (Long chunk : tickets.getSecond()) {
                ChunkPos chunkPos = new ChunkPos(chunk);
                if (Mth.absMax(chunkPos.x - center.x, chunkPos.z - center.z) >= range) {
                    helper.removeTicket(blockPos, chunk, true);
                    LOGGER.debug("CPL: level {} position {} unforced ticking {}: Out of range.", level.dimension().location(), blockPos.toShortString(), chunkPos);
                }
            }
            LOGGER.debug("CPL: level {} position {} continues forcing.", level.dimension().location(), blockPos.toShortString());
        });

        helper.getEntityTickets().forEach((entityUUID, tickets) -> {
            // We can't get entities at this point
            // Saving the chunk list for the movement behaviours to use

            Set<LoadedChunkPos> savedChunks = new HashSet<>();
            if (savedForcedChunks.containsKey(entityUUID)) {
                savedChunks = savedForcedChunks.get(entityUUID);
            }
            for (Long chunk : tickets.getFirst()) {
                savedChunks.add(new LoadedChunkPos(level, chunk));
            }
            for (Long chunk : tickets.getSecond()) {
                savedChunks.add(new LoadedChunkPos(level, chunk));
            }
            savedForcedChunks.put(entityUUID, savedChunks);
            LOGGER.debug("CPL: Inspecting entity {} which has {} non-ticking tickets and {} ticking tickets.",
                    entityUUID,
                    tickets.getFirst().size(),
                    tickets.getSecond().size());
        });
    }

    public record LoadedChunkPos(@NotNull ResourceLocation dimension, @NotNull ChunkPos chunkPos) {

        public LoadedChunkPos(@NotNull Level level, long chunkPos) {
            this(level.dimension().location(), new ChunkPos(chunkPos));
        }

        public LoadedChunkPos(@NotNull Level level, int pX, int pZ) {
            this(level.dimension().location(), new ChunkPos(pX, pZ));
        }

        public int x() {
            return this.chunkPos.x;
        }

        public int z() {
            return this.chunkPos.z;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof LoadedChunkPos loadedChunk)) return false;
            if (!Objects.equals(loadedChunk.dimension, this.dimension)) return false;
            if (!Objects.equals(loadedChunk.chunkPos, this.chunkPos)) return false;
            return true;
        }

        @Override
        public String toString() {
            return dimension + ":" + chunkPos;
        }
    }
}

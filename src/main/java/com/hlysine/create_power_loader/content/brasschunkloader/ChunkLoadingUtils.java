package com.hlysine.create_power_loader.content.brasschunkloader;

import com.hlysine.create_power_loader.CPLBlockEntityTypes;
import com.hlysine.create_power_loader.CreatePowerLoader;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.slf4j.Logger;

import java.util.*;

public class ChunkLoadingUtils {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<UUID, Set<ChunkPos>> savedForcedChunks = new HashMap<>();

    public static <T extends Comparable<? super T>> void updateForcedChunks(ServerLevel level, ChunkPos center, T owner, int loadingRange, Set<ChunkPos> forcedChunks) {
        Set<ChunkPos> targetChunks = new HashSet<>();
        for (int i = center.x - loadingRange + 1; i <= center.x + loadingRange - 1; i++) {
            for (int j = center.z - loadingRange + 1; j <= center.z + loadingRange - 1; j++) {
                targetChunks.add(new ChunkPos(i, j));
            }
        }

        Set<ChunkPos> unforcedChunks = new HashSet<>();
        for (ChunkPos chunk : forcedChunks) {
            if (targetChunks.contains(chunk)) {
                targetChunks.remove(chunk);
            } else {
                forceChunk(level, owner, chunk.x, chunk.z, false);
                unforcedChunks.add(chunk);
            }
        }
        forcedChunks.removeAll(unforcedChunks);
        for (ChunkPos chunk : targetChunks) {
            forceChunk(level, owner, chunk.x, chunk.z, true);
            forcedChunks.add(chunk);
        }
    }

    public static <T extends Comparable<? super T>> void unforceAllChunks(ServerLevel level, T owner, Set<ChunkPos> forcedChunks) {
        for (ChunkPos chunk : forcedChunks) {
            forceChunk(level, owner, chunk.x, chunk.z, false);
        }
        LOGGER.debug("Brass chunk loader destroyed, unloaded {} chunks.", forcedChunks.size());
        forcedChunks.clear();
    }

    private static <T extends Comparable<? super T>> void forceChunk(ServerLevel level, T owner, int chunkX, int chunkZ, boolean add) {
        if (owner instanceof BlockPos) {
            ForgeChunkManager.forceChunk(level, CreatePowerLoader.MODID, (BlockPos) owner, chunkX, chunkZ, add, true);
        } else {
            ForgeChunkManager.forceChunk(level, CreatePowerLoader.MODID, (UUID) owner, chunkX, chunkZ, add, true);
        }
    }

    public static void unforceAllChunks(ServerLevel level, UUID entityUUID, Set<ChunkPos> forcedChunks) {
        for (ChunkPos chunk : forcedChunks) {
            ForgeChunkManager.forceChunk(level, CreatePowerLoader.MODID, entityUUID, chunk.x, chunk.z, false, true);
        }
        LOGGER.debug("Entity destroyed, unloaded {} chunks.", forcedChunks.size());
        forcedChunks.clear();
    }

    public static Set<ChunkPos> getSavedForcedChunks(UUID entityUUID) {
        return savedForcedChunks.remove(entityUUID);
    }

    public static void validateAllForcedChunks(ServerLevel level, ForgeChunkManager.TicketHelper helper) {
        helper.getBlockTickets().forEach((blockPos, tickets) -> {
            LOGGER.debug("Inspecting block position {} which has {} non-ticking tickets and {} ticking tickets.",
                    blockPos.toShortString(),
                    tickets.getFirst().size(),
                    tickets.getSecond().size());
            Optional<BrassChunkLoaderBlockEntity> blockEntity = level.getBlockEntity(blockPos, CPLBlockEntityTypes.BRASS_CHUNK_LOADER.get());
            if (blockEntity.isEmpty()) {
                helper.removeAllTickets(blockPos);
                LOGGER.debug("Block position {} unforced: Cannot find block entity.", blockPos.toShortString());
                return;
            }
            if (!blockEntity.get().isSpeedRequirementFulfilled()) {
                helper.removeAllTickets(blockPos);
                LOGGER.debug("Block position {} unforced: Speed requirement not fulfilled.", blockPos.toShortString());
                return;
            }
            int range = blockEntity.get().getLoadingRange();
            ChunkPos center = new ChunkPos(blockPos);
            for (Long chunk : tickets.getFirst()) {
                ChunkPos chunkPos = new ChunkPos(chunk);
                if (Mth.absMax(chunkPos.x - center.x, chunkPos.z - center.z) >= range) {
                    helper.removeTicket(blockPos, chunk, false);
                    LOGGER.debug("Block position {} unforced non-ticking {}: Out of range.", blockPos.toShortString(), chunkPos);
                }
            }
            for (Long chunk : tickets.getSecond()) {
                ChunkPos chunkPos = new ChunkPos(chunk);
                if (Mth.absMax(chunkPos.x - center.x, chunkPos.z - center.z) >= range) {
                    helper.removeTicket(blockPos, chunk, true);
                    LOGGER.debug("Block position {} unforced ticking {}: Out of range.", blockPos.toShortString(), chunkPos);
                }
            }
            LOGGER.debug("Block position {} continues forcing.", blockPos.toShortString());
        });

        helper.getEntityTickets().forEach((entityUUID, tickets) -> {
            // We can't get entities at this point
            // Saving the chunk list for the movement behaviours to use

            Set<ChunkPos> savedChunks = new HashSet<>();
            if (savedForcedChunks.containsKey(entityUUID)) {
                savedChunks = savedForcedChunks.get(entityUUID);
            }
            for (Long chunk : tickets.getFirst()) {
                savedChunks.add(new ChunkPos(chunk));
            }
            for (Long chunk : tickets.getSecond()) {
                savedChunks.add(new ChunkPos(chunk));
            }
            savedForcedChunks.put(entityUUID, savedChunks);
            LOGGER.debug("Inspecting entity {} which has {} non-ticking tickets and {} ticking tickets.",
                    entityUUID,
                    tickets.getFirst().size(),
                    tickets.getSecond().size());
        });
    }
}

package com.hlysine.create_power_loader.compat;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SableCompat {
    /**
     * UUIDs of sub-levels that have an active CPL chunk loader on them.
     * The PhysicsChunkTicketManagerMixin checks this set to prevent storage.
     * Uses sub-level UUID (from SubLevelAccess.getUniqueId()), not contraption UUID.
     */
    public static final Set<UUID> pinnedSubLevels = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Projects a block position out of a Sable sub-level into real-world coordinates.
     * Returns null if the position is not currently inside an active sub-level, which
     * can happen when the sub-level has been put into storage (e.g. traveled beyond
     * simulation distance). Callers must treat null as "projection unavailable" and
     * avoid loading chunks based on stale or incorrect coordinates.
     */
    public static BlockPos projectOutOfSubLevel(Level level, BlockPos blockPos) {
        if (SableCompanion.INSTANCE.getContaining(level, blockPos.getCenter()) != null) {
            return BlockPos.containing(SableCompanion.INSTANCE.projectOutOfSubLevel(level, (Position) blockPos.getCenter()));
        }
        return blockPos;
    }

    /**
     * Pins the sub-level containing blockPos, preventing Sable from putting it into storage.
     * No-ops if the position is not inside an active sub-level.
     */
    public static void pinSubLevel(Level level, BlockPos blockPos) {
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, blockPos.getCenter());
        if (subLevel == null) return;
        pinnedSubLevels.add(subLevel.getUniqueId());
    }

    /**
     * Removes the pin for the sub-level containing blockPos.
     * No-ops if the position is not inside a sub-level or wasn't pinned.
     */
    public static void unpinSubLevel(Level level, BlockPos blockPos) {
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, blockPos.getCenter());
        if (subLevel == null) return;
        pinnedSubLevels.remove(subLevel.getUniqueId());
    }
}

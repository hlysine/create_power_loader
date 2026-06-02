package com.hlysine.create_power_loader.mixin;

import com.hlysine.create_power_loader.compat.SableCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.ticket.PhysicsChunkTicketManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(value = PhysicsChunkTicketManager.class, remap = false)
public class PhysicsChunkTicketManagerMixin {

    /**
     * Wraps the forceLoaded.contains(subLevel) check in PhysicsChunkTicketManager.update().
     * If the sub-level's UUID is in CPL's pinnedSubLevels set, we treat it as force-loaded
     * regardless of distance, preventing Sable from putting it into storage.
     */
    @WrapOperation(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Collection;contains(Ljava/lang/Object;)Z",
                    ordinal = 0
            )
    )
    private boolean cpl$preventPinnedSubLevelStorage(Collection<ServerSubLevel> forceLoaded, Object subLevel, Operation<Boolean> original,
                                                      ServerLevel level, ServerSubLevelContainer container) {
        if (original.call(forceLoaded, subLevel)) return true;
        if (subLevel instanceof ServerSubLevel serverSubLevel) {
            return SableCompat.pinnedSubLevels.contains(serverSubLevel.getUniqueId());
        }
        return false;
    }
}

package com.hlysine.create_power_loader.mixin.portinglib;

import io.github.fabricators_of_create.porting_lib.chunk.loading.PortingLibChunkManager;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortingLibChunkManager.class)
public class PortingLibChunkManagerMixin {

    @Inject(
            method = "hasForcedChunks",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void hasForcedChunks(ServerLevel level, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!cir.getReturnValue() && level.getForcedChunks().isEmpty());
    }
}

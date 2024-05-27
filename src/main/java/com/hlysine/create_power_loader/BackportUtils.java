package com.hlysine.create_power_loader;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class BackportUtils {
    public static BlockPos blockPosContaining(Vec3 vec) {
        return new BlockPos(vec.x, vec.y, vec.z);
    }
}

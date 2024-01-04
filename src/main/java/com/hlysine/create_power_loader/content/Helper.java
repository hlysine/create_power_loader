package com.hlysine.create_power_loader.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;

public class Helper {
    public static BlockPos blockPosContaining(Position pos) {
        return new BlockPos(Mth.floor(pos.x()), Mth.floor(pos.y()), Mth.floor(pos.z()));
    }
}

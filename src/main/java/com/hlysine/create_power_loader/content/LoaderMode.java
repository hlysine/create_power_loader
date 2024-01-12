package com.hlysine.create_power_loader.content;

import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum LoaderMode implements StringRepresentable {
    STATIC, CONTRAPTION, TRAIN, STATION;

    @Override
    public @NotNull String getSerializedName() {
        return Lang.asId(name());
    }
}

package com.hlysine.create_power_loader.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SableMixinPlugin implements IMixinConfigPlugin {
    private boolean isSableInstalled;

    @Override
    public void onLoad(String mixinPackage) {
        try {
            Class.forName("dev.ryanhcode.sable.sublevel.system.ticket.PhysicsChunkTicketManager", false, this.getClass().getClassLoader());
            isSableInstalled = true;
        } catch (Exception e) {
            isSableInstalled = false;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return isSableInstalled;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

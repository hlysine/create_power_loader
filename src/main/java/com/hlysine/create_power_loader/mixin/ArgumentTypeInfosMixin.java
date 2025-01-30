package com.hlysine.create_power_loader.mixin;

import com.hlysine.create_power_loader.CreatePowerLoader;
import com.hlysine.create_power_loader.content.LoaderMode;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraftforge.server.command.EnumArgument;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArgumentTypeInfos.class)
public abstract class ArgumentTypeInfosMixin {

    @Shadow
    private static <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> ArgumentTypeInfo<A, T> register(Registry<ArgumentTypeInfo<?, ?>> registry, String id, Class<? extends A> argumentClass, ArgumentTypeInfo<A, T> info) {
        throw new NotImplementedException();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(
            method = "bootstrap",
            at = @At("RETURN")
    )
    private static void bootstrap(Registry<ArgumentTypeInfo<?, ?>> registry, CallbackInfoReturnable<ArgumentTypeInfo<?, ?>> cir) {
        register(registry, CreatePowerLoader.asResource("enum").toString(), EnumArgument.class, new EnumArgument.Info());
    }
}

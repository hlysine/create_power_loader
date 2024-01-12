package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.command.ListLoadersCommand;
import com.hlysine.create_power_loader.command.SummaryCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Collections;

public class CPLCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("powerloader")
                .requires(cs -> cs.hasPermission(2))
                .then(SummaryCommand.register())
                .then(ListLoadersCommand.register());

        LiteralCommandNode<CommandSourceStack> cplRoot = dispatcher.register(root);

        CommandNode<CommandSourceStack> c = dispatcher.findNode(Collections.singleton("pl"));
        if (c != null)
            return;

        dispatcher.getRoot().addChild(buildRedirect("pl", cplRoot));
    }

    /**
     * *****
     * https://github.com/VelocityPowered/Velocity/blob/8abc9c80a69158ebae0121fda78b55c865c0abad/proxy/src/main/java/com/velocitypowered/proxy/util/BrigadierUtils.java#L38
     * *****
     * <p>
     * Returns a literal node that redirects its execution to
     * the given destination node.
     *
     * @param alias       the command alias
     * @param destination the destination node
     * @return the built node
     */
    public static LiteralCommandNode<CommandSourceStack> buildRedirect(final String alias, final LiteralCommandNode<CommandSourceStack> destination) {
        // Redirects only work for nodes with children, but break the top argument-less command.
        // Manually adding the root command after setting the redirect doesn't fix it.
        // See https://github.com/Mojang/brigadier/issues/46). Manually clone the node instead.
        LiteralArgumentBuilder<CommandSourceStack> builder = LiteralArgumentBuilder
                .<CommandSourceStack>literal(alias)
                .requires(destination.getRequirement())
                .forward(destination.getRedirect(), destination.getRedirectModifier(), destination.isFork())
                .executes(destination.getCommand());
        for (CommandNode<CommandSourceStack> child : destination.getChildren()) {
            builder.then(child);
        }
        return builder.build();
    }

}


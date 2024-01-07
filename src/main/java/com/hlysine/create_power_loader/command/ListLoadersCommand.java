package com.hlysine.create_power_loader.command;

import com.hlysine.create_power_loader.content.ChunkLoadManager;
import com.hlysine.create_power_loader.content.ChunkLoader;
import com.hlysine.create_power_loader.content.LoaderMode;
import com.hlysine.create_power_loader.content.WeakCollection;
import com.hlysine.create_power_loader.content.trains.CarriageChunkLoader;
import com.hlysine.create_power_loader.content.trains.StationChunkLoader;
import com.hlysine.create_power_loader.content.trains.TrainChunkLoader;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.command.EnumArgument;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ListLoadersCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("list")
                .requires(cs -> cs.hasPermission(2))
                .then(
                        Commands.argument("type", EnumArgument.enumArgument(LoaderMode.class))
                                .then(
                                        Commands.literal("limit")
                                                .then(
                                                        Commands.argument("limit", IntegerArgumentType.integer(1))
                                                                .executes(handler(true, true))
                                                )
                                )
                                .executes(handler(true, false))
                )
                .then(
                        Commands.literal("limit")
                                .then(
                                        Commands.argument("limit", IntegerArgumentType.integer(1))
                                                .executes(handler(false, true))
                                )
                )
                .executes(handler(false, false));
    }

    private static Command<CommandSourceStack> handler(boolean hasMode, boolean hasLimit) {
        return ctx -> {
            CommandSourceStack source = ctx.getSource();
            fillReport(source.getLevel(), source.getPosition(),
                    hasMode ? ctx.getArgument("type", LoaderMode.class) : null,
                    hasLimit ? ctx.getArgument("limit", Integer.class) : Integer.MAX_VALUE,
                    (s, f) -> source.sendSuccess(() -> Components.literal(s).withStyle(st -> st.withColor(f)), false),
                    (c) -> source.sendSuccess(() -> c, false));
            return Command.SINGLE_SUCCESS;
        };
    }

    private static void fillReport(ServerLevel level, Vec3 location, @Nullable LoaderMode mode, int limit, BiConsumer<String, Integer> chat,
                                   Consumer<Component> chatRaw) {
        int white = ChatFormatting.WHITE.getColor();
        int blue = 0xD3DEDC;
        int bright = 0xFFEFEF;
        int orange = 0xFFAD60;

        List<ChunkLoader> loaders = new LinkedList<>();
        if (mode == null) {
            for (WeakCollection<ChunkLoader> list : ChunkLoadManager.allLoaders.values()) {
                loaders.addAll(list);
            }
        } else {
            loaders.addAll(ChunkLoadManager.allLoaders.get(mode));
        }
        loaders.removeIf(loader -> loader.getForcedChunks().size() == 0);

        Map<ResourceLocation, DimensionType> typeCache = new HashMap<>();
        MinecraftServer server = level.getServer();
        Function<ResourceLocation, DimensionType> computeType = key -> server.getLevel(ResourceKey.create(Registries.DIMENSION, key)).dimensionType();
        List<Pair<ChunkLoader, Pair<ResourceLocation, Vec3>>> pairs = loaders.stream()
                .map(loader -> Pair.of(loader, loader.getLocation()))
                .map(pair -> Pair.of(pair.getFirst(), Pair.of(pair.getSecond().getFirst(), Vec3.atCenterOf(pair.getSecond().getSecond()))))
                .sorted(Comparator
                        .<Pair<ChunkLoader, Pair<ResourceLocation, Vec3>>>comparingInt(p -> p.getSecond().getFirst().equals(level.dimension().location()) ? 0 : 1)
                        .thenComparingDouble(p -> p.getSecond().getSecond()
                                .scale(DimensionType.getTeleportationScale(typeCache.computeIfAbsent(p.getSecond().getFirst(), computeType), level.dimensionType()))
                                .distanceToSqr(location))
                )
                .limit(limit)
                .toList();

        chat.accept("", white);
        chat.accept("-+------<< Chunk Loader List >>------+-", white);
        chat.accept(pairs.size() + " out of " + loaders.size() + " nearest" + (mode != null ? " " + mode.getSerializedName() : "") + " loaders", blue);
        for (Pair<ChunkLoader, Pair<ResourceLocation, Vec3>> pair : pairs) {
            ChunkLoader loader = pair.getFirst();
            ResourceLocation dimension = pair.getSecond().getFirst();
            BlockPos pos = BlockPos.containing(pair.getSecond().getSecond());

            chatRaw.accept(createTpButton(dimension, pos,
                    (mode == null ? " " + loader.getLoaderMode().getSerializedName() + " " : "")
                            + "[" + pos.toShortString() + "]"
                            + (!dimension.equals(level.dimension().location()) ? " in " + dimension : "")
                    , white));

            chat.accept(
                    "    "
                            + loader.getLoaderType().getSerializedName() + " - "
                            + loader.getForcedChunks().size() + " chunks"
                    , orange);
            if (loader instanceof TrainChunkLoader trainLoader) {
                for (int i = 0; i < trainLoader.carriageLoaders.size(); i++) {
                    CarriageChunkLoader carriageLoader = trainLoader.carriageLoaders.get(i);
                    if (carriageLoader.getForcedChunks().isEmpty()) continue;
                    Pair<ResourceLocation, BlockPos> carriageLocation = carriageLoader.getLocation();
                    chatRaw.accept(createTpButton(carriageLocation.getFirst(), carriageLocation.getSecond(),
                            "  Carriage " + (i + 1) + " - "
                                    + "[" + carriageLocation.getSecond().toShortString() + "]"
                                    + (!carriageLocation.getFirst().equals(level.dimension().location()) ? " in " + carriageLocation.getFirst().toString() : "")
                            , blue));
                    chat.accept(
                            "      "
                                    + carriageLoader.getLoaderType().getSerializedName() + " - "
                                    + carriageLoader.getForcedChunks().size() + " chunks"
                            , orange);
                }
            } else if (loader instanceof StationChunkLoader stationLoader) {
                for (StationChunkLoader.AttachedLoader attachment : stationLoader.attachments) {
                    chatRaw.accept(createTpButton(stationLoader.getLocation().getFirst(), attachment.pos(),
                            "  "
                                    + attachment.type().getSerializedName()
                                    + " - "
                                    + "[" + attachment.pos().toShortString() + "]"
                            , blue));
                }
            }
        }
        chat.accept("-+--------------------------------+-", white);
    }

    private static Component createTpButton(ResourceLocation dimension, BlockPos blockPos, String text, int color) {
        String teleport = "/execute in " + dimension.toString() + " run tp @s " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ();
        return Components.literal(text).withStyle((p_180514_) -> {
            return p_180514_.withColor(color)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, teleport))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Components.literal("Click to teleport")))
                    .withInsertion(teleport);
        });
    }

}

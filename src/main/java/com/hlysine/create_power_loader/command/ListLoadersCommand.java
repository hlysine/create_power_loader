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
import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.command.EnumArgument;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ListLoadersCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("list")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("type", EnumArgument.enumArgument(LoaderMode.class))
                        .then(Commands.literal("active")
                                .then(Commands.literal("limit")
                                        .then(Commands.argument("limit", IntegerArgumentType.integer(1))
                                                .executes(handler(true, true, true))
                                        )
                                ).executes(handler(true, false, true))
                        )
                        .then(Commands.literal("all")
                                .then(Commands.literal("limit")
                                        .then(Commands.argument("limit", IntegerArgumentType.integer(1))
                                                .executes(handler(true, true, false))
                                        )
                                ).executes(handler(true, false, false))
                        )
                )
                .then(Commands.literal("all")
                        .then(Commands.literal("active")
                                .then(Commands.literal("limit")
                                        .then(Commands.argument("limit", IntegerArgumentType.integer(1))
                                                .executes(handler(false, true, true))
                                        )
                                ).executes(handler(false, false, true))
                        )
                        .then(Commands.literal("all")
                                .then(Commands.literal("limit")
                                        .then(Commands.argument("limit", IntegerArgumentType.integer(1))
                                                .executes(handler(false, true, false))
                                        )
                                ).executes(handler(false, false, false))
                        )
                );
    }

    private static Command<CommandSourceStack> handler(boolean hasMode, boolean hasLimit, boolean activeOnly) {
        return ctx -> {
            CommandSourceStack source = ctx.getSource();
            fillReport(source.getLevel(), source.getPosition(),
                    hasMode ? ctx.getArgument("type", LoaderMode.class) : null,
                    hasLimit ? ctx.getArgument("limit", Integer.class) : 20,
                    activeOnly,
                    (s, f) -> source.sendSuccess(() -> Component.literal(s).withStyle(st -> st.withColor(f)), false),
                    (c) -> source.sendSuccess(() -> c, false));
            return Command.SINGLE_SUCCESS;
        };
    }

    private static void fillReport(ServerLevel level,
                                   Vec3 location,
                                   @Nullable LoaderMode mode,
                                   int limit,
                                   boolean activeOnly,
                                   BiConsumer<String, Integer> chat,
                                   Consumer<Component> chatRaw) {
        int white = ChatFormatting.WHITE.getColor();
        int gray = ChatFormatting.GRAY.getColor();
        int blue = 0xD3DEDC;
        int darkBlue = 0x5955A1;
        int orange = 0xFFAD60;

        List<ChunkLoader> loaders = new LinkedList<>();
        if (mode == null) {
            for (WeakCollection<ChunkLoader> list : ChunkLoadManager.allLoaders.values()) {
                loaders.addAll(list);
            }
        } else {
            loaders.addAll(ChunkLoadManager.allLoaders.get(mode));
        }
        loaders.removeIf(loader -> {
            if (loader instanceof TrainChunkLoader trainLoader) {
                for (CarriageChunkLoader carriageLoader : trainLoader.carriageLoaders) {
                    if (carriageLoader.known && (carriageLoader.brass || carriageLoader.andesite)) return false;
                }
                return true;
            } else if (loader instanceof StationChunkLoader stationLoader) {
                return stationLoader.attachments.size() == 0;
            } else {
                return false;
            }
        });
        if (activeOnly)
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
        chat.accept(pairs.size() + " out of " + loaders.size() + " nearest" + (activeOnly ? " active" : "") + (mode != null ? " " + mode.getSerializedName() : "") + " loaders", blue);
        chat.accept("", white);
        for (Pair<ChunkLoader, Pair<ResourceLocation, Vec3>> pair : pairs) {
            ChunkLoader loader = pair.getFirst();
            ResourceLocation dimension = pair.getSecond().getFirst();
            BlockPos pos = BlockPos.containing(pair.getSecond().getSecond());

            chatRaw.accept(
                    text(mode == null ? loader.getLoaderMode().getSerializedName() + " - " : "", white)
                            .append(text(loader.getLoaderType().getSerializedName() + " - ", orange))
                            .append(text(loader.getForcedChunks().size() + " chunks", colorForCount(loader.getForcedChunks().size())))
            );

            chatRaw.accept(
                    text("                    ↳ ", darkBlue)
                            .append(createTpButton(level.dimension().location(), dimension, pos, darkBlue))
            );
            if (loader instanceof TrainChunkLoader trainLoader) {
                for (int i = 0; i < trainLoader.carriageLoaders.size(); i++) {
                    CarriageChunkLoader carriageLoader = trainLoader.carriageLoaders.get(i);
                    if (carriageLoader.getForcedChunks().isEmpty()) continue;
                    Pair<ResourceLocation, BlockPos> carriageLocation = carriageLoader.getLocation();
                    chatRaw.accept(
                            text("    Carriage " + (i + 1) + " - ", gray)
                                    .append(text(carriageLoader.getLoaderType().getSerializedName() + " - ", orange))
                                    .append(text(carriageLoader.getForcedChunks().size() + " chunks", colorForCount(carriageLoader.getForcedChunks().size())))
                    );
                    chatRaw.accept(
                            text("                    ↳ ", darkBlue)
                                    .append(createTpButton(level.dimension().location(), carriageLocation.getFirst(), carriageLocation.getSecond(), darkBlue))
                    );
                }
            } else if (loader instanceof StationChunkLoader stationLoader) {
                for (StationChunkLoader.AttachedLoader attachment : stationLoader.attachments) {
                    chatRaw.accept(
                            text("    ", gray)
                                    .append(text("Attached - ", gray))
                                    .append(text(attachment.type().getSerializedName(), orange))
                    );
                    chatRaw.accept(
                            text("                    ↳ ", darkBlue)
                                    .append(createTpButton(level.dimension().location(), stationLoader.getLocation().getFirst(), attachment.pos(), darkBlue))
                    );
                }
            }
        }
        chat.accept("-+--------------------------------+-", white);
    }

    private static int colorForCount(int count) {
        if (count == 0) return ChatFormatting.DARK_GRAY.getColor();
        if (count < 5) return ChatFormatting.GRAY.getColor();
        if (count < 10) return ChatFormatting.YELLOW.getColor();
        return ChatFormatting.RED.getColor();
    }

    private static String shortString(ResourceLocation location) {
        if (location.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) return location.getPath();
        return location.toString();
    }

    private static MutableComponent text(String text, int color) {
        return Component.literal(text).withStyle(style -> style.withColor(color));
    }

    private static MutableComponent createTpButton(ResourceLocation origin, ResourceLocation dimension, BlockPos blockPos, int color) {
        String teleport = "/execute in " + dimension.toString() + " run tp @s " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ();
        return Component.literal("[" + blockPos.toShortString() + "]" + (!origin.equals(dimension) ? " in " + shortString(dimension) : "")).withStyle((style) -> style
                .withColor(color)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, teleport))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to teleport")))
                .withInsertion(teleport));
    }

}

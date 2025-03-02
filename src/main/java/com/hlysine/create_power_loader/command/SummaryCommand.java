package com.hlysine.create_power_loader.command;

import com.hlysine.create_power_loader.content.*;
import com.hlysine.create_power_loader.content.trains.CarriageChunkLoader;
import com.hlysine.create_power_loader.content.trains.StationChunkLoader;
import com.hlysine.create_power_loader.content.trains.TrainChunkLoader;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SummaryCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("summary")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("dimension", DimensionArgument.dimension())
                        .executes(ctx -> {
                            CommandSourceStack source = ctx.getSource();
                            fillReport(ctx.getArgument("dimension", ResourceLocation.class),
                                    (s, f) -> source.sendSuccess(() -> Component.literal(s).withStyle(st -> st.withColor(f)), false),
                                    (c) -> source.sendSuccess(() -> c, false));
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .executes(ctx -> {
                    CommandSourceStack source = ctx.getSource();
                    fillReport(null,
                            (s, f) -> source.sendSuccess(() -> Component.literal(s).withStyle(st -> st.withColor(f)), false),
                            (c) -> source.sendSuccess(() -> c, false));
                    return Command.SINGLE_SUCCESS;
                });
    }

    private static int white = ChatFormatting.WHITE.getColor();
    private static int gray = ChatFormatting.GRAY.getColor();
    private static int blue = 0xD3DEDC;

    private static void fillReport(@Nullable ResourceLocation dimension,
                                   BiConsumer<String, Integer> chat,
                                   Consumer<Component> chatRaw) {
        List<ChunkLoader> loaders = new LinkedList<>();
        for (WeakCollection<ChunkLoader> list : ChunkLoadManager.allLoaders.values()) {
            loaders.addAll(list);
        }
        if (dimension != null) {
            loaders.removeIf(loader -> !loader.getLocation().getFirst().equals(dimension));
        }

        chat.accept("", white);
        chat.accept("-+------<< Chunk Loader Summary >>------+-", white);
        if (dimension != null)
            chat.accept("For " + dimension, gray);
        genSummary(null, loaders, chat, chatRaw);
        chat.accept("", white);
        chat.accept("-+------------------------------------+-", white);
    }

    private static void genSummary(@Nullable LoaderMode mode,
                                   List<ChunkLoader> loaders,
                                   BiConsumer<String, Integer> chat,
                                   Consumer<Component> chatRaw) {
        if (mode == LoaderMode.STATIC || mode == null) {
            chat.accept("", white);
            chat.accept("Static chunk loaders", white);
            int staticLoaders = 0;
            int functional = 0;
            int brass = 0;
            int andesite = 0;
            Set<ChunkLoadManager.LoadedChunkPos> chunks = new HashSet<>();
            for (ChunkLoader loader : loaders) {
                if (loader instanceof AbstractChunkLoaderBlockEntity be) {
                    staticLoaders++;
                    if (be.getForcedChunks().size() > 0) functional++;
                    if (be.type == LoaderType.BRASS) brass++;
                    else if (be.type == LoaderType.ANDESITE) andesite++;
                    chunks.addAll(be.getForcedChunks());
                }
            }
            chatRaw.accept(line("Total blocks", staticLoaders));
            chatRaw.accept(line("Functional blocks", functional));
            chatRaw.accept(line("Brass loaders", brass));
            chatRaw.accept(line("Andesite loaders", andesite));
            chatRaw.accept(line("Loaded chunks", chunks.size()));
        }
        if (mode == LoaderMode.CONTRAPTION || mode == null) {
            chat.accept("", white);
            chat.accept("Contraption chunk loaders", white);
            int nonTrain = 0;
            Set<ChunkLoadManager.LoadedChunkPos> chunks = new HashSet<>();
            for (ChunkLoader loader : loaders) {
                if (loader instanceof ChunkLoaderMovementBehaviour.SavedState state) {
                    if (!state.isTrain) nonTrain++;
                    chunks.addAll(state.forcedChunks);
                }
            }
            chatRaw.accept(line("Non-train contraptions", nonTrain));
            chatRaw.accept(line("Loaded chunks", chunks.size()));
        }
        if (mode == LoaderMode.TRAIN || mode == null) {
            chat.accept("", white);
            chat.accept("Train chunk loaders", white);
            int trains = 0;
            int carriages = 0;
            int functionalTrains = 0;
            int functionalCarriages = 0;
            int unknown = 0;
            int brassAndAndesite = 0;
            int brass = 0;
            int andesite = 0;
            Set<ChunkLoadManager.LoadedChunkPos> chunks = new HashSet<>();
            for (ChunkLoader loader : loaders) {
                if (loader instanceof TrainChunkLoader train) {
                    trains++;
                    carriages += train.carriageLoaders.size();
                    if (train.getForcedChunks().size() > 0) functionalTrains++;
                    for (CarriageChunkLoader carriage : train.carriageLoaders) {
                        if (carriage.getForcedChunks().size() > 0) functionalCarriages++;
                        if (!carriage.known) unknown++;
                        else if (carriage.brass && carriage.andesite) brassAndAndesite++;
                        else if (carriage.brass) brass++;
                        else if (carriage.andesite) andesite++;
                        chunks.addAll(carriage.getForcedChunks());
                    }
                }
            }
            chatRaw.accept(line("Total trains", trains));
            chatRaw.accept(line("Total carriages", carriages));
            chatRaw.accept(line("Functional trains", functionalTrains));
            chatRaw.accept(line("Functional carriages", functionalCarriages));
            chatRaw.accept(line("Unknown carriages", unknown));
            chatRaw.accept(line("Brass+Andesite carriages", brassAndAndesite));
            chatRaw.accept(line("Brass carriages", brass));
            chatRaw.accept(line("Andesite carriages", andesite));
            chatRaw.accept(line("Loaded chunks", chunks.size()));
        }
        if (mode == LoaderMode.STATION || mode == null) {
            chat.accept("", white);
            chat.accept("Station chunk loaders", white);
            int stations = 0;
            int attachments = 0;
            int activeStations = 0;
            int activeAttachments = 0;
            int brass = 0;
            int andesite = 0;
            Set<ChunkLoadManager.LoadedChunkPos> chunks = new HashSet<>();
            for (ChunkLoader loader : loaders) {
                if (loader instanceof StationChunkLoader station) {
                    stations++;
                    attachments += station.attachments.size();
                    if (station.getForcedChunks().size() > 0) {
                        activeStations++;
                        activeAttachments += station.attachments.size();
                    }
                    for (StationChunkLoader.AttachedLoader attachment : station.attachments) {
                        if (attachment.type() == LoaderType.ANDESITE) andesite++;
                        else if (attachment.type() == LoaderType.BRASS) brass++;
                    }
                    chunks.addAll(station.getForcedChunks());
                }
            }
            chatRaw.accept(line("Total stations", stations));
            chatRaw.accept(line("Total attachments", attachments));
            chatRaw.accept(line("Active stations", activeStations));
            chatRaw.accept(line("Active attachments", activeAttachments));
            chatRaw.accept(line("Brass attachments", brass));
            chatRaw.accept(line("Andesite attachments", andesite));
            chatRaw.accept(line("Loaded chunks", chunks.size()));
        }
        if (mode == null) {
            chat.accept("", white);
            chat.accept("All chunk loaders", white);
            chatRaw.accept(line("Total units", loaders.size()));
            Set<ChunkLoadManager.LoadedChunkPos> allChunks = new HashSet<>();
            loaders.forEach(loader -> allChunks.addAll(loader.getForcedChunks()));
            chatRaw.accept(line("Total chunks", allChunks.size()));
        }
    }

    private static MutableComponent text(String text, int color) {
        return Component.literal(text).withStyle(style -> style.withColor(color));
    }

    private static MutableComponent line(String label, Object value) {
        return text("    ", gray).append(text(label + ": ", gray)).append(text(String.valueOf(value), blue));
    }
}

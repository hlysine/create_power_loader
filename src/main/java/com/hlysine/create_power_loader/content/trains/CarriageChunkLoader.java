package com.hlysine.create_power_loader.content.trains;

import com.hlysine.create_power_loader.CPLBlocks;
import com.hlysine.create_power_loader.config.CPLConfigs;
import com.hlysine.create_power_loader.content.ChunkLoadManager;
import com.hlysine.create_power_loader.content.ChunkLoader;
import com.hlysine.create_power_loader.content.LoaderMode;
import com.hlysine.create_power_loader.content.LoaderType;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static com.hlysine.create_power_loader.content.ChunkLoadManager.LoadedChunkPos;
import static com.hlysine.create_power_loader.content.Helper.blockPosContaining;

public class CarriageChunkLoader implements ChunkLoader {
    public final Carriage carriage;
    public boolean known;
    public boolean andesite;
    public boolean brass;
    public final Set<LoadedChunkPos> forcedChunks = new HashSet<>();

    public CarriageChunkLoader(Carriage carriage, boolean known, boolean andesite, boolean brass) {
        this.carriage = carriage;
        this.known = known;
        this.andesite = andesite;
        this.brass = brass;
    }

    @Override
    public @NotNull Set<LoadedChunkPos> getForcedChunks() {
        return forcedChunks;
    }

    @Override
    public LoaderMode getLoaderMode() {
        return LoaderMode.TRAIN;
    }

    @Override
    public LoaderType getLoaderType() {
        return brass ? LoaderType.BRASS : LoaderType.ANDESITE;
    }

    @Override
    public Pair<ResourceLocation, BlockPos> getLocation() {
        if (carriage.train.graph == null) return null;
        return Pair.of(
                carriage.leadingBogey().trailing().node1.getLocation().getDimension().location(),
                blockPosContaining(carriage.leadingBogey().trailing().getPosition(carriage.train.graph))
        );
    }

    public void tick(Level level) {
        if (!known) updateCarriage();
        if (!known) return;
        if (!canLoadChunks()) {
            if (!forcedChunks.isEmpty())
                ChunkLoadManager.unforceAllChunks(level.getServer(), carriage.train.id, forcedChunks);
            return;
        }

        Set<LoadedChunkPos> loadTargets = new HashSet<>();

        addLoadTargets(loadTargets, carriage.leadingBogey().trailing());
        addLoadTargets(loadTargets, carriage.trailingBogey().leading());

        ChunkLoadManager.updateForcedChunks(level.getServer(), loadTargets, carriage.train.id, CPLConfigs.server().getFor(getLoaderType()).rangeOnTrain.get(), forcedChunks);
    }

    public void onRemove() {
        ChunkLoadManager.enqueueUnforceAll(carriage.train.id, forcedChunks);
    }

    private void addLoadTargets(Set<LoadedChunkPos> loadTargets, TravellingPoint point) {
        if (point.edge.isInterDimensional()) {
            loadTargets.add(new LoadedChunkPos(
                    point.node1.getLocation().getDimension().location(),
                    new ChunkPos(blockPosContaining(point.node1.getLocation().getLocation()))
            ));
            loadTargets.add(new LoadedChunkPos(
                    point.node2.getLocation().getDimension().location(),
                    new ChunkPos(blockPosContaining(point.node2.getLocation().getLocation()))
            ));
        } else {
            loadTargets.add(new LoadedChunkPos(
                    point.node1.getLocation().getDimension().location(),
                    new ChunkPos(blockPosContaining(point.getPosition(carriage.train.graph)))
            ));
        }
    }

    private void updateCarriage() {
        CarriageContraptionEntity entity = carriage.anyAvailableEntity();
        known = entity != null;
        if (!known) return;

        Contraption contraption = entity.getContraption();
        andesite = !contraption.isActorTypeDisabled(ItemStack.EMPTY) && !contraption.isActorTypeDisabled(CPLBlocks.ANDESITE_CHUNK_LOADER.asStack());
        brass = !contraption.isActorTypeDisabled(ItemStack.EMPTY) && !contraption.isActorTypeDisabled(CPLBlocks.BRASS_CHUNK_LOADER.asStack());
        if (!andesite && !brass) return;

        boolean hasAndesite = false, hasBrass = false;
        for (MutablePair<StructureTemplate.StructureBlockInfo, MovementContext> actor : entity.getContraption().getActors()) {
            if (!hasAndesite && actor.left.state.is(CPLBlocks.ANDESITE_CHUNK_LOADER.get())) {
                hasAndesite = true;
            }
            if (!hasBrass && actor.left.state.is(CPLBlocks.BRASS_CHUNK_LOADER.get())) {
                hasBrass = true;
            }
            if (hasAndesite && hasBrass) break;
        }
        andesite = hasAndesite;
        brass = hasBrass;
    }

    private boolean canLoadChunks() {
        if (carriage.train.graph == null) return false;
        return andesite && CPLConfigs.server().andesite.enableTrain.get() || brass && CPLConfigs.server().brass.enableTrain.get();
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        if (known) {
            nbt.putBoolean("andesite", andesite);
            nbt.putBoolean("brass", brass);
        }
        return nbt;
    }

    public static CarriageChunkLoader read(Carriage carriage, CompoundTag nbt) {
        if (nbt.contains("andesite") && nbt.contains("brass"))
            return new CarriageChunkLoader(carriage, true, nbt.getBoolean("andesite"), nbt.getBoolean("brass"));
        return new CarriageChunkLoader(carriage, false, false, false);
    }
}

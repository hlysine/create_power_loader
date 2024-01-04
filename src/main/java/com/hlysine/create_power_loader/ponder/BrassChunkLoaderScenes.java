package com.hlysine.create_power_loader.ponder;

import com.hlysine.create_power_loader.content.AbstractChunkLoaderBlockEntity;
import com.hlysine.create_power_loader.content.brasschunkloader.BrassChunkLoaderBlockEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.MinecartElement;
import com.simibubi.create.foundation.ponder.element.ParrotElement;
import com.simibubi.create.foundation.ponder.element.ParrotElement.FacePointOfInterestPose;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BrassChunkLoaderScenes {
    public static void basicUsage(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("brass_chunk_loader_basic", "Basic Usage of the Brass Chunk Loader");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(util.select.layers(1, 2), Direction.DOWN);
        scene.idle(10);

        BlockPos loaderPos = new BlockPos(2, 2, 2);

        scene.effects.rotationSpeedIndicator(loaderPos);
        scene.effects.indicateSuccess(loaderPos);
        scene.idle(10);
        scene.overlay.showText(80)
                .text("The chunk loader keeps the current chunk loaded when given rotational power")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.topOf(loaderPos));
        scene.idle(100);

        scene.world.modifyKineticSpeed(util.select.everywhere(), f -> f / 4f);
        scene.effects.rotationSpeedIndicator(loaderPos);
        scene.idle(30);

        scene.overlay.showText(60)
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .text("It does not work when the rotation speed is too slow")
                .attachKeyFrame()
                .pointAt(util.vector.topOf(loaderPos));
        scene.idle(80);

        scene.world.modifyKineticSpeed(util.select.everywhere(), f -> f * 4f);
        scene.effects.rotationSpeedIndicator(loaderPos);
        scene.idle(20);


        Vec3 scrollSlot = util.vector.of(2.5, 2 + 4 / 16f, 2);
        scene.overlay.showFilterSlotInput(scrollSlot, Direction.NORTH, 170);
        scene.overlay.showText(60)
                .text("You can configure the loaded range through the value panel...")
                .pointAt(scrollSlot)
                .attachKeyFrame()
                .placeNearTarget();

        scene.idle(70);

        scene.overlay.showControls(new InputWindowElement(scrollSlot, Pointing.RIGHT).rightClick(), 20);
        scene.idle(5);
        scene.world.modifyBlockEntity(loaderPos, BrassChunkLoaderBlockEntity.class, be -> be.setLoadingRange(2));
        scene.idle(15);

        scene.overlay.showText(60)
                .colored(PonderPalette.RED)
                .text("...but a longer range requires a higher rotation speed")
                .pointAt(scrollSlot)
                .placeNearTarget();

        scene.idle(80);

        scene.world.modifyKineticSpeed(util.select.everywhere(), f -> f * 2f);
        scene.effects.rotationSpeedIndicator(loaderPos);
        scene.idle(40);
    }

    public static void redstone(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("brass_chunk_loader_redstone", "Redstone and the Brass Chunk Loader");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(util.select.layers(1, 2), Direction.DOWN);
        scene.idle(20);

        BlockPos loaderPos = new BlockPos(1, 1, 3);

        scene.world.toggleRedstonePower(util.select.fromTo(3, 1, 1, 1, 1, 3));
        scene.effects.indicateRedstone(new BlockPos(3, 1, 1));
        scene.world.setKineticSpeed(util.select.fromTo(3, 1, 3, 1, 1, 3), 128f);

        scene.overlay.showText(60)
                .attachKeyFrame()
                .text("The chunk loader gives comparator output when it is active")
                .pointAt(util.vector.blockSurface(new BlockPos(1, 1, 2), Direction.DOWN))
                .placeNearTarget();
        scene.idle(90);

        scene.world.toggleRedstonePower(util.select.fromTo(3, 1, 1, 1, 1, 3));
        scene.effects.indicateRedstone(new BlockPos(3, 1, 1));
        scene.world.setKineticSpeed(util.select.fromTo(3, 1, 3, 1, 1, 3), 0);

        scene.overlay.showText(90)
                .attachKeyFrame()
                .text("When rotational power is lost, there is a short delay (configurable) before the chunks are unloaded")
                .pointAt(util.vector.topOf(loaderPos))
                .placeNearTarget();
        scene.idle(110);

        scene.world.toggleRedstonePower(util.select.fromTo(3, 1, 1, 1, 1, 3));
        scene.effects.indicateRedstone(new BlockPos(3, 1, 1));
        scene.world.setKineticSpeed(util.select.fromTo(3, 1, 3, 1, 1, 3), 128f);

        scene.overlay.showText(90)
                .text("The chunks will stay loaded if power is restored during this delay")
                .pointAt(util.vector.topOf(loaderPos))
                .placeNearTarget();
        scene.idle(110);
    }

    public static void loadingContraptions(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("brass_chunk_loader_contraptions", "Loading Chunks Around Moving Contraptions");
        scene.configureBasePlate(0, 0, 6);
        scene.world.showSection(util.select.layer(0), Direction.UP);

        scene.world.modifyBlock(
                new BlockPos(4, 3, 4),
                state -> state.setValue(ControlsBlock.OPEN, true).setValue(ControlsBlock.VIRTUAL, true),
                false
        );

        scene.idle(5);

        Selection trainLoader = util.select.position(4, 3, 2);
        Vec3 trainLoaderSurface = util.vector.blockSurface(new BlockPos(4, 3, 2), Direction.WEST);
        scene.world.showSection(util.select.fromTo(5, 1, 0, 3, 4, 5), Direction.DOWN);
        scene.world.setKineticSpeed(trainLoader, 240f);

        scene.idle(10);

        Vec3 birbVec = new Vec3(4.5f, 3.5f, 3.5f);
        ElementLink<ParrotElement> birb = scene.special.createBirb(birbVec, FacePointOfInterestPose::new);

        scene.idle(20);

        scene.overlay.showSelectionWithText(trainLoader, 60)
                .attachKeyFrame()
                .text("Brass chunk loaders also work on contraptions")
                .pointAt(trainLoaderSurface)
                .placeNearTarget();

        scene.idle(80);

        scene.overlay.showText(80)
                .text("They always load a 3x3 chunk area around the contraption")
                .pointAt(util.vector.blockSurface(new BlockPos(4, 1, 4), Direction.WEST))
                .placeNearTarget();

        scene.idle(100);

        scene.world.showSection(util.select.fromTo(2, 1, 0, 0, 4, 5), Direction.DOWN);
        ElementLink<MinecartElement> cart = scene.special.createCart(util.vector.topOf(1, 0, 3), 0, Minecart::new);

        scene.idle(20);

        Selection cartLoader = util.select.position(1, 2, 3);
        Vec3 cartLoaderSurface = util.vector.blockSurface(new BlockPos(1, 2, 3), Direction.WEST);
        scene.world.toggleRedstonePower(util.select.everywhere());
        scene.effects.indicateRedstone(new BlockPos(1, 1, 1));
        scene.world.setKineticSpeed(cartLoader, 240f);

        scene.idle(20);

        scene.overlay.showSelectionWithText(cartLoader, 80)
                .text("When assembled, they do not require rotational power to function")
                .attachKeyFrame()
                .pointAt(cartLoaderSurface)
                .placeNearTarget();

        scene.idle(100);

        scene.overlay.showText(60)
                .text("They also function on stationary contraptions")
                .pointAt(util.vector.blockSurface(new BlockPos(1, 1, 3), Direction.WEST))
                .placeNearTarget();

        scene.idle(80);
    }

    public static void attachStation(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("brass_chunk_loader_station", "Loading Chunks For Train Stations");
        scene.configureBasePlate(30, 0, 9);
        scene.scaleSceneView(0.75f);

        scene.world.cycleBlockProperty(util.grid.at(60, 3, 6), BlazeBurnerBlock.HEAT_LEVEL);

        BlockPos chunkLoader = util.grid.at(34, 1, 2);
        Selection selectLoader = util.select.position(chunkLoader);
        BlockPos trainStation = util.grid.at(34, 1, 3);
        Selection train = util.select.fromTo(57, 2, 7, 61, 3, 5);
        Selection load = util.select.layer(0).add(selectLoader).add(util.select.position(trainStation));

        scene.showBasePlate();
        scene.idle(5);
        scene.world.showSection(util.select.layersFrom(1).substract(selectLoader), Direction.DOWN);
        scene.idle(10);
        scene.world.showSection(selectLoader, Direction.SOUTH);
        scene.idle(20);


        scene.overlay.showText(50)
                .text("Brass chunk loaders can attach to Train Stations")
                .pointAt(util.vector.blockSurface(chunkLoader, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(70);
        scene.overlay.showText(50)
                .text("Place them next to Train Stations in the correct direction")
                .pointAt(util.vector.blockSurface(chunkLoader, Direction.WEST))
                .placeNearTarget();
        scene.idle(70);

        scene.overlay.chaseBoundingBoxOutline(PonderPalette.BLUE, chunkLoader,
                new AABB(chunkLoader), 150);
        scene.world.hideSection(load, null);
        scene.idle(10);
        scene.overlay.showText(60)
                .text("They do not require rotational power, but are normally inactive")
                .pointAt(util.vector.blockSurface(chunkLoader, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(80);

        ElementLink<WorldSectionElement> trainElement = scene.world.showIndependentSectionImmediately(train);
        scene.world.moveSection(trainElement, new Vec3(-10, 0, 0), 1);
        scene.world.moveSection(trainElement, new Vec3(-15, 0, 0), 60);
        scene.world.animateBogey(util.grid.at(59, 2, 6), 15, 60);
        scene.idle(60);

        scene.addKeyframe();
        scene.world.showSection(load, null);
        scene.world.modifyBlockEntity(chunkLoader, AbstractChunkLoaderBlockEntity.class, be -> be.isLoaderActive = true);
        scene.world.animateTrainStation(trainStation, true);
        scene.idle(20);

        scene.overlay.showText(70)
                .text("When a train arrives at the station, a 3x3 area around the chunk loader is loaded")
                .pointAt(util.vector.blockSurface(chunkLoader, Direction.WEST))
                .placeNearTarget();
        scene.idle(90);

        scene.world.hideSection(load, null);
        scene.world.moveSection(trainElement, new Vec3(-15, 0, 0), 60);
        scene.world.animateBogey(util.grid.at(59, 2, 6), 15, 60);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.BLUE, chunkLoader,
                new AABB(chunkLoader), 100);
        scene.idle(40);

        scene.overlay.showText(50)
                .text("When the train leaves, the area is unloaded again")
                .pointAt(util.vector.blockSurface(chunkLoader, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget();
        scene.idle(60);
        scene.markAsFinished();
    }
}

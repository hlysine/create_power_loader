package com.hlysine.create_power_loader.ponder;

import com.hlysine.create_power_loader.content.brasschunkloader.BrassChunkLoaderBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.MinecartElement;
import com.simibubi.create.foundation.ponder.element.ParrotElement.FacePointOfInterestPose;
import com.simibubi.create.foundation.ponder.element.ParrotElement;
import net.minecraft.world.entity.vehicle.Minecart;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class BrassChunkLoaderScenes {
    public static void basicUsage(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("brass_chunk_loader", "Basic Usage of the Brass Chunk Loader");
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
                .text("The chunk loader keeps the current chunk loaded when given rotational force")
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
                .text("It will not work when the rotation speed is too slow")
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

    public static void loadingContraptions(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("brass_chunk_loader", "Loading Chunks Around Moving Contraptions");
        scene.configureBasePlate(0, 0, 6);
        scene.world.showSection(util.select.layer(0), Direction.UP);

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
                .text("When assembled, they do not require rotational power to work")
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
}

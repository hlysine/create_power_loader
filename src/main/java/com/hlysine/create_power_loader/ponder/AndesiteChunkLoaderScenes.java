package com.hlysine.create_power_loader.ponder;

import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class AndesiteChunkLoaderScenes {
    public static void basicUsage(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("andesite_chunk_loader_basic", "Basic Usage of the Andesite Chunk Loader");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(util.select.layers(1, 2), Direction.DOWN);
        scene.idle(10);

        BlockPos loaderPos = new BlockPos(2, 2, 2);

        scene.effects.rotationSpeedIndicator(loaderPos);
        scene.effects.indicateSuccess(loaderPos);
        scene.idle(10);
        scene.overlay.showText(60)
                .colored(PonderPalette.GREEN)
                .text("The chunk loader keeps the current chunk loaded when given rotational power.")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.topOf(loaderPos));
        scene.idle(80);

        scene.world.modifyKineticSpeed(util.select.everywhere(), f -> f / 4f);
        scene.effects.rotationSpeedIndicator(loaderPos);
        scene.idle(30);

        scene.overlay.showText(50)
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .text("It does not work when the rotation speed is too slow.")
                .attachKeyFrame()
                .pointAt(util.vector.topOf(loaderPos));
        scene.idle(60);
    }
}

package com.hlysine.create_power_loader.ponder;

import com.hlysine.create_power_loader.CPLBlocks;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.phys.Vec3;

import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import net.minecraft.world.entity.Entity;

public class EmptyChunkLoaderScenes {
    public static void usage(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("empty_chunk_loader_basic", "Usage of Empty Chunk Loaders");
        scene.scaleSceneView(5 / 11f);
        scene.configureBasePlate(0, 0, 11);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(10);

        BlockPos center = util.grid.at(5, 1, 5);

        scene.world.createEntity(w -> {
            Ghast ghastEntity = EntityType.GHAST.create(w);
            Vec3 v = util.vector.topOf(center);
            ghastEntity.setPosRaw(v.x, v.y, v.z);
            ghastEntity.setYRot(ghastEntity.yRotO = 180);
            return ghastEntity;
        });

        scene.idle(20);
        scene.overlay
                .showControls(new InputWindowElement(util.vector.centerOf(center.above(4)), Pointing.DOWN).rightClick()
                        .withItem(CPLBlocks.EMPTY_ANDESITE_CHUNK_LOADER.asStack()), 40);
        scene.idle(10);
        scene.overlay.showText(70)
                .text("Right-click a Ghast with an empty chunk loader to capture it")
                .attachKeyFrame()
                .pointAt(util.vector.blockSurface(center.above(4), Direction.WEST))
                .placeNearTarget();
        scene.idle(40);
        scene.overlay
                .showControls(new InputWindowElement(util.vector.centerOf(center.above(4)), Pointing.DOWN).rightClick()
                        .withItem(CPLBlocks.EMPTY_BRASS_CHUNK_LOADER.asStack()), 40);
        scene.idle(50);

        scene.world.modifyEntities(Ghast.class, Entity::discard);
        scene.idle(20);

        scene.world.showSection(util.select.layer(1), Direction.DOWN);
        scene.overlay.showText(70)
                .text("You now have functional chunk loaders")
                .attachKeyFrame()
                .pointAt(util.vector.blockSurface(center.west(), Direction.WEST))
                .placeNearTarget();
        scene.idle(80);
    }

}

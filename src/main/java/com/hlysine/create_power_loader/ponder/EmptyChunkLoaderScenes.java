package com.hlysine.create_power_loader.ponder;

import com.hlysine.create_power_loader.CPLBlocks;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.phys.Vec3;

import net.minecraft.world.entity.Entity;

public class EmptyChunkLoaderScenes {
    public static void usage(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("empty_chunk_loader_basic", "Usage of Empty Chunk Loaders");
        scene.scaleSceneView(5 / 11f);
        scene.configureBasePlate(0, 0, 11);
        scene.world().showSection(util.select().fromTo(0, 0, 0, 10, 0, 10), Direction.UP);
        scene.idle(10);

        BlockPos center = util.grid().at(5, 1, 5);

        scene.world().createEntity(w -> {
            Ghast ghastEntity = EntityType.GHAST.create(w);
            Vec3 v = util.vector().topOf(center);
            ghastEntity.setPosRaw(v.x, v.y, v.z);
            ghastEntity.setYRot(ghastEntity.yRotO = 180);
            return ghastEntity;
        });

        scene.idle(20);
        scene.overlay()
                .showControls(util.vector().centerOf(center.above(4)), Pointing.DOWN, 40).rightClick()
                .withItem(CPLBlocks.EMPTY_ANDESITE_CHUNK_LOADER.asStack());
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Right-click a Ghast with an empty chunk loader to capture it")
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(center.above(4), Direction.WEST))
                .placeNearTarget();
        scene.idle(40);
        scene.overlay()
                .showControls(util.vector().centerOf(center.above(4)), Pointing.DOWN, 40).rightClick()
                .withItem(CPLBlocks.EMPTY_BRASS_CHUNK_LOADER.asStack());
        scene.idle(50);

        scene.world().modifyEntities(Ghast.class, Entity::discard);
        scene.idle(20);

        BlockPos loader = util.grid().at(4, 2, 5);

        scene.world().showSection(util.select().fromTo(11, 0, 0, 11, 0, 11), Direction.UP);
        scene.world().showSection(util.select().layers(1, 2), Direction.DOWN);
        scene.world().modifyKineticSpeed(util.select().everywhere(), f -> f / 8f);
        scene.overlay().showText(70)
                .text("With rotational power, the captured ghasts light up the portal cores...")
                .attachKeyFrame()
                .pointAt(util.vector().blockSurface(loader.west(), Direction.WEST))
                .placeNearTarget();
        scene.idle(70);

        scene.world().modifyKineticSpeed(util.select().everywhere(), f -> f * 8f);

        scene.idle(10);

        scene.overlay().showText(60)
                .text("...bringing the chunk loaders to life")
                .pointAt(util.vector().blockSurface(loader.west(), Direction.WEST))
                .placeNearTarget();
        scene.idle(70);
    }

}

package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.content.chunkloader.BrassChunkLoaderBlock;
import com.hlysine.create_power_loader.content.chunkloader.BrassChunkLoaderMovementBehaviour;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class CPLBlocks {
    private static final CreateRegistrate REGISTRATE = CreatePowerLoader.getRegistrate();

    public static final BlockEntry<BrassChunkLoaderBlock> BRASS_CHUNK_LOADER = REGISTRATE.block("brass_chunk_loader", BrassChunkLoaderBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .isRedstoneConductor((state, getter, pos) -> false)
                    .noOcclusion()
                    .lightLevel((state) -> 6)
            )
            .blockstate(BlockStateGen.directionalBlockProvider(true))
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(movementBehaviour(new BrassChunkLoaderMovementBehaviour()))
            .transform(axeOrPickaxe())
            .transform(BlockStressDefaults.setImpact(4.0))
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {
    }
}

package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.content.andesitechunkloader.AndesiteChunkLoaderBlock;
import com.hlysine.create_power_loader.content.andesitechunkloader.AndesiteChunkLoaderMovementBehaviour;
import com.hlysine.create_power_loader.content.brasschunkloader.BrassChunkLoaderBlock;
import com.hlysine.create_power_loader.content.brasschunkloader.BrassChunkLoaderMovementBehaviour;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class CPLBlocks {
    private static final CreateRegistrate REGISTRATE = CreatePowerLoader.getRegistrate();

    public static final BlockEntry<AndesiteChunkLoaderBlock> ANDESITE_CHUNK_LOADER = REGISTRATE.block("andesite_chunk_loader", AndesiteChunkLoaderBlock::new)
            .initialProperties(() -> Blocks.BEACON)
            .properties(p -> p
                    .mapColor(MapColor.PODZOL)
                    .isRedstoneConductor((state, getter, pos) -> false)
                    .noOcclusion()
                    .lightLevel((state) -> 4)
            )
            .blockstate(BlockStateGen.directionalBlockProvider(true))
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(movementBehaviour(new AndesiteChunkLoaderMovementBehaviour()))
            .transform(BlockStressDefaults.setImpact(16.0))
            .item()
            .transform(customItemModel())
            .transform(axeOrPickaxe())
            .register();

    public static final BlockEntry<BrassChunkLoaderBlock> BRASS_CHUNK_LOADER = REGISTRATE.block("brass_chunk_loader", BrassChunkLoaderBlock::new)
            .initialProperties(() -> Blocks.BEACON)
            .properties(p -> p
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .isRedstoneConductor((state, getter, pos) -> false)
                    .noOcclusion()
                    .lightLevel((state) -> 6)
            )
            .blockstate(BlockStateGen.directionalBlockProvider(true))
            .addLayer(() -> RenderType::cutoutMipped)
            .onRegister(movementBehaviour(new BrassChunkLoaderMovementBehaviour()))
            .transform(BlockStressDefaults.setImpact(16.0))
            .item()
            .transform(customItemModel())
            .transform(axeOrPickaxe())
            .register();

    public static void register() {
    }
}

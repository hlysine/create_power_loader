package com.hlysine.create_power_loader;

import com.hlysine.create_power_loader.content.chunkloader.MechanicalChunkLoaderBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class CPLBlocks {
    private static final CreateRegistrate REGISTRATE = CreatePowerLoader.getRegistrate();

    public static final BlockEntry<MechanicalChunkLoaderBlock> MECHANICAL_CHUNK_LOADER = REGISTRATE.block("mechanical_chunk_loader", MechanicalChunkLoaderBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .isRedstoneConductor((state, getter, pos) -> false)
                    .noOcclusion()
                    .lightLevel((state) -> 6)
            )
            .blockstate(BlockStateGen.directionalBlockProvider(true))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .transform(BlockStressDefaults.setImpact(4.0))
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {
    }
}

package com.hlysine.create_power_loader;

import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory;

public class CPLRecipes {
    private static final ConversionRecipe ANDESITE_CHUNK_LOADER = ConversionRecipe.create(CPLBlocks.EMPTY_ANDESITE_CHUNK_LOADER.asStack(), CPLBlocks.ANDESITE_CHUNK_LOADER.asStack());
    private static final ConversionRecipe BRASS_CHUNK_LOADER = ConversionRecipe.create(CPLBlocks.EMPTY_BRASS_CHUNK_LOADER.asStack(), CPLBlocks.BRASS_CHUNK_LOADER.asStack());

    public static void register() {
        MysteriousItemConversionCategory.RECIPES.remove(ANDESITE_CHUNK_LOADER);
        MysteriousItemConversionCategory.RECIPES.remove(BRASS_CHUNK_LOADER);
        MysteriousItemConversionCategory.RECIPES.add(ANDESITE_CHUNK_LOADER);
        MysteriousItemConversionCategory.RECIPES.add(BRASS_CHUNK_LOADER);
    }
}

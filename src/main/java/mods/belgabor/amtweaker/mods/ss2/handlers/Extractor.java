package mods.belgabor.amtweaker.mods.ss2.handlers;

import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import shift.sextiarysector.api.recipe.RecipeAPI;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Created by Belgabor on 06.06.2016.
 */
@ZenClass("mods.ss2.Extractor")
public class Extractor extends FluidRecipe {

    @ZenMethod
    public static void add(ILiquidStack outputLiquid, IItemStack output, IIngredient input) {
        doAdd(RecipeAPI.extractor, "Extractor", outputLiquid, output, input);
    }

    @ZenMethod
    public static void remove(ILiquidStack outputLiquid, IItemStack output, IIngredient input) {
        doRemove(RecipeAPI.extractor, "Extractor", outputLiquid, output, input);
    }

    @ZenMethod
    public static void remove(ILiquidStack output) {
        doRemove(RecipeAPI.extractor, "Extractor", output, null, null);
    }
}

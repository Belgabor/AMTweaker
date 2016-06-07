package mods.belgabor.amtweaker.mods.ss2.handlers;

import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import shift.sextiarysector.SSRecipes;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Created by Belgabor on 06.06.2016.
 */
@ZenClass("mods.ss2.FoodSmokers")
public class FoodSmokers extends FluidRecipe {

    @ZenMethod
    public static void add(ILiquidStack outputLiquid, IItemStack output, IIngredient input) {
        doAdd(SSRecipes.foodSmokers, "Food Smokers", outputLiquid, output, input);
    }

    @ZenMethod
    public static void remove(ILiquidStack outputLiquid, IItemStack output, IIngredient input) {
        doRemove(SSRecipes.foodSmokers, "Food Smokers", outputLiquid, output, input);
    }

    @ZenMethod
    public static void remove(ILiquidStack output) {
        doRemove(SSRecipes.foodSmokers, "Food Smokers", output, null, null);
    }
}

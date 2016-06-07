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
@ZenClass("mods.ss2.FluidFurnace")
public class FluidFurnace extends FluidRecipe {

    @ZenMethod
    public static void add(ILiquidStack outputLiquid, IItemStack output, IIngredient input) {
        doAdd(SSRecipes.fluidFurnace, "Fluid Furnace", outputLiquid, output, input);
    }

    @ZenMethod
    public static void remove(ILiquidStack outputLiquid, IItemStack output, IIngredient input) {
        doRemove(SSRecipes.fluidFurnace, "Fluid Furnace", outputLiquid, output, input);
    }

    @ZenMethod
    public static void remove(ILiquidStack output) {
        doRemove(SSRecipes.fluidFurnace, "Fluid Furnace", output, null, null);
    }
}

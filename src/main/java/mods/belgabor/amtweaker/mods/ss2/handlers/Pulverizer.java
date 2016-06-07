package mods.belgabor.amtweaker.mods.ss2.handlers;

import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import shift.sextiarysector.api.recipe.RecipeAPI;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Created by Belgabor on 04.06.2016.
 */
@ZenClass("mods.ss2.Pulverizer")
public class Pulverizer extends NormalRecipe {
    
    @ZenMethod
    public static void add(IItemStack output, IIngredient input) {
        doAdd(RecipeAPI.pulverizer, "Pulverizer", output, input);
    }
    
    @ZenMethod
    public static void remove(IItemStack output, IIngredient input) {
        doRemove(RecipeAPI.pulverizer, "Pulverizer", output, input);
    }
    
    @ZenMethod
    public static void remove(IItemStack output) {
        doRemove(RecipeAPI.pulverizer, "Pulverizer", output, null);
    }
}

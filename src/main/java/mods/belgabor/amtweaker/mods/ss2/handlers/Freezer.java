package mods.belgabor.amtweaker.mods.ss2.handlers;

import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import shift.sextiarysector.SSRecipes;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Created by Belgabor on 04.06.2016.
 */
@ZenClass("mods.ss2.Freezer")
public class Freezer extends NormalRecipe {
    
    @ZenMethod
    public static void add(IItemStack output, IIngredient input) {
        doAdd(SSRecipes.freezer, "Freezer", output, input);
    }
    
    @ZenMethod
    public static void remove(IItemStack output, IIngredient input) {
        doRemove(SSRecipes.freezer, "Freezer", output, input);
    }
    
    @ZenMethod
    public static void remove(IItemStack output) {
        doRemove(SSRecipes.freezer, "Freezer", output, null);
    }
}

package mods.belgabor.amtweaker.mods.ss2.handlers;

import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import shift.sextiarysector.api.recipe.RecipeAPI;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Created by Belgabor on 04.06.2016.
 */
@ZenClass("mods.ss2.RollingMachine")
public class RollingMachine extends NormalRecipe {
    
    @ZenMethod
    public static void add(IItemStack output, IIngredient input) {
        doAdd(RecipeAPI.rollingMachine, "Rolling Machine", output, input);
    }
    
    @ZenMethod
    public static void remove(IItemStack output, IIngredient input) {
        doRemove(RecipeAPI.rollingMachine, "Rolling Machine", output, input);
    }
    
    @ZenMethod
    public static void remove(IItemStack output) {
        doRemove(RecipeAPI.rollingMachine, "Rolling Machine", output, null);
    }
}

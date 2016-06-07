package mods.belgabor.amtweaker.mods.ss2.handlers;

import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import shift.sextiarysector.SSRecipes;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Created by Belgabor on 04.06.2016.
 */
@ZenClass("mods.ss2.MagicFurnace")
public class MagicFurnace extends NormalRecipe {
    
    @ZenMethod
    public static void add(IItemStack output, IIngredient input) {
        doAdd(SSRecipes.magicFurnace, "Magic Furnace", output, input);
    }
    
    @ZenMethod
    public static void remove(IItemStack output, IIngredient input) {
        doRemove(SSRecipes.magicFurnace, "Magic Furnace", output, input);
    }
    
    @ZenMethod
    public static void remove(IItemStack output) {
        doRemove(SSRecipes.magicFurnace, "Magic Furnace", output, null);
    }
}

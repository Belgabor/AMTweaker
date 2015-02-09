package mods.belgabor.amtweaker.mods.amt.util;

import minetweaker.MineTweakerAPI;
import mods.belgabor.amtweaker.util.BaseListAddition;

import java.util.List;

/**
 * Created by Belgabor on 08.02.2015.
 */
public class AMTListAddition extends BaseListAddition {
    private Object amt_recipe = null;

    public AMTListAddition(String description, List list, AMTRecipeWrapper recipe) {
        super(description, list, recipe);
    }

    @Override
    public void apply() {
        if (amt_recipe == null) {
            AMTRecipeWrapper my_recipe = (AMTRecipeWrapper) recipe;
            my_recipe.register();

            for (Object r : list) {
                if (my_recipe.matches(r)) {
                    amt_recipe = r;
                    break;
                }
            }
            if (amt_recipe == null) {
                MineTweakerAPI.getLogger().logError("Failed to locate AMT recipe for " + description);
            }
        } else {
            list.add(amt_recipe);
        }
    }

    @Override
    public void undo() {
        if (amt_recipe == null) {
            MineTweakerAPI.getLogger().logError("Failed to undo AMT recipe for " + description);
        } else {
            list.remove(amt_recipe);
        }
    }

    @Override
    public String getRecipeInfo() {
        return ((AMTRecipeWrapper) recipe).getRecipeInfo();
    }

}

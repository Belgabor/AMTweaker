package mods.belgabor.amtweaker.mods.amt.handlers;


import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import mods.belgabor.amtweaker.mods.amt.util.AMTListAddition;
import mods.belgabor.amtweaker.mods.amt.util.AMTRecipeWrapper;
import mods.belgabor.amtweaker.mods.amt.util.BlockAddition;
import mods.belgabor.amtweaker.util.BaseListRemoval;
import mods.defeatedcrow.api.recipe.IPlateRecipe;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.isABlock;
import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;

@ZenClass("mods.amt.Plate")
public class Plate {
    // Adding a new cooking recipe for the iron plate
    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack input, int cookingTime, boolean isOvenRecipe) {
        MineTweakerAPI.apply(new Add(new PlateRecipeWrapper(output, input, cookingTime, isOvenRecipe)));
    }

    private static class PlateRecipeWrapper extends AMTRecipeWrapper {
        private ItemStack output;
        private ItemStack input;
        private int cookingTime;
        private boolean isOvenRecipe;

        public PlateRecipeWrapper(IItemStack output, IItemStack input, int cookingTime, boolean isOvenRecipe)  {
            this.output = toStack(output, true);
            this.input = toStack(input, true);
            this.cookingTime = cookingTime;
            this.isOvenRecipe = isOvenRecipe;
        }

        @Override
        public void register() {
            RecipeRegisterManager.plateRecipe.register(input, output, cookingTime, isOvenRecipe);
        }

        @Override
        public String getRecipeInfo() {
            return output.getDisplayName();
        }

        @Override
        public boolean matches(Object o) {
            IPlateRecipe r = (IPlateRecipe) o;
            return (r.getInput() != null && areEqual(r.getInput(), input)) && (r.getOutput() != null && areEqual(r.getOutput(), output)) && (r.useOvenRecipe() == isOvenRecipe);
        }
    }

    //Passes the list to the base list implementation, and adds the recipe
    private static class Add extends AMTListAddition {

        public Add(PlateRecipeWrapper recipe) {
            super("Cooking Iron Plate", RecipeRegisterManager.plateRecipe.getRecipeList(), recipe);
        }

    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Removing a Cooking Iron Plate recipe
    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        MineTweakerAPI.apply(new Remove(toStack(output)));
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class Remove extends BaseListRemoval {
        public Remove(ItemStack stack) {
            super("Cooking Iron Plate", RecipeRegisterManager.plateRecipe.getRecipeList(), stack);
        }

        //Loops through the registry, to find the item that matches, saves that recipe then removes it
        @Override
        public void apply() {
            for (IPlateRecipe r : RecipeRegisterManager.plateRecipe.getRecipeList()) {
                if (r.getOutput() != null && areEqual(r.getOutput(), stack)) {
                    recipe = r;
                    break;
                }
            }

            if (recipe == null) {
                MineTweakerAPI.getLogger().logWarning("No Cooking Iron Plate recipe for " + getRecipeInfo() + " found.");
            } else {
                list.remove(recipe);
            }
        }

        @Override
        public String getRecipeInfo() {
            return stack.getDisplayName();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Add a heat source
    @ZenMethod
    public static void registerHeatSource(IItemStack block) {
        if (isABlock(toStack(block))) {
            MineTweakerAPI.apply(new PlateBlockAddition(block));
        } else {
            MineTweakerAPI.getLogger().logError("Heat source for Cooking Iron Plate must be a block: " + toStack(block).getDisplayName());
        }
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class PlateBlockAddition extends BlockAddition {
        public PlateBlockAddition(IItemStack block) {
            super("Cooking Iron Plate Heat Source", block);
        }

        //Loops through the registry, to find the item that matches, saves that recipe then removes it
        @Override
        public void apply() {
            RecipeRegisterManager.plateRecipe.registerHeatSource(block, meta);
        }

    }

}


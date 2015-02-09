package mods.belgabor.amtweaker.mods.amt.handlers;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import mods.belgabor.amtweaker.mods.amt.util.AMTListAddition;
import mods.belgabor.amtweaker.mods.amt.util.AMTRecipeWrapper;
import mods.belgabor.amtweaker.util.BaseListRemoval;
import mods.defeatedcrow.api.recipe.IEvaporatorRecipe;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.toFluid;
import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqualNull;

@ZenClass("mods.amt.Evaporator")
public class Evaporator {
    // Adding a new cooking recipe for the iron plate
    @ZenMethod
    public static void addRecipe(IItemStack output, ILiquidStack secondary, IItemStack input, boolean returnContainer) {
        MineTweakerAPI.apply(new Add(new EvaporatorRecipeWrapper(output, secondary, input, returnContainer)));
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, ILiquidStack secondary, IItemStack input) {
        MineTweakerAPI.apply(new Add(new EvaporatorRecipeWrapper(output, secondary, input, true)));
    }

    private static class EvaporatorRecipeWrapper extends AMTRecipeWrapper {
        private ItemStack output;
        private FluidStack secondary;
        private ItemStack input;
        private boolean returnContainer;

        public EvaporatorRecipeWrapper(IItemStack output, ILiquidStack secondary, IItemStack input, boolean returnContainer) {
            this.output = toStack(output);
            this.secondary = toFluid(secondary);
            this.input = toStack(input);
            this.returnContainer = returnContainer;
        }

        @Override
        public void register() {
            RecipeRegisterManager.evaporatorRecipe.addRecipe(output, secondary, input, returnContainer);
        }

        @Override
        public boolean matches(Object o) {
            IEvaporatorRecipe r = (IEvaporatorRecipe) o;
            return (r.returnContainer() == returnContainer) &&
                    (areEqualNull(r.getInput(), input)) &&
                    (areEqualNull(r.getOutput(), output)) &&
                    (areEqualNull(r.getSecondary(), secondary));
        }

        @Override
        public String getRecipeInfo() {
            if (secondary == null) {
                return this.output.getDisplayName();
            } else {
                return this.output.getDisplayName() + " + " + this.secondary.getLocalizedName();
            }
        }
    }

    //Passes the list to the base list implementation, and adds the recipe
    private static class Add extends AMTListAddition {

        public Add(EvaporatorRecipeWrapper recipe) {
            super("Processor", RecipeRegisterManager.evaporatorRecipe.getRecipeList(), recipe);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Removing a Processor recipe
    @ZenMethod
    public static void removeRecipe(IItemStack input) {
        MineTweakerAPI.apply(new Remove(toStack(input)));
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class Remove extends BaseListRemoval {
        public Remove(ItemStack stack) {
            super("Evaporator", RecipeRegisterManager.evaporatorRecipe.getRecipeList(), stack);
        }

        //Loops through the registry, to find the item that matches, saves that recipe then removes it
        @Override
        public void apply() {
            for (IEvaporatorRecipe r : RecipeRegisterManager.evaporatorRecipe.getRecipeList()) {
                if (r.getInput() != null && areEqual(r.getInput(), stack)) {
                    recipe = r;
                    break;
                }
            }

            if (recipe == null) {
                MineTweakerAPI.getLogger().logWarning("No evaporator recipe for " + getRecipeInfo() + " found.");
            } else {
                list.remove(recipe);
            }
        }

        @Override
        public String getRecipeInfo() {
            return stack.getDisplayName();
        }
    }


}

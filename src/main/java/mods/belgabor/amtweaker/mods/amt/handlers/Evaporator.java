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
        doAddRecipe(output, secondary, input, returnContainer);
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, ILiquidStack secondary, IItemStack input) {
        doAddRecipe(output, secondary, input, true);
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack input, boolean returnContainer) {
        doAddRecipe(output, null, input, returnContainer);
    }

    @ZenMethod
    public static void addRecipe(ILiquidStack secondary, IItemStack input, boolean returnContainer) {
        doAddRecipe(null, secondary, input, returnContainer);
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack input) {
        doAddRecipe(output, null, input, true);
    }

    @ZenMethod
    public static void addRecipe(ILiquidStack secondary, IItemStack input) {
        doAddRecipe(null, secondary, input, true);
    }

    private static void doAddRecipe(IItemStack output, ILiquidStack secondary, IItemStack input, boolean returnContainer) {
        if (input == null) {
            MineTweakerAPI.getLogger().logError("Evaporator: Input item must not be null!");
            return;
        }
        if ((output == null) && (secondary == null)) {
            MineTweakerAPI.getLogger().logError("Evaporator: Primary and secondary output must not both be null!");
            return;
        }
        MineTweakerAPI.apply(new Add(new EvaporatorRecipeWrapper(output, secondary, input, returnContainer)));
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
            String s = "";
            if (output != null) {
                s += output.getDisplayName();
            }
            if (secondary != null) {
                if (output != null) {
                    s += " + ";
                }
                s += this.secondary.getLocalizedName();
            }
            return s;
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

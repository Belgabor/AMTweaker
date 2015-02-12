package mods.belgabor.amtweaker.mods.amt.handlers;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import mods.belgabor.amtweaker.mods.amt.util.AMTListAddition;
import mods.belgabor.amtweaker.mods.amt.util.AMTRecipeWrapper;
import mods.belgabor.amtweaker.util.BaseListRemoval;
import mods.defeatedcrow.api.recipe.IProcessorRecipe;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;

import static mods.belgabor.amtweaker.helpers.InputHelper.toObjects;
import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqualNull;

@ZenClass("mods.amt.Processor")
public class Processor {
    // Adding a new processor recipe
    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack secondary, IIngredient[] inputs, boolean isFoodRecipe, float secondaryChance) {
        MineTweakerAPI.apply(new Add(new ProcessorRecipeWrapper(output, secondary, inputs, isFoodRecipe, secondaryChance)));
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack secondary, IIngredient[] inputs, boolean isFoodRecipe) {
        MineTweakerAPI.apply(new Add(new ProcessorRecipeWrapper(output, secondary, inputs, isFoodRecipe, null)));
    }

    private static class ProcessorRecipeWrapper extends AMTRecipeWrapper {
        private ItemStack output;
        private ItemStack secondary;
        private Object[] inputs;
        private boolean isFoodRecipe;
        private Float secondaryChance;

        public ProcessorRecipeWrapper(IItemStack output, IItemStack secondary, IIngredient[] inputs, boolean isFoodRecipe, Float secondaryChance) {
            this.output = toStack(output);
            this.secondary = toStack(secondary);
            this.inputs = toObjects(inputs, true);
            this.isFoodRecipe = isFoodRecipe;
            this.secondaryChance = secondaryChance;
        }

        @Override
        public void register() {
            if (secondaryChance != null) {
                RecipeRegisterManager.processorRecipe.addRecipe(output, isFoodRecipe, secondary, secondaryChance.floatValue(), inputs);
            } else {
                RecipeRegisterManager.processorRecipe.addRecipe(output, isFoodRecipe, secondary, inputs);
            }
        }

        @Override
        public boolean matches(Object o) {
            IProcessorRecipe r = (IProcessorRecipe) o;
            return (r.isFoodRecipe() == isFoodRecipe) &&
                   (r.getInput() != null && Arrays.deepEquals(r.getInput(), inputs)) &&
                   (areEqualNull(r.getOutput(), output)) &&
                   (areEqualNull(r.getSecondary(), secondary));
        }

        @Override
        public String getRecipeInfo() {
            if (secondary == null) {
                return this.output.getDisplayName();
            } else {
                return this.output.getDisplayName() + " + " + this.secondary.getDisplayName();
            }
        }
    }

    //Passes the list to the base list implementation, and adds the recipe
    private static class Add extends AMTListAddition {

        public Add(ProcessorRecipeWrapper recipe) {
            super("Processor", RecipeRegisterManager.processorRecipe.getRecipes(), recipe);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Removing a Processor recipe
    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        MineTweakerAPI.apply(new Remove(toStack(output)));
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class Remove extends BaseListRemoval {
        public Remove(ItemStack stack) {
            super("Processor", RecipeRegisterManager.processorRecipe.getRecipes(), stack);
        }

        //Loops through the registry, to find the item that matches, saves that recipe then removes it
        @Override
        public void apply() {
            for (IProcessorRecipe r : RecipeRegisterManager.processorRecipe.getRecipes()) {
                if (r.getOutput() != null && areEqual(r.getOutput(), stack)) {
                    recipe = r;
                    break;
                }
            }

            if (recipe == null) {
                MineTweakerAPI.getLogger().logWarning("No processor recipe for " + getRecipeInfo() + " found.");
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

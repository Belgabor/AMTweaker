package mods.belgabor.amtweaker.mods.amt.handlers;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import mods.belgabor.amtweaker.mods.amt.util.AMTListAddition;
import mods.belgabor.amtweaker.mods.amt.util.AMTRecipeWrapper;
import mods.belgabor.amtweaker.util.BaseListRemoval;
import mods.belgabor.amtweaker.util.BaseListWildcardRemoval;
import mods.defeatedcrow.api.recipe.IProcessorRecipe;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
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
    public static void addRecipe(IItemStack output, IItemStack secondary, IIngredient[] inputs, boolean isFoodRecipe, float secondaryChance, boolean forceReturnContainer) {
        doAddRecipe(output, secondary, inputs, isFoodRecipe, secondaryChance, forceReturnContainer);
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack secondary, IIngredient[] inputs, boolean isFoodRecipe, float secondaryChance) {
        doAddRecipe(output, secondary, inputs, isFoodRecipe, secondaryChance, false);
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack secondary, IIngredient[] inputs, boolean isFoodRecipe) {
        doAddRecipe(output, secondary, inputs, isFoodRecipe, null, false);
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IIngredient[] inputs, boolean isFoodRecipe) {
        doAddRecipe(output, null, inputs, isFoodRecipe, null, false);
    }

    private static void doAddRecipe(IItemStack output, IItemStack secondary, IIngredient[] inputs, boolean isFoodRecipe, Float secondaryChance, boolean forceReturnContainer) {
        if (inputs == null) {
            MineTweakerAPI.getLogger().logError("Processor: Input set must not be null!");
            return;
        }
        if (inputs.length == 0) {
            MineTweakerAPI.getLogger().logError("Processor: Input set must not empty!");
            return;
        }
        if ((output == null) && (secondary == null)) {
            MineTweakerAPI.getLogger().logError("Processor: Primary and secondary output must not both be null!");
            return;
        }
        MineTweakerAPI.apply(new Add(new ProcessorRecipeWrapper(output, secondary, inputs, isFoodRecipe, secondaryChance, forceReturnContainer)));
    }

    private static class ProcessorRecipeWrapper extends AMTRecipeWrapper {
        private ItemStack output;
        private ItemStack secondary;
        private Object[] inputs;
        private boolean isFoodRecipe;
        private Float secondaryChance;
        private boolean forceReturnContainer;

        public ProcessorRecipeWrapper(IItemStack output, IItemStack secondary, IIngredient[] inputs, boolean isFoodRecipe, Float secondaryChance, boolean forceReturnContainer) {
            this.output = toStack(output);
            this.secondary = toStack(secondary);
            this.inputs = toObjects(inputs, true);
            this.isFoodRecipe = isFoodRecipe;
            this.secondaryChance = secondaryChance;
            this.forceReturnContainer = forceReturnContainer;
        }

        @Override
        public void register() {
            RecipeRegisterManager.processorRecipe.addRecipe(output, isFoodRecipe, forceReturnContainer, secondary, (secondaryChance == null)?0:secondaryChance.floatValue(), inputs);
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
    private static class Remove extends BaseListWildcardRemoval {
        public Remove(ItemStack stack) {
            super("Processor", RecipeRegisterManager.processorRecipe.getRecipes(), stack);
        }

        //Loops through the registry, to find the item that matches, saves that recipe then removes it
        @Override
        public void apply() {
            recipes.clear();
            for (IProcessorRecipe r : RecipeRegisterManager.processorRecipe.getRecipes()) {
                if (r.getOutput() != null) {
                    if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                        if (stack.getItem() == r.getOutput().getItem()) {
                            recipes.add(r);
                        }
                    } else if (areEqual(r.getOutput(), stack)) {
                        recipes.add(r);
                    }
                }
            }

            if (recipes.size() == 0) {
                MineTweakerAPI.getLogger().logWarning("No processor recipe for " + getRecipeInfo() + " found.");
            } else {
                for(Object o: recipes) {
                    list.remove(o);
                }
            }
        }

        @Override
        public String getRecipeInfo() {
            return stack.getDisplayName();
        }
    }


}

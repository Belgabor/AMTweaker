/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Belgabor
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mods.belgabor.amtweaker.mods.amt.handlers;

/**
 * Created by Belgabor on 09.02.2015.
 */

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import mods.belgabor.amtweaker.mods.amt.util.AMTListAddition;
import mods.belgabor.amtweaker.mods.amt.util.AMTRecipeWrapper;
import mods.belgabor.amtweaker.mods.amt.util.BlockAddition;
import mods.belgabor.amtweaker.util.BaseListRemoval;
import mods.defeatedcrow.api.recipe.IPanRecipe;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.isABlock;
import static mods.belgabor.amtweaker.helpers.InputHelper.toObject;
import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqualNull;

@ZenClass("mods.amt.Pan")
public class Pan {
    // Adding a new cooking recipe for the clay pan
    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack jbowl_output, IItemStack input, String texture, String display) {
        doAddRecipe(output, jbowl_output, input, texture, display);
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack input, String texture, String display) {
        doAddRecipe(output, null, input, texture, display);
    }

    private static void doAddRecipe(IItemStack output, IItemStack jbowl_output, IItemStack input, String texture, String display) {
        if ((output == null) || (input == null)) {
            MineTweakerAPI.getLogger().logError("Clay Pan: Neither input nor output may be null!");
            return;
        }
        MineTweakerAPI.apply(new Add(new PanRecipeWrapper(output, jbowl_output, input, texture, display)));
    }

    private static class PanRecipeWrapper extends AMTRecipeWrapper {
        private ItemStack output;
        private ItemStack jbowl_output;
        private ItemStack input;
        private String texture;
        private String display;

        public PanRecipeWrapper(IItemStack output, IItemStack jbowl_output, IItemStack input, String texture, String display)  {
            this.output = toStack(output, true);
            this.jbowl_output = toStack(jbowl_output, true);
            this.input = toStack(input, true);
            this.texture = texture;
            this.display = display;
        }

        @Override
        public void register() {
            RecipeRegisterManager.panRecipe.register(input, output, jbowl_output, texture, display);
        }

        @Override
        public String getRecipeInfo() {
            return output.getDisplayName();
        }

        @Override
        public boolean matches(Object o) {
            IPanRecipe r = (IPanRecipe) o;
            return (areEqualNull(r.getOutput(), output)) &&
                    (areEqualNull(r.getOutputJP(), jbowl_output)) &&
                    (areEqualNull(r.getInput(), input));
        }
    }

    //Passes the list to the base list implementation, and adds the recipe
    private static class Add extends AMTListAddition {

        public Add(PanRecipeWrapper recipe) {
            super("Clay Pan", RecipeRegisterManager.panRecipe.getRecipeList(), recipe);
        }

    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Removing a clay pan recipe
    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        MineTweakerAPI.apply(new Remove(toStack(output)));
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class Remove extends BaseListRemoval {
        public Remove(ItemStack stack) {
            super("Clay Pan", RecipeRegisterManager.panRecipe.getRecipeList(), stack);
        }

        //Loops through the registry, to find the item that matches, saves that recipe then removes it
        @Override
        public void apply() {
            for (IPanRecipe r : RecipeRegisterManager.panRecipe.getRecipeList()) {
                if (r.getOutput() != null && areEqual(r.getOutput(), stack)) {
                    recipe = r;
                    break;
                }
            }

            if (recipe == null) {
                MineTweakerAPI.getLogger().logWarning("No Clay Pan recipe for " + getRecipeInfo() + " found.");
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
        if (block == null) {
            MineTweakerAPI.getLogger().logError("Clay Pan: Heat source block must not be null!");
            return;
        }
        if (!isABlock(toStack(block))) {
            MineTweakerAPI.getLogger().logError("Heat source for Clay Pan must be a block: " + toStack(block).getDisplayName());
            return;
        }
        MineTweakerAPI.apply(new PanBlockAddition(block));
    }

    private static class PanBlockAddition extends BlockAddition {
        public PanBlockAddition(IItemStack block) {
            super("Clay Pan Heat Source", RecipeRegisterManager.panRecipe.getHeatSourceList(), block);
        }

        @Override
        public void apply() {
            RecipeRegisterManager.panRecipe.registerHeatSource(block, getMeta());
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static Object getChocolateKey(Object input) {
        if (input == null) {
            return null;
        }

        for (Object key : RecipeRegisterManager.chocoRecipe.getRecipeList().keySet()) {
            if ((key instanceof String) && (input instanceof String)) {
                if ((String) key == (String) input) {
                    return key;
                }
            }
            else if ((key instanceof ItemStack) && (input instanceof ItemStack)) {
                if (areEqualNull((ItemStack) key, (ItemStack) input))
                {
                    return key;
                }
            }

        }
        return null;
    }

    // Adding a new cooking recipe for the clay pan
    @ZenMethod
    public static void addChocolateRecipe(IItemStack output, IIngredient input) {
        Object cinput = toObject(input, true);
        if ((output == null) || (cinput == null)) {
            MineTweakerAPI.getLogger().logError("Chocolate Recipe: Neither input nor output may be null!");
            return;
        }
        if (RecipeRegisterManager.chocoRecipe.getRecipeList().containsKey(getChocolateKey(cinput))) {
            MineTweakerAPI.getLogger().logError("Chocolate Recipe: Input item " + input.toString() + " already has a recipe");
            return;
        }
        MineTweakerAPI.apply(new ChocolateAdd(output, cinput));
    }

    private static class ChocolateAdd implements IUndoableAction {
        private ItemStack output;
        private Object input;
        private Boolean applied;

        public ChocolateAdd(IItemStack output, Object input)  {
            this.output = toStack(output, true);
            this.input = input;
            this.applied = false;
        }

        @Override
        public void apply() {
            if (!applied) {
                if (input instanceof String) {
                    RecipeRegisterManager.chocoRecipe.register((String) input, output);
                } else {
                    RecipeRegisterManager.chocoRecipe.register((ItemStack) input, output);
                }
                applied = true;
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            if (applied) {
                RecipeRegisterManager.chocoRecipe.getRecipeList().remove(input);
                applied = false;
            }
        }

        @Override
        public String describe() {
            return "Adding Chocolate Recipe for " + output.getDisplayName();
        }

        @Override
        public String describeUndo() {
            return "Removing Chocolate Recipe for " + output.getDisplayName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Removing a clay pan recipe
    @ZenMethod
    public static void removeChocolateRecipe(IIngredient input) {
        Object cinput = toObject(input, true);
        if (cinput == null) {
            MineTweakerAPI.getLogger().logError("Chocolate Recipe removal: Input may not be null!");
            return;
        }
        if (!RecipeRegisterManager.chocoRecipe.getRecipeList().containsKey(getChocolateKey(cinput))) {
            MineTweakerAPI.getLogger().logError("Chocolate Recipe removal: Input item " + input.toString() + " doesn't have a recipe");
            return;
        }
        MineTweakerAPI.apply(new ChocolateRemove(cinput));
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class ChocolateRemove implements IUndoableAction {
        private ItemStack output = null;
        private Object input;
        private Boolean applied = false;

        public ChocolateRemove(Object input) {
            this.input = input;
        }

        @Override
        public void apply() {
            if (!applied) {
                Object tinput = getChocolateKey(input);
                output = RecipeRegisterManager.chocoRecipe.getRecipeList().get(tinput);
                if (output == null) {
                    MineTweakerAPI.getLogger().logError("Chocolate Recipe removal: Couldn't apply recipe removal (input item unexpectedly has no recipe)");
                    return;
                }
                RecipeRegisterManager.chocoRecipe.getRecipeList().remove(tinput);
                applied = true;
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            if (applied) {
                if (input instanceof String) {
                    RecipeRegisterManager.chocoRecipe.register((String) input, output);
                } else {
                    RecipeRegisterManager.chocoRecipe.register((ItemStack) input, output);
                }
                applied = false;
            }
        }

        @Override
        public String describeUndo() {
            if (input instanceof String) {
                return "Restoring Chocolate Recipe for <ore:" + (String) input + ">";
            } else {
                return "Restoring Chocolate Recipe for " + ((ItemStack) input).getDisplayName();
            }
        }

        @Override
        public String describe() {
            if (input instanceof String) {
                return "Removing Chocolate Recipe for <ore:" + (String) input + ">";
            } else {
                return "Removing Chocolate Recipe for " + ((ItemStack) input).getDisplayName();
            }
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }

    }
}


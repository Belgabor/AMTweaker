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

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import mods.belgabor.amtweaker.mods.amt.util.AMTListAddition;
import mods.belgabor.amtweaker.mods.amt.util.AMTRecipeWrapper;
import mods.belgabor.amtweaker.mods.amt.util.BlockAddition;
import mods.belgabor.amtweaker.util.BaseListRemoval;
import mods.defeatedcrow.api.recipe.IPanRecipe;
import mods.defeatedcrow.api.recipe.IPlateRecipe;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.isABlock;
import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqualNull;

@ZenClass("mods.amt.Pan")
public class Pan {
    // Adding a new cooking recipe for the iron plate
    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack jbowl_output, IItemStack input, String texture, String display) {
        MineTweakerAPI.apply(new Add(new PanRecipeWrapper(output, jbowl_output, input, texture, display)));
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack input, String texture, String display) {
        MineTweakerAPI.apply(new Add(new PanRecipeWrapper(output, null, input, texture, display)));
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

    // Removing a Cooking Iron Plate recipe
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
        if (isABlock(toStack(block))) {
            MineTweakerAPI.apply(new PanBlockAddition(block));
        } else {
            MineTweakerAPI.getLogger().logError("Heat source for Clay Pan must be a block: " + toStack(block).getDisplayName());
        }
    }

    private static class PanBlockAddition extends BlockAddition {
        public PanBlockAddition(IItemStack block) {
            super("Clay Pan Heat Source", block);
        }

        @Override
        public void apply() {
            RecipeRegisterManager.panRecipe.registerHeatSource(block, meta);
        }

    }

}


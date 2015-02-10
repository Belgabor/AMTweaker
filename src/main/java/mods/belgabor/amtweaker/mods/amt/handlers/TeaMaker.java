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

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import mods.belgabor.amtweaker.mods.amt.util.AMTListAddition;
import mods.belgabor.amtweaker.mods.amt.util.AMTRecipeWrapper;
import mods.belgabor.amtweaker.util.BaseListRemoval;
import mods.defeatedcrow.api.recipe.ITeaRecipe;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqualNull;

/**
 * Created by Belgabor on 10.02.2015.
 */
@ZenClass("mods.amt.TeaMaker")
public class TeaMaker {
    // Adding a new tea maker recipe
    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack milk_output, IItemStack input, String texture, String milk_texture) {
        MineTweakerAPI.apply(new Add(new TeaRecipeWrapper(output, milk_output, input, texture, milk_texture)));
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack milk_output, IItemStack input, String texture) {
        MineTweakerAPI.apply(new Add(new TeaRecipeWrapper(output, milk_output, input, texture, texture)));
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack input, String texture) {
        MineTweakerAPI.apply(new Add(new TeaRecipeWrapper(output, null, input, texture, texture)));
    }

    private static class TeaRecipeWrapper extends AMTRecipeWrapper {
        private ItemStack output;
        private ItemStack milk_output;
        private ItemStack input;
        private String texture;
        private String milk_texture;

        public TeaRecipeWrapper(IItemStack output, IItemStack milk_output, IItemStack input, String texture, String milk_texture) {
            this.output = toStack(output);
            this.milk_output = toStack(milk_output);
            this.input = toStack(input);
            this.texture = texture;
            this.milk_texture = milk_texture;
        }

        @Override
        public void register() {
            RecipeRegisterManager.teaRecipe.registerCanMilk(input, output, milk_output, texture, milk_texture);
        }

        @Override
        public boolean matches(Object o) {
            ITeaRecipe r = (ITeaRecipe) o;
            return (areEqualNull(r.getOutput(), output)) &&
                    (areEqualNull(r.getOutputMilk(), milk_output)) &&
                    (r.getTex() == texture) &&
                    (r.getMilkTex() == milk_texture) &&
                    (areEqualNull(r.getInput(), input));
        }

        @Override
        public String getRecipeInfo() {
            return this.output.getDisplayName();
        }
    }

    //Passes the list to the base list implementation, and adds the recipe
    private static class Add extends AMTListAddition {

        public Add(TeaRecipeWrapper recipe) {
            super("Tea Maker", RecipeRegisterManager.teaRecipe.getRecipeList(), recipe);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Removing a tea maker recipe
    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        MineTweakerAPI.apply(new Remove(toStack(output)));
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class Remove extends BaseListRemoval {
        public Remove(ItemStack stack) {
            super("Tea Makerr", RecipeRegisterManager.teaRecipe.getRecipeList(), stack);
        }

        //Loops through the registry, to find the item that matches, saves that recipe then removes it
        @Override
        public void apply() {
            for (ITeaRecipe r : RecipeRegisterManager.teaRecipe.getRecipeList()) {
                if (r.getOutput() != null && areEqual(r.getOutput(), stack)) {
                    recipe = r;
                    break;
                }
            }

            if (recipe == null) {
                MineTweakerAPI.getLogger().logWarning("No tea maker recipe for " + getRecipeInfo() + " found.");
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

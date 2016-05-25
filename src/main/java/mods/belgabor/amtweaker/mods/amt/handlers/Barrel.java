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

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.liquid.ILiquidStack;
import mods.defeatedcrow.recipe.BrewingRecipe;
import net.minecraftforge.fluids.Fluid;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.toFluid;

/**
 * Created by Belgabor on 13.05.2015.
 */
@ZenClass("mods.amt.Barrel")
public class Barrel {
    // Adding a new barrel brewing recipe
    @ZenMethod
    public static void addRecipe(ILiquidStack output, ILiquidStack input) {
        if ((output == null) || (input == null)) {
            MineTweakerAPI.getLogger().logError("Barrel Recipe: Neither input nor output may be null!");
            return;
        }
        Fluid cinput = toFluid(input).getFluid();
        Fluid coutput = toFluid(output).getFluid();
        if (BrewingRecipe.instance.recipeMap().containsKey(cinput)) {
            MineTweakerAPI.getLogger().logError("Barrel Recipe: Input fluid " + input.toString() + " already has a recipe");
            return;
        }
        MineTweakerAPI.apply(new BarrelAdd(coutput, cinput));
    }

    private static class BarrelAdd implements IUndoableAction {
        private final Fluid output;
        private final Fluid input;
        private Boolean applied;

        public BarrelAdd(Fluid output, Fluid input)  {
            this.output = output;
            this.input = input;
            this.applied = false;
        }

        @Override
        public void apply() {
            if (!applied) {
                BrewingRecipe.instance.registerRecipe(input, output);
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
                BrewingRecipe.instance.recipeMap().remove(input);
                applied = false;
            }
        }

        @Override
        public String describe() {
            return "Adding barrel brewing recipe for " + output.getName();
        }

        @Override
        public String describeUndo() {
            return "Removing barrel brewing recipe for " + output.getName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Removing a clay pan recipe
    @ZenMethod
    public static void removeRecipe(ILiquidStack input) {
        Fluid cinput = toFluid(input).getFluid();
        if (cinput == null) {
            MineTweakerAPI.getLogger().logError("Barrel recipe removal: Input may not be null!");
            return;
        }
        if (!BrewingRecipe.instance.recipeMap().containsKey(cinput)) {
            MineTweakerAPI.getLogger().logError("Barrel recipe removal: Input item " + input.toString() + " doesn't have a recipe");
            return;
        }
        MineTweakerAPI.apply(new BarrelRemove(cinput));
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class BarrelRemove implements IUndoableAction {
        private Fluid output = null;
        private final Fluid input;
        private Boolean applied = false;

        public BarrelRemove(Fluid input) {
            this.input = input;
        }

        @Override
        public void apply() {
            if (!applied) {
                output = BrewingRecipe.instance.recipeMap().get(input);
                if (output == null) {
                    MineTweakerAPI.getLogger().logError("Barrel recipe removal: Couldn't apply recipe removal (input item unexpectedly has no recipe)");
                    return;
                }
                BrewingRecipe.instance.recipeMap().remove(input);
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
                BrewingRecipe.instance.registerRecipe(input, output);
                applied = false;
            }
        }

        @Override
        public String describeUndo() {
            return "Restoring barrel brewing recipe for " + input.getName();
        }

        @Override
        public String describe() {
            return "Removing barrel brewing for " + input.getName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }

    }

}

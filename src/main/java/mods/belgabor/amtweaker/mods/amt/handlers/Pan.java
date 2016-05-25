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
import mods.belgabor.amtweaker.mods.amt.util.AMTListAddition;
import mods.belgabor.amtweaker.mods.amt.util.AMTRecipeWrapper;
import mods.belgabor.amtweaker.mods.amt.util.BlockAddition;
import mods.belgabor.amtweaker.util.BaseListRemoval;
import mods.defeatedcrow.api.appliance.SoupType;
import mods.defeatedcrow.api.recipe.IFondueRecipe;
import mods.defeatedcrow.api.recipe.IFondueSource;
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
        private final ItemStack output;
        private final ItemStack jbowl_output;
        private final ItemStack input;
        private final String texture;
        private final String display;

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
            super("Clay Pan Heat Source", RecipeRegisterManager.panRecipe.getHeatSourcesList(), block);
        }

        @Override
        public void apply() {
            RecipeRegisterManager.panRecipe.registerHeatSource(block, getMeta());
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static IFondueRecipe findRecipe(Object input, int type) {
        for(IFondueRecipe recipe : RecipeRegisterManager.fondueRecipe.getRecipeList()) {
            boolean inputMatch = false;
            if ((recipe.getInput() instanceof String) && (input instanceof String)) {
                if (recipe.getInput().equals(input)) {
                    inputMatch = true;
                }
            }
            else if ((recipe.getInput() instanceof ItemStack) && (input instanceof ItemStack)) {
                if (areEqualNull((ItemStack) recipe.getInput(), (ItemStack) input))
                {
                    inputMatch = true;
                }
            }
            if (inputMatch && (recipe.getType().id == type)) {
                return recipe;
            }
        }

        return null;
    }

    // Adding a new cooking recipe for the clay pan

    @ZenMethod
    public static void addFondueRecipe(IItemStack output, IIngredient input, int type) {
        Object cinput = toObject(input, true);
        if ((output == null) || (cinput == null)) {
            MineTweakerAPI.getLogger().logError("Fondue Recipe: Neither input nor output may be null!");
            return;
        }
        if ((type < 0) || (type >= SoupType.types.length)) {
            MineTweakerAPI.getLogger().logError(String.format("Fondue Recipe: Unknown soup type %d for input item %s", type, input.toString()));
            return;
        }
        if (findRecipe(cinput, type) != null) {
            MineTweakerAPI.getLogger().logError(String.format("Fondue Recipe: Input item %s already has a recipe for soup type %d (%s)", input.toString(), type, SoupType.getType(type).display));
            return;
        }
        MineTweakerAPI.apply(new ChocolateAdd(output, cinput, type));
    }

    @ZenMethod
    public static void addChocolateRecipe(IItemStack output, IIngredient input) {
        addFondueRecipe(output, input, SoupType.CHOCO.id);
    }

    private static class ChocolateAdd implements IUndoableAction {
        private final ItemStack output;
        private final Object input;
        private Boolean applied;
        private final Integer type;

        public ChocolateAdd(IItemStack output, Object input, int type)  {
            this.output = toStack(output, true);
            this.input = input;
            this.type = type;
            this.applied = false;
        }

        @Override
        public void apply() {
            if (!applied) {
                if (input instanceof String) {
                    RecipeRegisterManager.fondueRecipe.registerByOre((String) input, output, SoupType.getType(type));
                } else {
                    RecipeRegisterManager.fondueRecipe.register((ItemStack) input, output, SoupType.getType(type));
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
                RecipeRegisterManager.fondueRecipe.getRecipeList().remove(findRecipe(input, type));
                applied = false;
            }
        }

        @Override
        public String describe() {
            return "Adding Fondue Recipe for " + output.getDisplayName();
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
    public static void removeFondueRecipe(IIngredient input, int type) {
        Object cinput = toObject(input, true);
        if (cinput == null) {
            MineTweakerAPI.getLogger().logError("Fondue Recipe removal: Input may not be null!");
            return;
        }
        if ((type < 0) || (type >= SoupType.types.length)) {
            MineTweakerAPI.getLogger().logError(String.format("Fondue Recipe removal: Unknown soup type %d for input item %s", type, input.toString()));
            return;
        }
        if (findRecipe(cinput, type) == null) {
            MineTweakerAPI.getLogger().logError(String.format("Fondue Recipe removal: Input item %s has no recipe for soup type %d (%s)", input.toString(), type, SoupType.getType(type).display));
            return;
        }
        MineTweakerAPI.apply(new ChocolateRemove(cinput, type));
    }

    @ZenMethod
    public static void removeChocolateRecipe(IIngredient input) {
        removeFondueRecipe(input, SoupType.CHOCO.id);
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class ChocolateRemove implements IUndoableAction {
        private ItemStack output = null;
        private final Object input;
        private final Integer type;
        private Boolean applied = false;

        public ChocolateRemove(Object input, int type) {
            this.input = input;
            this.type = type;
        }

        @Override
        public void apply() {
            if (!applied) {
                IFondueRecipe recipe = findRecipe(input, type);
                if (recipe == null) {
                    MineTweakerAPI.getLogger().logError("Fondue Recipe removal: Couldn't apply recipe removal (input item unexpectedly has no recipe)");
                    return;
                }
                output = recipe.getOutput();
                RecipeRegisterManager.fondueRecipe.getRecipeList().remove(recipe);
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
                    RecipeRegisterManager.fondueRecipe.registerByOre((String) input, output, SoupType.getType(type));
                } else {
                    RecipeRegisterManager.fondueRecipe.register((ItemStack) input, output, SoupType.getType(type));
                }
                applied = false;
            }
        }

        @Override
        public String describeUndo() {
            String x;
            if (input instanceof String) {
                x = "<ore:" + input + ">";
            } else {
                x = ((ItemStack) input).getDisplayName();
            }
            return String.format("Restoring Fondue Recipe for %s in soup type %d (%s)", x, type, SoupType.getType(type).display);
        }

        @Override
        public String describe() {
            String x;
            if (input instanceof String) {
                x = "<ore:" + input + ">";
            } else {
                x = ((ItemStack) input).getDisplayName();
            }
            return String.format("Removing Fondue Recipe for %s in soup type %d (%s)", x, type, SoupType.getType(type).display);
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static IFondueSource findSource(Object input, int beforeType) {
        for(IFondueSource recipe : RecipeRegisterManager.fondueRecipe.getSourceList()) {
            boolean inputMatch = false;
            if ((recipe.getInput() instanceof String) && (input instanceof String)) {
                if (recipe.getInput().equals(input)) {
                    inputMatch = true;
                }
            }
            else if ((recipe.getInput() instanceof ItemStack) && (input instanceof ItemStack)) {
                if (areEqualNull((ItemStack) recipe.getInput(), (ItemStack) input))
                {
                    inputMatch = true;
                }
            }
            if (inputMatch && (recipe.beforeType().id == beforeType)) {
                return recipe;
            }
        }

        return null;
    }

    // Adding a new fondue source recipe

    @ZenMethod
    public static void addFondueSource(IIngredient input, int beforeType, int afterType) {
        Object cinput = toObject(input, true);
        if (cinput == null) {
            MineTweakerAPI.getLogger().logError("Fondue Source: Input may not be null!");
            return;
        }
        if ((beforeType < 0) || (beforeType >= SoupType.types.length)) {
            MineTweakerAPI.getLogger().logError(String.format("Fondue Source: Unknown soup type %d for input item %s", beforeType, input.toString()));
            return;
        }
        if ((afterType < 0) || (afterType >= SoupType.types.length)) {
            MineTweakerAPI.getLogger().logError(String.format("Fondue Source: Unknown soup type %d for input item %s", afterType, input.toString()));
            return;
        }
        if (findSource(cinput, beforeType) != null) {
            MineTweakerAPI.getLogger().logError(String.format("Fondue Source: Input item %s already has a recipe for soup type %d (%s)", input.toString(), beforeType, SoupType.getType(beforeType).display));
            return;
        }
        MineTweakerAPI.apply(new SourceAdd(cinput, beforeType, afterType));
    }

    private static class SourceAdd implements IUndoableAction {
        private final Object input;
        private Boolean applied;
        private final Integer beforeType;
        private final Integer afterType;

        public SourceAdd(Object input, int beforeType, int afterType)  {
            this.input = input;
            this.beforeType = beforeType;
            this.afterType = afterType;
            this.applied = false;
        }

        @Override
        public void apply() {
            if (!applied) {
                RecipeRegisterManager.fondueRecipe.registerSource(input, SoupType.getType(beforeType), SoupType.getType(afterType));
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
                RecipeRegisterManager.fondueRecipe.getSourceList().remove(findSource(input, beforeType));
                applied = false;
            }
        }

        @Override
        public String describe() {
            return "Adding Fondue Source for " + SoupType.getType(afterType).display;
        }

        @Override
        public String describeUndo() {
            return "Removing Chocolate Recipe for " + SoupType.getType(afterType).display;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Removing a fondue source recipe
    @ZenMethod
    public static void removeFondueSource(IIngredient input, int beforeType) {
        Object cinput = toObject(input, true);
        if (cinput == null) {
            MineTweakerAPI.getLogger().logError("Fondue Source removal: Input may not be null!");
            return;
        }
        if ((beforeType < 0) || (beforeType >= SoupType.types.length)) {
            MineTweakerAPI.getLogger().logError(String.format("Fondue Source removal: Unknown soup type %d for input item %s", beforeType, input.toString()));
            return;
        }
        if (findSource(cinput, beforeType) == null) {
            MineTweakerAPI.getLogger().logError(String.format("Fondue Source removal: Input item %s has no recipe for soup type %d (%s)", input.toString(), beforeType, SoupType.getType(beforeType).display));
            return;
        }
        MineTweakerAPI.apply(new SourceRemove(cinput, beforeType));
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class SourceRemove implements IUndoableAction {
        private final Object input;
        private final Integer beforeType;
        private Integer afterType;
        private Boolean applied = false;

        public SourceRemove(Object input, int beforeType) {
            this.input = input;
            this.beforeType = beforeType;
        }

        @Override
        public void apply() {
            if (!applied) {
                IFondueSource recipe = findSource(input, beforeType);
                if (recipe == null) {
                    MineTweakerAPI.getLogger().logError("Fondue Source removal: Couldn't apply recipe removal (input item unexpectedly has no recipe)");
                    return;
                }
                afterType = recipe.afterType().id;
                RecipeRegisterManager.fondueRecipe.getSourceList().remove(recipe);
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
                RecipeRegisterManager.fondueRecipe.registerSource(input, SoupType.getType(beforeType), SoupType.getType(afterType));
                applied = false;
            }
        }

        @Override
        public String describeUndo() {
            String x;
            if (input instanceof String) {
                x = "<ore:" + input + ">";
            } else {
                x = ((ItemStack) input).getDisplayName();
            }
            return String.format("Restoring Fondue Source for %s in soup type %d (%s)", x, beforeType, SoupType.getType(beforeType).display);
        }

        @Override
        public String describe() {
            String x;
            if (input instanceof String) {
                x = "<ore:" + input + ">";
            } else {
                x = ((ItemStack) input).getDisplayName();
            }
            return String.format("Removing Fondue Source for %s in soup type %d (%s)", x, beforeType, SoupType.getType(beforeType).display);
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }

    }

}


package mods.belgabor.amtweaker.mods.ss2.handlers;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import mods.belgabor.amtweaker.mods.amt.util.AMTListAddition;
import mods.belgabor.amtweaker.util.BaseListWildcardRemoval;
import mods.belgabor.amtweaker.util.CommandLoggerBase;
import mods.defeatedcrow.api.recipe.IProcessorRecipe;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import shift.sextiarysector.recipe.FurnaceCraftingManager;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;

import static mods.belgabor.amtweaker.helpers.InputHelper.toObjects;
import static mods.belgabor.amtweaker.helpers.InputHelper.toShapedObjects;
import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqualNull;

@ZenClass("mods.ss2.LargeFurnace")
public class LargeFurnace {
    private static final int TYPE_ANY = 0;
    private static final int TYPE_SHAPELESS = 1;
    private static final int TYPE_SHAPED = 2;
    
    
    @ZenMethod
    public static void addShapeless(IItemStack output, IIngredient[] inputs) {
        if (inputs == null) {
            MineTweakerAPI.getLogger().logError("Large Furnace: Input set must not be null!");
            return;
        }
        if (inputs.length == 0) {
            MineTweakerAPI.getLogger().logError("Large Furnace: Input set must not empty!");
            return;
        }
        if (output == null) {
            MineTweakerAPI.getLogger().logError("Large Furnace: Output must not be null!");
            return;
        }
        ShapelessOreRecipe recipe = constructSafely(toStack(output), toObjects(inputs));
        if (recipe == null) {
            MineTweakerAPI.getLogger().logError("Large Furnace: Illegal recipe.");
            return;
        }
        MineTweakerAPI.apply(new FurnaceAdd(output, recipe));
    }

    @ZenMethod
    public static void addShaped(IItemStack output, IIngredient[][] inputs) {
        if (inputs == null) {
            MineTweakerAPI.getLogger().logError("Large Furnace: Input set must not be null!");
            return;
        }
        if (inputs.length == 0) {
            MineTweakerAPI.getLogger().logError("Large Furnace: Input set must not empty!");
            return;
        }
        if (output == null) {
            MineTweakerAPI.getLogger().logError("Large Furnace: Output must not be null!");
            return;
        }
        ShapedOreRecipe recipe = constructSafelyShaped(toStack(output), toShapedObjects(inputs, true));
        if (recipe == null) {
            MineTweakerAPI.getLogger().logError("Large Furnace: Illegal recipe.");
            return;
        }
        MineTweakerAPI.apply(new FurnaceAdd(output, recipe));
    }

    private static ShapelessOreRecipe constructSafely(ItemStack output, Object[] inputs) {
        try {
            return new ShapelessOreRecipe(output, inputs);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static ShapedOreRecipe constructSafelyShaped(ItemStack output, Object[] inputs) {
        try {
            return new ShapedOreRecipe(output, inputs);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static IRecipe findRecipe(ItemStack stack, int type) {
        final IRecipe[] recipe = {null};
        FurnaceCraftingManager.getInstance().getRecipeList().stream()
        .filter(xRecipe -> {
            if (type == TYPE_SHAPELESS) {
                return xRecipe instanceof ShapelessOreRecipe || xRecipe instanceof ShapelessRecipes;
            } else if (type == TYPE_SHAPED) {
                return xRecipe instanceof ShapedOreRecipe;
            }
            return xRecipe instanceof IRecipe;
        })        
        .forEach(xRecipe -> {
            if (areEqual(stack, ((IRecipe) xRecipe).getRecipeOutput())) {
                recipe[0] = (IRecipe) xRecipe;
            }
        });
        return recipe[0];
    }

    private static class FurnaceAdd implements IUndoableAction {
        private final ItemStack output;
        private final IRecipe recipe;
        private boolean applied = false;

        public FurnaceAdd(IItemStack output, IRecipe recipe) {
            this.output = toStack(output);
            this.recipe = recipe;
        }
        
        @Override
        public void apply() {
            if (!applied) {
                FurnaceCraftingManager.getInstance().addRecipe(recipe);
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
                FurnaceCraftingManager.getInstance().getRecipeList().remove(recipe);
                applied = false;
            }
        }

        @Override
        public String describe() {
            return "Adding Large Furnace recipe for " + CommandLoggerBase.getFullObjectDeclaration(output);
        }

        @Override
        public String describeUndo() {
            return "Removing Large Furnace recipe for " + CommandLoggerBase.getFullObjectDeclaration(output);
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
    

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ZenMethod
    public static void removeShapeless(IItemStack output) {
        doRemove(output, TYPE_SHAPELESS);
    }

    @ZenMethod
    public static void removeShaped(IItemStack output) {
        doRemove(output, TYPE_SHAPED);
    }

    @ZenMethod
    public static void remove(IItemStack output) {
        doRemove(output, TYPE_ANY);
    }
    
    private static void doRemove(IItemStack output, int type) {
        if (output == null) {
            MineTweakerAPI.getLogger().logError("Large Furnace: Output must not be null!");
            return;
        }
        ItemStack sOutput = toStack(output, true);
        if (findRecipe(sOutput, type) == null) {
            MineTweakerAPI.getLogger().logError("Large Furnace: No recipe for " + output.toString());
            return;
        }
        MineTweakerAPI.apply(new FurnaceRemove(sOutput, type));
    }

    private static class FurnaceRemove implements IUndoableAction {
        private final ItemStack output;
        private ArrayList<IRecipe> recipes = new ArrayList<>();
        private boolean applied = false;

        public FurnaceRemove(ItemStack output, int type) {
            this.output = output;
            FurnaceCraftingManager.getInstance().getRecipeList().stream().filter(xRecipe -> xRecipe instanceof IRecipe)
            .filter(xRecipe -> {
                if (type == TYPE_SHAPELESS) {
                    return xRecipe instanceof ShapelessOreRecipe || xRecipe instanceof ShapelessRecipes;
                } else if (type == TYPE_SHAPED) {
                    return xRecipe instanceof ShapedOreRecipe;
                }
                return xRecipe instanceof IRecipe;
            })
            .forEach(xRecipe -> {
                if (areEqual(output, ((IRecipe) xRecipe).getRecipeOutput())) {
                    recipes.add((IRecipe) xRecipe);
                }
            });
            
        }

        @Override
        public void apply() {
            if (!applied) {
                recipes.stream().forEach(recipe -> FurnaceCraftingManager.getInstance().getRecipeList().remove(recipe));
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
                recipes.stream().forEach(recipe -> FurnaceCraftingManager.getInstance().addRecipe(recipe));
                applied = false;
            }
        }

        @Override
        public String describe() {
            return "Removing Large Furnace recipes for " + CommandLoggerBase.getFullObjectDeclaration(output);
        }

        @Override
        public String describeUndo() {
            return "Restoring Large Furnace recipes for " + CommandLoggerBase.getFullObjectDeclaration(output);
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

}

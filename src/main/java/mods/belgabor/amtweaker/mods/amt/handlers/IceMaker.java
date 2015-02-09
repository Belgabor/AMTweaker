package mods.belgabor.amtweaker.mods.amt.handlers;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import mods.belgabor.amtweaker.mods.amt.util.AMTListAddition;
import mods.belgabor.amtweaker.mods.amt.util.AMTRecipeWrapper;
import mods.belgabor.amtweaker.util.BaseListRemoval;
import mods.defeatedcrow.api.recipe.IChargeIce;
import mods.defeatedcrow.api.recipe.IIceRecipe;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqualNull;

/**
 * Created by Belgabor on 09.02.2015.
 */
@ZenClass("mods.amt.IceMaker")
public class IceMaker {
    // Adding a new recipe for the ice maker
    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack input, IItemStack container) {
        MineTweakerAPI.apply(new Add(new IceMakerRecipeWrapper(output, input, container)));
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IItemStack input) {
        MineTweakerAPI.apply(new Add(new IceMakerRecipeWrapper(output, input, null)));
    }

    private static class IceMakerRecipeWrapper extends AMTRecipeWrapper {
        private ItemStack output;
        private ItemStack input;
        private ItemStack container;

        public IceMakerRecipeWrapper(IItemStack output, IItemStack input, IItemStack container) {
            this.output = toStack(output);
            this.container = toStack(container);
            this.input = toStack(input, true);
        }

        @Override
        public void register() {
            if (container != null) {
                RecipeRegisterManager.iceRecipe.registerCanLeave(input, output, container);
            } else {
                RecipeRegisterManager.iceRecipe.register(input, output);
            }
        }

        @Override
        public boolean matches(Object o) {
            IIceRecipe r = (IIceRecipe) o;
            return (areEqualNull(r.getInput(), input)) &&
                    (areEqualNull(r.getOutput(), output)) &&
                    (areEqualNull(r.getContainer(), container));
        }

        @Override
        public String getRecipeInfo() {
            if (container == null) {
                return this.output.getDisplayName();
            } else {
                return this.output.getDisplayName() + " + " + this.container.getDisplayName();
            }
        }
    }

    //Passes the list to the base list implementation, and adds the recipe
    private static class Add extends AMTListAddition {

        public Add(IceMakerRecipeWrapper recipe) {
            super("Ice Maker", RecipeRegisterManager.iceRecipe.getRecipeList(), recipe);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Removing an ice maker recipe
    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        MineTweakerAPI.apply(new Remove(toStack(output)));
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class Remove extends BaseListRemoval {
        public Remove(ItemStack stack) {
            super("Ice Maker", RecipeRegisterManager.iceRecipe.getRecipeList(), stack);
        }

        //Loops through the registry, to find the item that matches, saves that recipe then removes it
        @Override
        public void apply() {
            for (IIceRecipe r : RecipeRegisterManager.iceRecipe.getRecipeList()) {
                if (r.getOutput() != null && areEqual(r.getOutput(), stack)) {
                    recipe = r;
                    break;
                }
            }

            if (recipe == null) {
                MineTweakerAPI.getLogger().logWarning("No ice maker recipe for " + getRecipeInfo() + " found.");
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

    // Adding a new charge item for the ice maker
    @ZenMethod
    public static void registerChargeItem(IItemStack item, int charge) {
        MineTweakerAPI.apply(new AddCharge(new ChargeItemWrapper(item, charge)));
    }

    private static class ChargeItemWrapper extends AMTRecipeWrapper {
        private ItemStack item;
        private int charge;

        public ChargeItemWrapper(IItemStack item, int charge) {
            this.item = toStack(item, true);
            this.charge = charge;
        }

        @Override
        public void register() {
            RecipeRegisterManager.iceRecipe.registerCharger(item, charge);
        }

        @Override
        public boolean matches(Object o) {
            IChargeIce r = (IChargeIce) o;
            return (areEqualNull(r.getItem(), item)) &&
                    (r.chargeAmount() == charge);
        }

        @Override
        public String getRecipeInfo() {
            return this.item.getDisplayName() + " (" + this.charge + ")";
        }
    }

    //Passes the list to the base list implementation, and adds the recipe
    private static class AddCharge extends AMTListAddition {

        public AddCharge(ChargeItemWrapper recipe) {
            super("Ice Maker Charge Item", RecipeRegisterManager.iceRecipe.getChargeItemList(), recipe);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Removing an ice maker charge item
    @ZenMethod
    public static void unregisterChargeItem(IItemStack item) {
        MineTweakerAPI.apply(new RemoveCharge(toStack(item)));
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class RemoveCharge extends BaseListRemoval {
        public RemoveCharge(ItemStack stack) {
            super("Ice Maker Charge Item", RecipeRegisterManager.iceRecipe.getChargeItemList(), stack);
        }

        //Loops through the registry, to find the item that matches, saves that recipe then removes it
        @Override
        public void apply() {
            for (IChargeIce r : RecipeRegisterManager.iceRecipe.getChargeItemList()) {
                if (r.getItem() != null && areEqual(r.getItem(), stack)) {
                    recipe = r;
                    break;
                }
            }

            if (recipe == null) {
                MineTweakerAPI.getLogger().logWarning(getRecipeInfo() + " is not an ice maker charge item.");
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

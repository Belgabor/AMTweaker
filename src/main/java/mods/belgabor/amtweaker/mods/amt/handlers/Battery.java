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
import mods.defeatedcrow.api.charge.ChargeItemManager;
import mods.defeatedcrow.api.charge.IChargeItem;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqualNull;

/**
 * Created by Belgabor on 10.02.2015.
 */
@ZenClass("mods.amt.Battery")
public class Battery {
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Adding a new charge item for the ice maker
    @ZenMethod
    public static void registerChargeItem(IItemStack item, IItemStack discharged, int charge) {
        doRegisterChargeItem(item, discharged, charge);
    }

    @ZenMethod
    public static void registerChargeItem(IItemStack item, int charge) {
        doRegisterChargeItem(item, null, charge);
    }

    private static void doRegisterChargeItem(IItemStack item, IItemStack discharged, int charge) {
        if (item == null) {
            MineTweakerAPI.getLogger().logError("Battery: item must not be null!");
            return;
        }
        MineTweakerAPI.apply(new AddCharge(new ChargeItemWrapper(item, discharged, charge)));
    }

    private static class ChargeItemWrapper extends AMTRecipeWrapper {
        private ItemStack item;
        private ItemStack discharged;
        private int charge;

        public ChargeItemWrapper(IItemStack item, IItemStack discharged, int charge) {
            this.item = toStack(item, true);
            this.discharged = toStack(item);
            this.charge = charge;
        }

        @Override
        public void register() {
            ChargeItemManager.chargeItem.registerCharger(item, discharged, charge);
        }

        @Override
        public boolean matches(Object o) {
            IChargeItem r = (IChargeItem) o;
            return (areEqualNull(r.getItem(), item)) &&
                    (areEqualNull(r.returnItem(), discharged)) &&
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
            super("Battery", ChargeItemManager.chargeItem.getChargeItemList(), recipe);
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
            super("Battery", ChargeItemManager.chargeItem.getChargeItemList(), stack);
        }

        //Loops through the registry, to find the item that matches, saves that recipe then removes it
        @Override
        public void apply() {
            for (IChargeItem r : ChargeItemManager.chargeItem.getChargeItemList()) {
                if (r.getItem() != null && areEqual(r.getItem(), stack)) {
                    recipe = r;
                    break;
                }
            }

            if (recipe == null) {
                MineTweakerAPI.getLogger().logWarning(getRecipeInfo() + " is not a battery.");
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

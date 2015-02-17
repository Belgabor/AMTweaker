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
import minetweaker.api.item.IItemStack;
import mods.defeatedcrow.plugin.LoadBambooPlugin;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;

/**
 * Created by Belgabor on 17.02.2015.
 */
@ZenClass("mods.amt.BambooBasket")
public class BambooBasket {
    @ZenMethod
    public static void set(IItemStack item) {
        if (item == null) {
            MineTweakerAPI.getLogger().logError("Slag loot: Item must not be null!");
            return;
        }
        MineTweakerAPI.apply(new BambooBasketSetAction(item));
    }

    private static class BambooBasketSetAction implements IUndoableAction {
        private final ItemStack item;
        private ItemStack original;

        public BambooBasketSetAction(IItemStack item) {
            this.item = toStack(item);
        }

        @Override
        public void apply() {
            original = LoadBambooPlugin.bambooBasket;
            LoadBambooPlugin.bambooBasket = item;
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            LoadBambooPlugin.bambooBasket = original;
        }

        @Override
        public String describe() {
            return "Setting as bamboo basket: " + item.getDisplayName();
        }

        @Override
        public String describeUndo() {
            return "Reverting bamboo basket from: " + item.getDisplayName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}

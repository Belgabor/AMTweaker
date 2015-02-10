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
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;

/**
 * Created by Belgabor on 10.02.2015.
 */
@ZenClass("mods.amt.Slag")
public class Slag {
    @ZenMethod
    public static void addLoot(IItemStack item, int tier) {
        if (tier <= 0 || tier > 5) {
            MineTweakerAPI.getLogger().logError("Slag loot tier must be 1-5.");
        } else {
            MineTweakerAPI.apply(new SlagLootAction(item, tier));
        }
    }

    private static class SlagLootAction implements IUndoableAction {
        private final ItemStack item;
        private final int tier;

        public SlagLootAction(IItemStack item, int tier) {
            this.item = toStack(item);
            this.tier = tier;
        }

        @Override
        public void apply() {
            RecipeRegisterManager.slagLoot.addLoot(item, tier);
        }

        @Override
        public boolean canUndo() {
            return false;
        }

        @Override
        public void undo() {

        }

        @Override
        public String describe() {
            return "Adding AMT Slag loot to tier " + tier + ": " + item.getDisplayName();
        }

        @Override
        public String describeUndo() {
            return null;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}

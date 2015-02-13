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
import minetweaker.api.minecraft.MineTweakerMC;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import mods.defeatedcrow.recipe.OreCrushRecipe;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;

/**
 * Created by Belgabor on 10.02.2015.
 */
@ZenClass("mods.amt.Slag")
public class Slag {
    @ZenMethod
    public static void addLoot(IItemStack item, int tier) {
        if (item == null) {
            MineTweakerAPI.getLogger().logError("Slag loot: Item must not be null!");
            return;
        }
        if (tier <= 0 || tier > 5) {
            MineTweakerAPI.getLogger().logError("Slag loot tier must be 1-5.");
            return;
        }
        MineTweakerAPI.apply(new SlagLootAddAction(item, tier));
    }

    @ZenMethod
    public static void removeLoot(IItemStack item, int tier) {
        //System.out.println("removeLoot: "+item.getDisplayName()+ " "+tier+" "+item.getDamage()+" "+item.getItems().size());
        if (tier <= 0 || tier > 5) {
            MineTweakerAPI.getLogger().logError("Slag loot tier must be 1-5.");
            return;
        }
        MineTweakerAPI.apply(new SlagLootRemoveAction(item, tier));
    }

    @ZenMethod
    public static void removeLoot(IItemStack item) {
        MineTweakerAPI.apply(new SlagLootRemoveAllAction(item));
    }

    private static List<ItemStack> getLootList(int tier) {
        switch(tier)
        {
            case 1:
                return OreCrushRecipe.tier1;
            case 2:
                return OreCrushRecipe.tier2;
            case 3:
                return OreCrushRecipe.tier3;
            case 4:
                return OreCrushRecipe.tier4;
            case 5:
                return OreCrushRecipe.tier5;
            default:
                return null;
        }
    }


    private static class SlagLootAddAction implements IUndoableAction {
        private final ItemStack item;
        private final int tier;
        private final List<ItemStack> list;

        public SlagLootAddAction(IItemStack item, int tier) {
            this.item = toStack(item);
            this.tier = tier;
            //this.list = RecipeRegisterManager.slagLoot.getLootList(tier);
            this.list = getLootList(tier);
        }

        @Override
        public void apply() {
            RecipeRegisterManager.slagLoot.addLoot(item, tier);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            boolean found = false;
            for(ItemStack i: list) {
                if (areEqual(i, item)) {
                    found = true;
                    list.remove(i);
                    break;
                }
            }
            if (!found) {
                MineTweakerAPI.getLogger().logWarning("Could not remove slag loot " + item.getDisplayName() + " from tier " + tier);
            }
        }

        @Override
        public String describe() {
            return "Adding AMT Slag loot to tier " + tier + ": " + item.getDisplayName();
        }

        @Override
        public String describeUndo() {
            return "Removing AMT Slag loot from tier " + tier + ": " + item.getDisplayName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class SlagLootRemoveAction implements IUndoableAction {
        private final IItemStack item;
        private final int tier;
        private final List<ItemStack> list;
        private final List<ItemStack> items;

        public SlagLootRemoveAction(IItemStack item, int tier) {
            this.item = item;
            this.tier = tier;
            this.items = new ArrayList<ItemStack>();
            //this.list = RecipeRegisterManager.slagLoot.getLootList(tier);
            this.list = getLootList(tier);
        }

        @Override
        public void undo() {
            for (ItemStack i: items) RecipeRegisterManager.slagLoot.addLoot(i, tier);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void apply() {
            items.clear();
            ItemStack it = toStack(item);
            if (item.getDamage() == 32767) {
                // :*
                for(ItemStack i: list) {
                    if (i.getItem() == it.getItem()) {
                        items.add(i);
                        //System.out.println(MineTweakerMC.getIItemStack(i).toString());
                    }
                }
            } else {
                for(ItemStack i: list) {
                    if (areEqual(i, it)) {
                        items.add(i);
                        break;
                    }
                }
            }
            if (items.size() == 0) {
                MineTweakerAPI.getLogger().logWarning(item.toString() + " is not a slag loot of tier " + tier);
            } else {
                for(ItemStack i: items) {
                    list.remove(i);
                }
            }
        }

        @Override
        public String describeUndo() {
            return "Restoring AMT Slag loot to tier " + tier + ": " + item.toString();
        }

        @Override
        public String describe() {
            return "Removing AMT Slag loot from tier " + tier + ": " + item.toString();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class SlagLootRemoveAllAction implements IUndoableAction {
        private final IItemStack item;
        private final List<ItemStack> items;
        private final List<Integer> tiers;

        public SlagLootRemoveAllAction(IItemStack item) {
            this.item = item;
            this.items = new ArrayList<ItemStack>();
            this.tiers = new ArrayList<Integer>();
        }

        @Override
        public void undo() {
            for (int i=0; i<items.size(); i++) {
                RecipeRegisterManager.slagLoot.addLoot(items.get(i), tiers.get(i));
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void apply() {
            items.clear();
            tiers.clear();
            ItemStack it = toStack(item);
            for (int t=1; t<=5; t++) {
                if (item.getDamage() == 32767) {
                    // :*
                    for(ItemStack i: getLootList(t)) {
                        if (i.getItem() == it.getItem()) {
                            items.add(i);
                            tiers.add(t);
                            //System.out.println(MineTweakerMC.getIItemStack(i).toString());
                        }
                    }
                } else {
                    for(ItemStack i: getLootList(t)) {
                        if (areEqual(i, it)) {
                            items.add(i);
                            tiers.add(t);
                            break;
                        }
                    }
                }
            }
            if (items.size() == 0) {
                MineTweakerAPI.getLogger().logWarning(item.toString() + " is not slag loot");
            } else {
                for (int i=0; i<items.size(); i++) {
                    getLootList(tiers.get(i)).remove(items.get(i));
                }
            }
        }

        @Override
        public String describeUndo() {
            return "Restoring AMT Slag loot: " + item.getDisplayName();
        }

        @Override
        public String describe() {
            return "Removing AMT Slag loot: " + item.getDisplayName();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}

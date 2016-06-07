package mods.belgabor.amtweaker.helpers;

/**
 * Originally part of ModTweaker by joshiejack
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 joshiejack
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
 *
 */

import java.util.ArrayList;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class InputHelper {
    public static boolean isABlock(IItemStack block) {
        if (!(isABlock(toStack(block)))) {
            MineTweakerAPI.getLogger().logError("Item must be a block");
            return false;
        } else return true;
    }

    public static boolean isABlock(ItemStack block) {
        return block.getItem() instanceof ItemBlock;
    }

    public static ItemStack toStack(IItemStack iStack) {
        return toStack(iStack, false);
    }
    public static ItemStack toStack(IItemStack iStack, boolean trim) {
        if (iStack == null) return null;
        else {
            Object internal = iStack.getInternal();
            if (internal == null || !(internal instanceof ItemStack)) {
                MineTweakerAPI.getLogger().logError("Not a valid item stack: " + iStack);
            } else if (trim) {
                ItemStack t = (ItemStack) internal;
                if (t.stackSize > 1) {
                    MineTweakerAPI.getLogger().logWarning("Stack size not supported, reduced to one item.");
                    t.stackSize = 1;
                }
                return t;
            }
            //noinspection ConstantConditions
            return (ItemStack) internal;
        }
    }

    public static ItemStack[] toStacks(IItemStack[] iStack) {
        if (iStack == null) return null;
        else {
            ItemStack[] output = new ItemStack[iStack.length];
            for (int i = 0; i < iStack.length; i++) {
                output[i] = toStack(iStack[i]);
            }

            return output;
        }
    }

    public static Object toObject(IIngredient iStack) {
        return toObject(iStack, false);
    }


    public static Object toObject(IIngredient iStack, boolean trim) {
        if (iStack == null) return null;
        else {
            if (iStack instanceof IOreDictEntry) {
                return toString((IOreDictEntry) iStack);
            } else if (iStack instanceof IItemStack) {
                return toStack((IItemStack) iStack, trim);
            } else return null;
        }
    }

    public static Object[] toObjects(IIngredient[] ingredient) {
        return toObjects(ingredient, false);
    }

    public static Object[] toObjects(IIngredient[] ingredient, boolean trim) {
        if (ingredient == null) return null;
        else {
            Object[] output = new Object[ingredient.length];
            for (int i = 0; i < ingredient.length; i++) {
                if (ingredient[i] != null) {
                    output[i] = toObject(ingredient[i], trim);
                } else output[i] = "";
            }

            return output;
        }
    }

    public static Object[] toShapedObjects(IIngredient[][] ingredients) {
        return toShapedObjects(ingredients, false);
    }
    
    public static Object[] toShapedObjects(IIngredient[][] ingredients, boolean trim) {
        if (ingredients == null) return null;
        else {
            int h = trim?ingredients.length:3;
            int w = 0;
            if (trim) {
                for (IIngredient[] test: ingredients) {
                    if (test.length > w)
                        w = test.length;
                }
            } else {
                w = 3;
            }
            
            ArrayList prep = new ArrayList();
            prep.add("abc".substring(0, w));
            if (h>=2)
                prep.add("def".substring(0, w));
            if (h>=3)
                prep.add("ghi".substring(0, w));
            
            char[][] map = new char[][] { { 'a', 'b', 'c' }, { 'd', 'e', 'f' }, { 'g', 'h', 'i' } };
            for (int x = 0; x < ingredients.length; x++) {
                if (ingredients[x] != null) {
                    for (int y = 0; y < ingredients[x].length; y++) {
                        if (ingredients[x][y] != null && x < map.length && y < map[x].length) {
                            prep.add(map[x][y]);
                            prep.add(toObject(ingredients[x][y]));
                        }
                    }
                }
            }
            return prep.toArray();
        }
    }

    public static String toString(IOreDictEntry entry) {
        return entry.getName();
    }

    public static FluidStack toFluid(ILiquidStack iStack) {
        if (iStack == null) {
            return null;
        } else return FluidRegistry.getFluidStack(iStack.getName(), iStack.getAmount());
    }
/*
    public static FluidStack[] toFluids(IIngredient[] input) {
        return toFluids((IItemStack[]) input);
    }
*/
    public static FluidStack[] toFluids(ILiquidStack[] iStack) {
        FluidStack[] stack = new FluidStack[iStack.length];
        for (int i = 0; i < stack.length; i++)
            stack[i] = toFluid(iStack[i]);
        return stack;
    }
}

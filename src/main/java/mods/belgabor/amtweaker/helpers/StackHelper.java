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

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class StackHelper {
    //Stack is the stack that is part of a recipe, stack2 is the one input trying to match
    public static boolean areEqual(ItemStack stack, ItemStack stack2) {
        if (stack == null || stack2 == null) return false;
        return stack.isItemEqual(stack2);
    }

    public static boolean areEqualNull(ItemStack stack, ItemStack stack2) {
        return (stack == null ? stack2 == null : stack.isItemEqual(stack2));
    }

    public static boolean areEqualNull(FluidStack stack, FluidStack stack2) {
        return (stack == null ? stack2 == null : stack.isFluidEqual(stack2));
    }
}

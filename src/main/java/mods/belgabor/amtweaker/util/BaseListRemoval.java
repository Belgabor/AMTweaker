package mods.belgabor.amtweaker.util;

/**
 * Part of ModTweaker by joshiejack
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

import java.util.List;

import minetweaker.IUndoableAction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract class BaseListRemoval implements IUndoableAction {
    protected final String description;
    protected final List list;
    protected final FluidStack fluid;
    protected final ItemStack stack;
    protected Object recipe;

    public BaseListRemoval(String description, List list, ItemStack stack, FluidStack fluid) {
        this.list = list;
        this.stack = stack;
        this.description = description;
        this.fluid = fluid;
    }

    public BaseListRemoval(String description, List list, ItemStack stack) {
        this(description, list, stack, null);
    }

    public BaseListRemoval(String description, List list, FluidStack fluid) {
        this(description, list, null, fluid);
    }

    public BaseListRemoval(List list, ItemStack stack) {
        this(null, list, stack);
    }

    public BaseListRemoval(List list, FluidStack stack) {
        this(null, list, stack);
    }

    public BaseListRemoval(String description, List list) {
        this(description, list, null, null);
    }

    @Override
    public boolean canUndo() {
        return list != null;
    }

    @Override
    public void undo() {
        if (recipe != null) {
            list.add(recipe);
        }
    }

    public String getRecipeInfo() {
        return "Unknown Item";
    }

    @Override
    public String describe() {
        if (recipe instanceof ItemStack) return "Removing " + description + " Recipe for :" + ((ItemStack) recipe).getDisplayName();
        else if (recipe instanceof FluidStack) return "Removing " + description + " Recipe for :" + ((FluidStack) recipe).getFluid().getLocalizedName((FluidStack) recipe);
        else return "Removing " + description + " Recipe for :" + getRecipeInfo();
    }

    @Override
    public String describeUndo() {
        if (recipe instanceof ItemStack) return "Restoring " + description + " Recipe for :" + ((ItemStack) recipe).getDisplayName();
        else if (recipe instanceof FluidStack) return "Restoring " + description + " Recipe for :" + ((FluidStack) recipe).getFluid().getLocalizedName((FluidStack) recipe);
        else return "Restoring " + description + " Recipe for :" + getRecipeInfo();
    }

    @Override
    public Object getOverrideKey() {
        return null;
    }
}

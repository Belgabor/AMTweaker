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

package mods.belgabor.amtweaker.util;

import minetweaker.IUndoableAction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Belgabor on 08.03.2015.
 */
public abstract class BaseListWildcardRemoval implements IUndoableAction {
    protected final String description;
    protected final List list;
    protected final FluidStack fluid;
    protected final ItemStack stack;
    protected final ArrayList<Object> recipes;

    public BaseListWildcardRemoval(String description, List list, ItemStack stack, FluidStack fluid) {
        this.list = list;
        this.stack = stack;
        this.description = description;
        this.fluid = fluid;
        this.recipes = new ArrayList<Object>();
    }

    public BaseListWildcardRemoval(String description, List list, ItemStack stack) {
        this(description, list, stack, null);
    }

    public BaseListWildcardRemoval(String description, List list, FluidStack fluid) {
        this(description, list, null, fluid);
    }

    public BaseListWildcardRemoval(List list, ItemStack stack) {
        this(null, list, stack);
    }

    public BaseListWildcardRemoval(List list, FluidStack stack) {
        this(null, list, stack);
    }

    public BaseListWildcardRemoval(String description, List list) {
        this(description, list, null, null);
    }

    @Override
    public boolean canUndo() {
        return list != null;
    }

    @Override
    public void undo() {
        for(Object o: recipes) {
            list.add(o);
        }
    }

    public String getRecipeInfo() {
        return "Unknown Item";
    }

    @Override
    public String describe() {
        return "Removing " + description + " Recipe for :" + getRecipeInfo();
    }

    @Override
    public String describeUndo() {
        return "Restoring " + description + " Recipe for :" + getRecipeInfo();
    }

    @Override
    public Object getOverrideKey() {
        return null;
    }
}

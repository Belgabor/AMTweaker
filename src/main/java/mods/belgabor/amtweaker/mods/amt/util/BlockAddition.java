package mods.belgabor.amtweaker.mods.amt.util;


import minetweaker.IUndoableAction;
import minetweaker.api.item.IItemStack;
import net.minecraft.block.Block;

import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;

public abstract class BlockAddition implements IUndoableAction {
    protected final String description;
    protected final Block block;
    protected final int meta;

    public BlockAddition(String description, IItemStack stack) {
        this.description = description;
        this.block = Block.getBlockFromItem(toStack(stack).getItem());
        this.meta = toStack(stack).getItemDamage();
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
        return "Restoring " + description + " Recipe for :" + getRecipeInfo();
    }

    @Override
    public String describeUndo() {
        return null;
    }

    @Override
    public Object getOverrideKey() {
        return null;
    }

    public String getRecipeInfo() {
        return block.getLocalizedName();
    }
}

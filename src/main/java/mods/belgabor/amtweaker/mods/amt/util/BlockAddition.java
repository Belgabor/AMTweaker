package mods.belgabor.amtweaker.mods.amt.util;


import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.List;

import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;

public abstract class BlockAddition implements IUndoableAction {
    protected final String description;
    protected final Block block;
    protected final int meta;
    protected final ItemStack item;
    protected final List<ItemStack> list;

    public BlockAddition(String description, List<ItemStack> list, IItemStack stack) {
        this.description = description;
        this.item = toStack(stack);
        this.block = Block.getBlockFromItem(this.item.getItem());
        this.meta = this.item.getItemDamage();
        this.list = list;
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public void undo() {
        boolean found = false;
        for (ItemStack i: list) {
            if (areEqual(i, item)) {
                found = true;
                list.remove(i);
                break;
            }
        }
        if (!found) {
            MineTweakerAPI.getLogger().logWarning("Could not remove heat source: " + item.getDisplayName());
        }
    }

    @Override
    public String describe() {
        return "Adding " + description + ":" + getRecipeInfo();
    }

    @Override
    public String describeUndo() {
        return "Removing " + description + ":" + getRecipeInfo();
    }

    @Override
    public Object getOverrideKey() {
        return null;
    }

    public String getRecipeInfo() {
        return block.getLocalizedName();
    }
}

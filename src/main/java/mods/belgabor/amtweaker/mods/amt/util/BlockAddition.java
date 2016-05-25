package mods.belgabor.amtweaker.mods.amt.util;


import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import mods.defeatedcrow.api.recipe.ICookingHeatSource;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;

public abstract class BlockAddition implements IUndoableAction {
    protected final String description;
    protected final Block block;
    protected final int meta;
    protected final ItemStack item;
    protected final List<? extends ICookingHeatSource> list;

    public BlockAddition(String description, List<? extends ICookingHeatSource> list, IItemStack stack) {
        this.description = description;
        this.item = toStack(stack);
        this.block = Block.getBlockFromItem(this.item.getItem());
        this.meta = this.item.getItemDamage();
        this.list = list;
    }

    protected int getMeta() {
        if (this.meta == OreDictionary.WILDCARD_VALUE) {
            return -1;
        } else {
            return this.meta;
        }
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public void undo() {
        boolean found = false;
        for (ICookingHeatSource i: list) {
            if ((meta == i.getMetadata()) && (block.equals(i.getBlock()))) {
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

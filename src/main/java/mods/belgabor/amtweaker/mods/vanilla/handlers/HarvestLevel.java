package mods.belgabor.amtweaker.mods.vanilla.handlers;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.isABlock;
import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;

/**
 * Created by Belgabor on 03.06.2016.
 */

@ZenClass("mods.vanilla.HarvestLevel")
public class HarvestLevel {
 
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Change harvest level
    
    @ZenMethod
    public static void set(IItemStack item, String tool, int level) {
        if (item == null) {
            MineTweakerAPI.getLogger().logError("Harvest level: Block/Item must not be null!");
            return;
        }
        if (isABlock(toStack(item))) {
            MineTweakerAPI.apply(new HarvestLevelChangeBlock(item, tool, level));
        } else {
            if (tool == null) {
                MineTweakerAPI.getLogger().logError("Harvest level: For items tool must not be null!");
                return;
            }
            MineTweakerAPI.apply(new HarvestLevelChangeItem(item, tool, level));
        }
    }


    private static class HarvestLevelChangeBlock implements IUndoableAction {
        private final ItemStack item;
        private final Block block;
        private final String tool;
        private final Integer level;
        private final int meta;
        private final String[] tools = new String[16];
        private final Integer[] levels = new Integer[16];
        private boolean applied = false;
        
        public HarvestLevelChangeBlock(IItemStack block, String tool, Integer level) {
            this.item = toStack(block, true);
            this.block = Block.getBlockFromItem(this.item.getItem());
            this.tool = tool;
            this.level = level;
            this.meta = this.item.getItemDamage();
            
            for(int i=0; i<16; i++) {
                tools[i] = this.block.getHarvestTool(i);
                levels[i] = this.block.getHarvestLevel(i);
            }
        }
        
        @Override
        public void apply() {
            if (!applied) {
                if (meta == OreDictionary.WILDCARD_VALUE) {
                    this.block.setHarvestLevel(tool, level);
                } else {
                    this.block.setHarvestLevel(tool, level, meta);
                }
                applied = true;
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            if (applied) {
                if (meta == OreDictionary.WILDCARD_VALUE) {
                    for(int i=0; i<16; i++) {
                        this.block.setHarvestLevel(tools[i], levels[i], i);
                    }
                } else {
                    this.block.setHarvestLevel(tools[meta], levels[meta], meta);
                }
                applied = false;
            }
        }
        
        private String getBlockDescription() {
            return this.block.getUnlocalizedName() + ":" + (meta == OreDictionary.WILDCARD_VALUE?"*":Integer.toString(meta));
        }

        @Override
        public String describe() {
            return String.format("Setting harvest level for block %s: %s, %d", getBlockDescription(), tool==null?"null":tool, level);
        }

        @Override
        public String describeUndo() {
            return "Restoring harvest level of block " + getBlockDescription();
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class HarvestLevelChangeItem implements IUndoableAction {
        private final ItemStack item;
        private final Item theItem;
        private final String tool;
        private final Integer level;
        private final Integer currentLevel;
        private boolean applied = false;

        public HarvestLevelChangeItem(IItemStack item, String tool, Integer level) {
            this.item = toStack(item, true);
            this.theItem = this.item.getItem();
            this.tool = tool;
            this.level = level;
            
            currentLevel = theItem.getHarvestLevel(this.item, tool);
        }

        @Override
        public void apply() {
            if (!applied) {
                theItem.setHarvestLevel(tool, level);
                applied = true;
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            if (applied) {
                theItem.setHarvestLevel(tool, currentLevel);
                applied = false;
            }
        }

        @Override
        public String describe() {
            return String.format("Setting harvest level for item %s: %s, %d", item.getDisplayName(), tool, level);
        }

        @Override
        public String describeUndo() {
            return String.format("Restoring harvest level for item %s: %s, %d", item.getDisplayName(), tool, currentLevel);
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}


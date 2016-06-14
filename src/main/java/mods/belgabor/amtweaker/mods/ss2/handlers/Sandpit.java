package mods.belgabor.amtweaker.mods.ss2.handlers;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import mods.belgabor.amtweaker.mods.ss2.SS2;
import mods.belgabor.amtweaker.mods.ss2.util.SS2AccessHelper;
import mods.belgabor.amtweaker.util.CommandLoggerBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import shift.sextiarysector.block.BlockSandpit;
import shift.sextiarysector.recipe.FurnaceCraftingManager;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;

import static mods.belgabor.amtweaker.helpers.InputHelper.*;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;

@ZenClass("mods.ss2.Sandpit")
public class Sandpit {

    @ZenMethod
    public static void add(IItemStack item, int weight) {
        doAdd(item, weight, 0, false);
    }

    @ZenMethod
    public static void add(IItemStack item, int weight, float damage) {
        doAdd(item, weight, damage, false);
    }

    @ZenMethod
    public static void add(IItemStack item, int weight, boolean enchant) {
        doAdd(item, weight, 0, enchant);
    }

    @ZenMethod
    public static void add(IItemStack item, int weight, float damage, boolean enchant) {
        doAdd(item, weight, damage, enchant);
    }
    
    public static void doAdd(IItemStack item, int weight, float damage, boolean enchant) {
        if (!SS2.available) {
            MineTweakerAPI.getLogger().logError("SS2 sandpit support not available.");
            return;
        }
        if (item == null) {
            MineTweakerAPI.getLogger().logError("Sandpit: item must not be null!");
            return;
        }
        if (weight <= 0) {
            MineTweakerAPI.getLogger().logError("Sandpit: wight must be > 0");
            return;
        }
        if (damage < 0) {
            MineTweakerAPI.getLogger().logError("Sandpit: damage must be >= 0");
            return;
        }

        BlockSandpit.ShellEntry entry = new BlockSandpit.ShellEntry(toStack(item), weight).setDamage(damage);
        if (enchant)
            entry.setEnchant();
        
        MineTweakerAPI.apply(new SandpitAdd(entry));
    }
    
    private static abstract class SandpitAction implements IUndoableAction {
        protected final BlockSandpit.ShellEntry entry;
        protected boolean applied = false;

        public SandpitAction(BlockSandpit.ShellEntry entry) {
            this.entry = entry;
        }
        
        protected void add() {
            BlockSandpit.addShell(entry);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        protected void remove() {
            SS2AccessHelper.shellList.remove(entry);
        }
        
        protected String describeEntry() {
            return String.format("%s (%d, %f%s)", CommandLoggerBase.getFullObjectDeclaration(entry.shell), entry.itemWeight,
                    SS2AccessHelper.getShellEntry_damage(entry), SS2AccessHelper.getShellEntry_enchant(entry)?", enchanted":"");
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class SandpitAdd extends SandpitAction {

        public SandpitAdd(BlockSandpit.ShellEntry entry) {
            super(entry);
        }

        @Override
        public void apply() {
            if (!applied) {
                add();
                applied = true;
            }
        }

        @Override
        public void undo() {
            if (applied) {
                remove();
                applied = false;
            }
        }

        @Override
        public String describe() {
            return "Adding Sandpit drop " + describeEntry();
        }

        @Override
        public String describeUndo() {
            return "Removing Sandpit drop " + describeEntry();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ZenMethod
    public static void remove(IItemStack item) {
        if (!SS2.available) {
            MineTweakerAPI.getLogger().logError("SS2 sandpit support not available.");
            return;
        }
        if (item == null) {
            MineTweakerAPI.getLogger().logError("Sandpit: item must not be null!");
            return;
        }

        BlockSandpit.ShellEntry entry = null;
        ItemStack theItem = toStack(item);
        for (BlockSandpit.ShellEntry it : SS2AccessHelper.shellList) {
            if (areEqual(it.shell, theItem)) {
                entry = it;
                break;
            }
        }

        if (entry == null) {
            MineTweakerAPI.getLogger().logError("Sandpit: Not found: " + item.toString());
            return;
        }
        
        MineTweakerAPI.apply(new SandpitRemove(entry));
    }


    private static class SandpitRemove extends SandpitAction {

        public SandpitRemove(BlockSandpit.ShellEntry drop) {
            super(drop);
        }

        @Override
        public void apply() {
            if (!applied) {
                remove();
                applied = true;
            }
        }

        @Override
        public void undo() {
            if (applied) {
                add();
                applied = false;
            }
        }

        @Override
        public String describeUndo() {
            return "Restoring Sandpit drop " + describeEntry();
        }

        @Override
        public String describe() {
            return "Removing Sandpit drop " + describeEntry();
        }
    }

}

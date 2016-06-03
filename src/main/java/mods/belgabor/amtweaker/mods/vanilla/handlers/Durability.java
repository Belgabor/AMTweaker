package mods.belgabor.amtweaker.mods.vanilla.handlers;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;

/**
 * Created by Belgabor on 03.06.2016.
 */
@ZenClass("mods.vanilla.Durability")
public class Durability {

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Change harvest level

    @ZenMethod
    public static void set(IItemStack item, int durability) {
        if (item == null) {
            MineTweakerAPI.getLogger().logError("Durability: Item must not be null!");
            return;
        }
        Item test = toStack(item).getItem();
        if (!test.isDamageable() || test.getHasSubtypes()) {
            MineTweakerAPI.getLogger().logError(String.format("Durability: Item %s has subtypes or isn't damageable!", item.toString()));
            return;
        }
        MineTweakerAPI.apply(new DurabilityChange(item, durability));
    }



    private static class DurabilityChange implements IUndoableAction {
        private final ItemStack stack;
        private final Item item;
        private final Integer durability;
        private final Integer currentDurability;
        private boolean applied = false;

        public DurabilityChange(IItemStack item, Integer durability) {
            this.stack = toStack(item, true);
            this.item = this.stack.getItem();
            this.durability = durability;

            currentDurability = this.item.getMaxDamage(this.stack);
        }

        @Override
        public void apply() {
            if (!applied) {
                item.setMaxDamage(durability);
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
                item.setMaxDamage(currentDurability);
                applied = false;
            }
        }

        @Override
        public String describe() {
            return String.format("Setting durability (max damage) for item %s: %d", stack.getDisplayName(), durability);
        }

        @Override
        public String describeUndo() {
            return String.format("Restoring durability (max damage) for item %s: %d", stack.getDisplayName(), currentDurability);
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}

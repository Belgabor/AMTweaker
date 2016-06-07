package mods.belgabor.amtweaker.mods.ss2.handlers;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import mods.belgabor.amtweaker.util.CommandLoggerBase;
import net.minecraft.item.ItemStack;
import shift.sextiarysector.SSRecipes;
import shift.sextiarysector.recipe.RecipeSimpleFuel;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.toObject;

/**
 * Created by Belgabor on 06.06.2016.
 */

@ZenClass("mods.ss2.Fuel")
public class Fuel {
    @ZenMethod
    public static void addMagic(IIngredient fuel, int amount) {
        doAdd(SSRecipes.magicFuel, "Magic Fuel", fuel, amount);
    }

    @ZenMethod
    public static void removeMagic(IIngredient fuel) {
        doRemove(SSRecipes.magicFuel, "Magic Fuel", fuel);
    }

    @ZenMethod
    public static void addIce(IIngredient fuel, int amount) {
        doAdd(SSRecipes.iceFuel, "Ice Fuel", fuel, amount);
    }

    @ZenMethod
    public static void removeIce(IIngredient fuel) {
        doRemove(SSRecipes.iceFuel, "Ice Fuel", fuel);
    }

    protected static void doAdd(RecipeSimpleFuel handler, String name, IIngredient input, int amount) {
        if (input == null) {
            MineTweakerAPI.getLogger().logError(String.format("%s: Fuel item must not be null!", name));
            return;
        }
        if (input.getAmount() > 1) {
            MineTweakerAPI.getLogger().logWarning(String.format("%s: Sextiary Sector does not support more than on fuel item (%s), the recipe will only require one.", name, input.toString()));
        }
        Object iInput = toObject(input, true);
        if (iInput == null) {
            MineTweakerAPI.getLogger().logError(String.format("%s: Fuel item invalid!", name));
            return;
        }
        if (iInput instanceof String) {
            if (handler.getOreList().containsKey((String) iInput)) {
                MineTweakerAPI.getLogger().logError(String.format("%s: Fuel item %s already set", name, input.toString()));
                return;
            }
        } else if (iInput instanceof ItemStack) {
            if (getActualItemStack(handler, (ItemStack) iInput) != null) {
                MineTweakerAPI.getLogger().logError(String.format("%s: Fuel item %s already set", name, input.toString()));
                return;
            }
        }
        if (amount <= 0) {
            MineTweakerAPI.getLogger().logError(String.format("%s: Amount must be positive!", name));
            return;
        }

        MineTweakerAPI.apply(new FuelAdd(handler, name, iInput, amount));
    }
    
    protected static void doRemove(RecipeSimpleFuel handler, String name, IIngredient fuel) {
        if (fuel == null) {
            MineTweakerAPI.getLogger().logError(String.format("%s: Fuel item must not be null!", name));
            return;
        }
        Object iInput = toObject(fuel, true);
        if (iInput == null) {
            MineTweakerAPI.getLogger().logError(String.format("%s: Fuel item invalid!", name));
            return;
        }
        iInput = toObject(fuel);
        if (iInput == null) {
            MineTweakerAPI.getLogger().logError(String.format("%s: Input item invalid!", name));
            return;
        }
        if (iInput instanceof String) {
            Integer test = handler.getOreList().get((String) iInput);
            if (test == null) {
                MineTweakerAPI.getLogger().logError(String.format("%s: Fuel %s not found.", name, fuel.toString()));
                return;
            }
        } else if (iInput instanceof ItemStack) {
            ItemStack aInput = getActualItemStack(handler, (ItemStack) iInput);
            Integer test = handler.getMetaList().get(aInput);
            if (aInput == null || test == null) {
                MineTweakerAPI.getLogger().logError(String.format("%s: Fuel %s not found.", name, fuel.toString()));
                return;
            }
        }

        MineTweakerAPI.apply(new FuelRemove(handler, name, iInput));
    }

    private static ItemStack getActualItemStack(RecipeSimpleFuel handler, ItemStack item) {
        for (ItemStack sItem : handler.getMetaList().keySet()) {
            if (ItemStack.areItemStacksEqual(item, sItem))
                return sItem;
        }
        return null;
    }

    private static class FuelAdd implements IUndoableAction {
        private final RecipeSimpleFuel handler;
        private final String name;
        private final Object fuel;
        private final int amount;
        private boolean applied = false;
        
        protected FuelAdd(RecipeSimpleFuel handler, String name, Object fuel, int amount) {
            this.handler = handler;
            this.name = name;
            this.fuel = fuel;
            this.amount = amount;
        }

        @Override
        public void apply() {
            if (!applied) {
                if (fuel instanceof String) {
                    handler.add((String) fuel, amount);
                    applied = true;
                } else if (fuel instanceof ItemStack){
                    handler.add((ItemStack) fuel, amount);
                    applied = true;
                }
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            if (applied) {
                if (fuel instanceof String) {
                    handler.getOreList().remove((String) fuel);
                    applied = false;
                } else if (fuel instanceof ItemStack){
                    ItemStack temp = getActualItemStack(handler, (ItemStack) fuel);
                    if (temp == null) {
                        MineTweakerAPI.getLogger().logError(String.format("%s: Unexpectedly could not find recipe when undoing change!", name));
                        return;
                    }
                    handler.getMetaList().remove(temp);
                    applied = false;
                }
            }
        }

        @Override
        public String describe() {
            return String.format("%s: Adding %s (%d)", name, CommandLoggerBase.getObjectDeclaration(fuel), amount);
        }

        @Override
        public String describeUndo() {
            return String.format("%s: Removing %s (%d)", name, CommandLoggerBase.getObjectDeclaration(fuel), amount);
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
    
    private static class FuelRemove implements IUndoableAction {
        private final RecipeSimpleFuel handler;
        private final String name;
        private final Object fuel;
        private Integer amount;
        private boolean applied = false;

        protected FuelRemove(RecipeSimpleFuel handler, String name, Object fuel) {
            this.handler = handler;
            this.name = name;
            this.fuel = fuel;
        }

        @Override
        public void undo() {
            if (applied) {
                if (fuel instanceof String) {
                    handler.add((String) fuel, amount);
                    applied = false;
                } else if (fuel instanceof ItemStack){
                    handler.add((ItemStack) fuel, amount);
                    applied = false;
                }
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void apply() {
            if (!applied) {
                if (fuel instanceof String) {
                    amount = handler.getOreList().get((String) fuel);
                    handler.getOreList().remove((String) fuel);
                    applied = true;
                } else if (fuel instanceof ItemStack){
                    ItemStack temp = getActualItemStack(handler, (ItemStack) fuel);
                    if (temp == null) {
                        MineTweakerAPI.getLogger().logError(String.format("%s: Unexpectedly could not find fuel when removing it!", name));
                        return;
                    }
                    amount = handler.getMetaList().get(temp);
                    handler.getMetaList().remove(temp);
                    applied = true;
                }
            }
        }

        @Override
        public String describe() {
            return String.format("%s: Removing %s", name, CommandLoggerBase.getObjectDeclaration(fuel));
        }

        @Override
        public String describeUndo() {
            return String.format("%s: Restoring %s", name, CommandLoggerBase.getObjectDeclaration(fuel));
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}

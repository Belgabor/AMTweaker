package mods.belgabor.amtweaker.mods.ss2.handlers;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import mods.belgabor.amtweaker.util.CommandLoggerBase;
import net.minecraft.item.ItemStack;
import shift.sextiarysector.api.recipe.INormalRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static mods.belgabor.amtweaker.helpers.InputHelper.toObject;
import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;
import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;

/**
 * Created by Belgabor on 04.06.2016.
 */
public class NormalRecipe {
    protected static void doAdd(INormalRecipe handler, String name, IItemStack output, IIngredient input) {
        if (input == null) {
            MineTweakerAPI.getLogger().logError(String.format("%s: Input item must not be null!", name));
            return;
        }
        if (input.getAmount() > 1) {
            MineTweakerAPI.getLogger().logWarning(String.format("%s: Sextiary Sector does not support more than on input item (%s), the recipe will only require one.", name, input.toString()));
        }
        Object iInput = toObject(input, true);
        if (iInput == null) {
            MineTweakerAPI.getLogger().logError(String.format("%s: Input item invalid!", name));
            return;
        }
        if (iInput instanceof String) {
            if (handler.getOreList().containsKey(iInput)) {
                MineTweakerAPI.getLogger().logError(String.format("%s: Input item %s already has a recipe", name, input.toString()));
                return;
            }
        } else if (iInput instanceof ItemStack) {
            if (getActualItemStack(handler, (ItemStack) iInput) != null) {
                MineTweakerAPI.getLogger().logError(String.format("%s: Input item %s already has a recipe", name, input.toString()));
                return;
            }
        }
        if (output == null) {
            MineTweakerAPI.getLogger().logError(String.format("%s: Output item must not be null!", name));
            return;
        }
        
        MineTweakerAPI.apply(new NormalRecipeAdd(handler, name, output, iInput));
    }
    
    private static ItemStack getActualItemStack(INormalRecipe handler, ItemStack item) {
        for (ItemStack sItem : handler.getMetaList().keySet()) {
            if (ItemStack.areItemStacksEqual(item, sItem))
                return sItem;
        }
        return null;
    }
    
    private static class NormalRecipeAdd implements IUndoableAction {
        private final INormalRecipe handler;
        private final String name;
        private final Object input;
        private final ItemStack output;
        private boolean applied = false;
        
        public NormalRecipeAdd(INormalRecipe handler, String name, IItemStack output, Object input) {
            this.handler = handler;
            this.name = name;
            this.input = input;
            this.output = toStack(output);
        }

        @Override
        public void apply() {
            if (!applied) {
                if (input instanceof String) {
                    handler.add((String) input, output);
                    applied = true;
                } else if (input instanceof ItemStack){
                    handler.add((ItemStack) input, output);
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
                if (input instanceof String) {
                    handler.getOreList().remove(input);
                    applied = false;
                } else if (input instanceof ItemStack){
                    ItemStack temp = getActualItemStack(handler, (ItemStack) input);
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
            return String.format("%s: Adding recipe for %s to %s", name, CommandLoggerBase.getObjectDeclaration(input), CommandLoggerBase.getObjectDeclaration(output));
        }

        @Override
        public String describeUndo() {
            return String.format("%s: Removing recipe for %s to %s", name, CommandLoggerBase.getObjectDeclaration(input), CommandLoggerBase.getObjectDeclaration(output));
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    protected static void doRemove(INormalRecipe handler, String name, IItemStack output, IIngredient input) {
        if (output == null) {
            MineTweakerAPI.getLogger().logError(String.format("%s: Output item must not be null!", name));
            return;
        }
        Object iInput = null;
        if (input == null) {
            boolean found = false;
            for(ItemStack test : handler.getOreList().values()) {
                if (areEqual(test, toStack(output))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                for(ItemStack test : handler.getMetaList().values()) {
                    if (areEqual(test, toStack(output))) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                MineTweakerAPI.getLogger().logError(String.format("%s: No recipe for %s found.", name, output.toString()));
                return;
            }
        } else {
            iInput = toObject(input);
            if (iInput == null) {
                MineTweakerAPI.getLogger().logError(String.format("%s: Input item invalid!", name));
                return;
            }
            if (iInput instanceof String) {
                ItemStack test = handler.getOreList().get(iInput);
                if (test == null || !ItemStack.areItemStacksEqual(test, toStack(output))) {
                    MineTweakerAPI.getLogger().logError(String.format("%s: No recipe for %s to %s found.", name, input.toString(), output.toString()));
                    return;
                }
            } else if (iInput instanceof ItemStack) {
                ItemStack aInput = getActualItemStack(handler, (ItemStack) iInput);
                ItemStack test = handler.getMetaList().get(aInput);
                if (aInput == null || test == null || !ItemStack.areItemStacksEqual(test, toStack(output))) {
                    MineTweakerAPI.getLogger().logError(String.format("%s: No recipe for %s to %s found.", name, input.toString(), output.toString()));
                    return;
                }
            }
        }

        MineTweakerAPI.apply(new NormalRecipeRemove(handler, name, output, iInput));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private static class NormalRecipeRemove implements IUndoableAction {
        private final INormalRecipe handler;
        private final String name;
        private final Object input;
        private final ItemStack output;
        private final HashMap<Object, ItemStack> backup = new HashMap<>();
        private boolean applied = false;

        public NormalRecipeRemove(INormalRecipe handler, String name, IItemStack output, Object input) {
            this.handler = handler;
            this.name = name;
            this.input = input;
            this.output = toStack(output);
        }

        @Override
        public void apply() {
            if (!applied) {
                if (input == null) {
                    backup.clear();
                    ArrayList<Object> removes = new ArrayList<>();
                    for(Map.Entry<String, ItemStack> test : handler.getOreList().entrySet()) {
                        if (areEqual(test.getValue(), output)) {
                            backup.put(test.getKey(), test.getValue());
                            removes.add(test.getKey());
                        }
                    }
                    if (removes.size() > 0) {
                        for (Object r : removes)
                            handler.getOreList().remove(r);
                        removes.clear();
                    }
                    for(Map.Entry<ItemStack, ItemStack> test : handler.getMetaList().entrySet()) {
                        if (areEqual(test.getValue(), output)) {
                            backup.put(test.getKey(), test.getValue());
                            removes.add(test.getKey());
                        }
                    }
                    if (removes.size() > 0) {
                        for (Object r : removes)
                            handler.getMetaList().remove(r);
                    }
                    applied = true;
                } else if (input instanceof String) {
                    handler.getOreList().remove(input);
                    applied = true;
                } else if (input instanceof ItemStack){
                    ItemStack temp = getActualItemStack(handler, (ItemStack) input);
                    if (temp == null) {
                        MineTweakerAPI.getLogger().logError(String.format("%s: Unexpectedly could not find recipe when removing recipe!", name));
                        return;
                    }
                    handler.getMetaList().remove(temp);
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
                if (input == null) {
                    for(Map.Entry<Object, ItemStack> test : backup.entrySet()) {
                        Object inp = test.getKey();
                        if (inp instanceof String) {
                            handler.add((String) inp, test.getValue());
                        } else if (inp instanceof ItemStack){
                            handler.add((ItemStack) inp, test.getValue());
                        }
                    }
                    applied = false;
                } else if (input instanceof String) {
                    handler.add((String) input, output);
                    applied = false;
                } else if (input instanceof ItemStack){
                    handler.add((ItemStack) input, output);
                    applied = false;
                }
            }
        }

        @Override
        public String describe() {
            if (input == null)
                return String.format("%s: Removing recipes for %s", name, CommandLoggerBase.getObjectDeclaration(output));
            else
                return String.format("%s: Removing recipe for %s to %s", name, CommandLoggerBase.getObjectDeclaration(input), CommandLoggerBase.getObjectDeclaration(output));
        }

        @Override
        public String describeUndo() {
            if (input == null)
                return String.format("%s: Restoring recipes for %s", name, CommandLoggerBase.getObjectDeclaration(output));
            else
                return String.format("%s: Restoring recipe for %s to %s", name, CommandLoggerBase.getObjectDeclaration(input), CommandLoggerBase.getObjectDeclaration(output));
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}

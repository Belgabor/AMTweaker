package mods.belgabor.amtweaker.mods.ss2.handlers;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import mods.belgabor.amtweaker.util.CommandLoggerBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import shift.sextiarysector.api.recipe.IFluidRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static mods.belgabor.amtweaker.helpers.InputHelper.toFluid;
import static mods.belgabor.amtweaker.helpers.InputHelper.toObject;
import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;

/**
 * Created by Belgabor on 04.06.2016.
 */
public class FluidRecipe {
    protected static void doAdd(IFluidRecipe handler, String name, ILiquidStack outputLiquid, IItemStack output, IIngredient input) {
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
            if (handler.getOreList().containsKey((String) iInput)) {
                MineTweakerAPI.getLogger().logError(String.format("%s: Input item %s already has a recipe", name, input.toString()));
                return;
            }
        } else if (iInput instanceof ItemStack) {
            if (getActualItemStack(handler, (ItemStack) iInput) != null) {
                MineTweakerAPI.getLogger().logError(String.format("%s: Input item %s already has a recipe", name, input.toString()));
                return;
            }
        }
        if (outputLiquid == null) {
            MineTweakerAPI.getLogger().logError(String.format("%s: Output fluid must not be null!", name));
            return;
        }
        
        MineTweakerAPI.apply(new FluidRecipeAdd(handler, name, output, outputLiquid, iInput));
    }
    
    private static ItemStack getActualItemStack(IFluidRecipe handler, ItemStack item) {
        for (ItemStack sItem : handler.getMetaList().keySet()) {
            if (ItemStack.areItemStacksEqual(item, sItem))
                return sItem;
        }
        return null;
    }
    
    private static class FluidRecipeAdd implements IUndoableAction {
        private final IFluidRecipe handler;
        private final String name;
        private final Object input;
        private final ItemStack output;
        private final FluidStack outputLiquid;
        private boolean applied = false;
        
        public FluidRecipeAdd(IFluidRecipe handler, String name, IItemStack output, ILiquidStack outputLiquid, Object input) {
            this.handler = handler;
            this.name = name;
            this.input = input;
            this.output = toStack(output);
            this.outputLiquid = toFluid(outputLiquid);
        }

        @Override
        public void apply() {
            if (!applied) {
                if (input instanceof String) {
                    handler.add((String) input, output, outputLiquid);
                    applied = true;
                } else if (input instanceof ItemStack){
                    handler.add((ItemStack) input, output, outputLiquid);
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
                    handler.getOreList().remove((String) input);
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

    protected static void doRemove(IFluidRecipe handler, String name, ILiquidStack outputLiquid, IItemStack output, IIngredient input) {
        if (outputLiquid == null) {
            MineTweakerAPI.getLogger().logError(String.format("%s: Output fluid must not be null!", name));
            return;
        }
        Object iInput = null;
        FluidStack iOutputLiquid = toFluid(outputLiquid);
        ItemStack iOutput = toStack(output);
        if (input == null) {
            boolean found = false;
            for(Object[] test : handler.getOreList().values()) {
                if (iOutputLiquid.isFluidEqual((FluidStack) test[1])) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                for(Object[] test : handler.getMetaList().values()) {
                    if (iOutputLiquid.isFluidEqual((FluidStack) test[1])) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                MineTweakerAPI.getLogger().logError(String.format("%s: No recipe for %s found.", name, outputLiquid.toString()));
                return;
            }
        } else {
            iInput = toObject(input);
            if (iInput == null) {
                MineTweakerAPI.getLogger().logError(String.format("%s: Input item invalid!", name));
                return;
            }
            if (iInput instanceof String) {
                Object[] test = handler.getOreList().get((String) iInput);
                if (test == null || !iOutputLiquid.isFluidStackIdentical((FluidStack) test[1]) || !ItemStack.areItemStacksEqual(iOutput, (ItemStack) test[0])) {
                    MineTweakerAPI.getLogger().logError(String.format("%s: No recipe for %s to %s/%s found.", name, input.toString(), outputLiquid.toString(), output==null?"-":output.toString()));
                    return;
                }
            } else if (iInput instanceof ItemStack) {
                ItemStack aInput = getActualItemStack(handler, (ItemStack) iInput);
                Object[] test = handler.getMetaList().get(aInput);
                if (aInput == null || test == null || !iOutputLiquid.isFluidStackIdentical((FluidStack) test[1]) || !ItemStack.areItemStacksEqual(iOutput, (ItemStack) test[0])) {
                    MineTweakerAPI.getLogger().logError(String.format("%s: No recipe for %s to %s/%s found.", name, input.toString(), outputLiquid.toString(), output==null?"-":output.toString()));
                    return;
                }
            }
        }

        MineTweakerAPI.apply(new FluidRecipeRemove(handler, name, iOutputLiquid, output, iInput));
    }

    private static class FluidRecipeRemove implements IUndoableAction {
        private final IFluidRecipe handler;
        private final String name;
        private final Object input;
        private final ItemStack output;
        private final FluidStack outputLiquid;
        private final HashMap<Object, Object[]> backup = new HashMap<>();
        private boolean applied = false;

        public FluidRecipeRemove(IFluidRecipe handler, String name, FluidStack outputLiquid, IItemStack output, Object input) {
            this.handler = handler;
            this.name = name;
            this.input = input;
            this.output = toStack(output);
            this.outputLiquid = outputLiquid;
        }

        @Override
        public void apply() {
            if (!applied) {
                if (input == null) {
                    backup.clear();
                    ArrayList<Object> removes = new ArrayList<>();
                    for(Map.Entry<String, Object[]> test : handler.getOreList().entrySet()) {
                        if (outputLiquid.isFluidEqual((FluidStack) test.getValue()[1])) {
                            backup.put(test.getKey(), test.getValue());
                            removes.add(test.getKey());
                        }
                    }
                    if (removes.size() > 0) {
                        for (Object r : removes)
                            handler.getOreList().remove(r);
                        removes.clear();
                    }
                    for(Map.Entry<ItemStack, Object[]> test : handler.getMetaList().entrySet()) {
                        if (outputLiquid.isFluidEqual((FluidStack) test.getValue()[1])) {
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
                    handler.getOreList().remove((String) input);
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
                    for(Map.Entry<Object, Object[]> test : backup.entrySet()) {
                        Object inp = test.getKey();
                        if (inp instanceof String) {
                            handler.add((String) inp, (ItemStack) test.getValue()[0], (FluidStack) test.getValue()[1]);
                        } else if (inp instanceof ItemStack){
                            handler.add((ItemStack) inp, (ItemStack) test.getValue()[0], (FluidStack) test.getValue()[1]);
                        }
                    }
                    applied = false;
                } else if (input instanceof String) {
                    handler.add((String) input, output, outputLiquid);
                    applied = false;
                } else if (input instanceof ItemStack){
                    handler.add((ItemStack) input, output, outputLiquid);
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

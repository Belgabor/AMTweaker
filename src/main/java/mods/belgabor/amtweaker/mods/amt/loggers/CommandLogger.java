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

package mods.belgabor.amtweaker.mods.amt.loggers;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.player.IPlayer;
import minetweaker.api.server.ICommandFunction;
import mods.defeatedcrow.api.charge.ChargeItemManager;
import mods.defeatedcrow.api.charge.IChargeItem;
import mods.defeatedcrow.api.recipe.*;
import mods.defeatedcrow.recipe.BrewingRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

/**
 * Created by Belgabor on 10.02.2015.
 */
public class CommandLogger implements ICommandFunction {
    public static void register() {
        MineTweakerAPI.server.addMineTweakerCommand("amt", new String[] {
                "/minetweaker amt charge",
                "    list charge items",
                "/minetweaker amt choco",
                "    list chocolate recipes",
                "/minetweaker amt heat",
                "    list clay pan and iron plate heat sources",
                "/minetweaker amt recipes",
                "    list all AMT specific recipes to the minetweaker log",
                "/minetweaker amt slag",
                "    list slag loot"
        }, new CommandLogger());
    }

    private void logBoth(IPlayer player, String s) {
        MineTweakerAPI.logCommand(s);
        if (player != null)
            player.sendChat(s);
    }

    private void logBarrelRecipes(IPlayer player) {
        for (Map.Entry<Fluid, Fluid> entry: BrewingRecipe.instance.recipeMap().entrySet()) {
            if (player != null)
                player.sendChat(getItemDeclaration(entry.getKey()) + " ---> " + getItemDeclaration(entry.getValue()));
            MineTweakerAPI.logCommand("mods.amt.Pan.addChocolateRecipe(" + getItemDeclaration(entry.getValue()) + ", " + getItemDeclaration(entry.getKey()) + ");");
        }
    }

    private void logChocolateRecipes(IPlayer player) {
        for (Map.Entry<Object, ItemStack> entry: RecipeRegisterManager.chocoRecipe.getRecipeList().entrySet()) {
            String input = getObjectDeclaration(entry.getKey());
            if (player != null)
                player.sendChat(input + " ---> " + getItemDeclaration(entry.getValue()));
            MineTweakerAPI.logCommand("mods.amt.Pan.addChocolateRecipe(" + getItemDeclaration(entry.getValue()) + ", " + input + ");");
        }
    }

    private void logEvaporatorRecipes() {
        for (IEvaporatorRecipe recipe: RecipeRegisterManager.evaporatorRecipe.getRecipeList()) {
            ItemStack input = recipe.getInput();
            ItemStack output = recipe.getOutput();
            FluidStack secondary = recipe.getSecondary();
            String o = "mods.amt.Evaporator.addRecipe(";
            if (output != null)
                o += getItemDeclaration(output) + ", ";
            if (secondary != null)
                o += getItemDeclaration(secondary) + ", ";
            o += getItemDeclaration(input);
            if (!recipe.returnContainer())
                o += ", false";
            o += ");";
            MineTweakerAPI.logCommand(o);
        }
    }

    private void logIceRecipes() {
        for (IIceRecipe recipe: RecipeRegisterManager.iceRecipe.getRecipeList()) {
            String output = "mods.amt.IceMaker.addRecipe(" + getItemDeclaration(recipe.getOutput()) + ", " + getItemDeclaration(recipe.getInput());
            ItemStack container = recipe.getContainer();
            if (container != null)
                output += ", " + getItemDeclaration(container);
            output += ");";
            MineTweakerAPI.logCommand(output);
        }
    }

    private void logPanRecipes() {
        for (IPanRecipe recipe: RecipeRegisterManager.panRecipe.getRecipeList()) {
            ItemStack jpoutput = recipe.getOutputJP();
            String o = "mods.amt.Pan.addRecipe(" + getItemDeclaration(recipe.getOutput());
            if (jpoutput != null) {
                o += ", " + getItemDeclaration(jpoutput);
            }
            o += ", " + getItemDeclaration(recipe.getInput()) + ", \"" + recipe.getTex() + "\", \"" + recipe.getDisplayName() + "\");";
            MineTweakerAPI.logCommand(o);
        }
    }

    private void logPlateRecipes() {
        for (IPlateRecipe recipe: RecipeRegisterManager.plateRecipe.getRecipeList()) {
            String o = "mods.amt.Plate.addRecipe(" + getItemDeclaration(recipe.getOutput()) + ", " + getItemDeclaration(recipe.getInput()) + ", " + recipe.cookingTime() + ", ";
            if (recipe.useOvenRecipe()) {
                o += "true";
            } else {
                o += "false";
            }
            MineTweakerAPI.logCommand(o + ");");
        }
    }

    private void logProcessorRecipes() {
        for (IProcessorRecipe recipe: RecipeRegisterManager.processorRecipe.getRecipes()) {
            String o = "mods.amt.Processor.addRecipe(" + getItemDeclaration(recipe.getOutput());
            float sec = recipe.getChance();
            boolean ret = recipe.forceReturnContainer();
            if ((recipe.getSecondary() != null) | (sec != 0) | ret) {
                o += ", " + getItemDeclaration(recipe.getSecondary());
            }
            o += ", [";
            boolean first = true;
            for (Object x: recipe.getInput()) {
                if (!first) {
                    o += ", ";
                } else {
                    first = false;
                }
                o += getObjectDeclaration(x);
            }
            o += "], ";
            if (recipe.isFoodRecipe()) {
                o += "true";
            } else {
                o += "false";
            }
            if ((sec != 0) | ret) {
                o += ", " + sec;
            }
            if (ret) {
                o += ", true";
            }
            MineTweakerAPI.logCommand(o + ");");
        }
    }

    private void logTeaRecipes() {
        for (ITeaRecipe recipe: RecipeRegisterManager.teaRecipe.getRecipeList()) {
            String o = "mods.amt.Processor.addRecipe(" + getItemDeclaration(recipe.getOutput());
            ItemStack milk = recipe.getOutputMilk();
            if (milk != null) {
                o += ", " + getItemDeclaration(milk);
            }
            o += ", " + getItemDeclaration(recipe.getInput()) + ", \"" + recipe.getTex() + "\"";
            if ((milk != null) && (recipe.getTex() != recipe.getMilkTex())) {
                o += ", \"" + recipe.getMilkTex() + "\"";
            }
            MineTweakerAPI.logCommand(o + ");");
        }
    }

    private String getItemDeclaration(ItemStack stack) {
        if (stack == null) {
            return "null";
        }
        return MineTweakerMC.getIItemStack(stack).toString();
    }
    private String getItemDeclaration(Fluid stack) {
        return "<liquid:" + stack.getName() + ">";
    }
    private String getItemDeclaration(FluidStack stack) {
        return "<liquid:" + stack.getFluid().getName() + "> * " + stack.amount;
    }
    private String getObjectDeclaration(Object stack) {
        if (stack instanceof String) {
            return "<ore:" + (String) stack + ">";
        } else if (stack instanceof ItemStack) {
            return getItemDeclaration((ItemStack) stack);
        } else if (stack instanceof Item) {
            return getItemDeclaration(new ItemStack((Item) stack, 1));
        } else if (stack instanceof Block) {
            return getItemDeclaration(new ItemStack((Block) stack, 1));
        } else if (stack instanceof Fluid) {
            return getItemDeclaration((Fluid) stack);
        } else if (stack instanceof FluidStack) {
            return getItemDeclaration((FluidStack) stack);
        } else if (stack == null) {
            return "null";
        } else {
            return "?????";
        }
    }

    @Override
    public void execute(String[] arguments, IPlayer player) {
        if (arguments.length > 0) {
            if (arguments[0].equalsIgnoreCase("charge")) {
                logBoth(player, "Battery:");
                for (IChargeItem i : ChargeItemManager.chargeItem.getChargeItemList()) {
                    String s = getItemDeclaration(i.getItem());
                    if (i.returnItem() != null) {
                        s += " --> " + getItemDeclaration(i.returnItem());
                    }
                    s += " --- " + i.getItem().getDisplayName() + " (" + i.chargeAmount() + ")";
                    logBoth(player, s);
                }
                logBoth(player, "Ice Maker:");
                for (IChargeIce i : RecipeRegisterManager.iceRecipe.getChargeItemList()) {
                    logBoth(player, getItemDeclaration(i.getItem()) + " --- " + i.getItem().getDisplayName() + " (" + i.chargeAmount() + ")");
                }
            } else if (arguments[0].equalsIgnoreCase("choco")) {
                logBoth(player, "Chocolate Recipes:");
                logChocolateRecipes(player);
                /*
                for (Map.Entry<Object, ItemStack> entry: RecipeRegisterManager.chocoRecipe.getRecipeList().entrySet()) {
                    String input = "";
                    if (entry.getKey() instanceof String) {
                        input = "<ore:" + (String) entry.getKey() + ">";
                    } else {
                        input = getItemDeclaration((ItemStack) entry.getKey());
                    }
                    player.sendChat(input + " ---> " + getItemDeclaration(entry.getValue()));
                    MineTweakerAPI.logCommand("mods.amt.Pan.addChocolateRecipe(" + getItemDeclaration(entry.getValue()) + ", " + input + ");");
                }
                */
            } else if (arguments[0].equalsIgnoreCase("heat")) {
                logBoth(player, "Pan:");
                for (ItemStack i: RecipeRegisterManager.panRecipe.getHeatSourceList()) {
                    logBoth(player, getItemDeclaration(i) + " -- " + i.getDisplayName());
                }
                logBoth(player, "Plate:");
                for (ItemStack i: RecipeRegisterManager.plateRecipe.getHeatSourceList()) {
                    logBoth(player, getItemDeclaration(i) + " -- " + i.getDisplayName());
                }
            } else if (arguments[0].equalsIgnoreCase("recipes")) {
                MineTweakerAPI.logCommand("Barrel brewing recipes:");
                logBarrelRecipes(null);
                MineTweakerAPI.logCommand("Chocolate fondue recipes:");
                logChocolateRecipes(null);
                MineTweakerAPI.logCommand("Evaporator recipes:");
                logEvaporatorRecipes();
                MineTweakerAPI.logCommand("Ice Maker recipes:");
                logIceRecipes();
                MineTweakerAPI.logCommand("Pan recipes:");
                logPanRecipes();
                MineTweakerAPI.logCommand("Plate recipes:");
                logPlateRecipes();
                MineTweakerAPI.logCommand("Processor recipes:");
                logProcessorRecipes();
                MineTweakerAPI.logCommand("Teamaker recipes:");
                logTeaRecipes();
                player.sendChat("AMT recipes have been written to the MineTweaker log.");
            } else if (arguments[0].equalsIgnoreCase("slag")) {
                for (int l=1; l <= 5; l++) {
                    logBoth(player, "Tier " + l + ":");
                    for (ItemStack i: RecipeRegisterManager.slagLoot.getLootList(l)) {
                        logBoth(player, getItemDeclaration(i) + " -- " + i.getDisplayName());
                    }
                }
            } else {
                player.sendChat("Unknown subcommand: "+arguments[0]);
            }
        } else {
            player.sendChat("Please use a subcommand.");
        }

    }
}

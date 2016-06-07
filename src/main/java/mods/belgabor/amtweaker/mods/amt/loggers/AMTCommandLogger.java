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

import com.google.common.base.Joiner;
import minetweaker.MineTweakerAPI;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.player.IPlayer;
import minetweaker.api.server.ICommandFunction;
import mods.belgabor.amtweaker.util.CommandLoggerBase;
import mods.defeatedcrow.api.appliance.SoupType;
import mods.defeatedcrow.api.charge.ChargeItemManager;
import mods.defeatedcrow.api.charge.IChargeItem;
import mods.defeatedcrow.api.recipe.*;
import mods.defeatedcrow.recipe.BrewingRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Belgabor on 10.02.2015.
 */
public class AMTCommandLogger extends CommandLoggerBase implements ICommandFunction {
    public static void register() {
        MineTweakerAPI.server.addMineTweakerCommand("amt", new String[] {
                "/minetweaker amt charge",
                "    list charge items",
                "/minetweaker amt fondue",
                "    list fondue recipes",
                "/minetweaker amt soup",
                "    list fondue soup types",
                "/minetweaker amt source",
                "    list fondue sources (for reference)",
                "/minetweaker amt heat",
                "    list clay pan and iron plate heat sources",
                "/minetweaker amt recipes",
                "    list all AMT specific recipes to the minetweaker log",
                "/minetweaker amt slag",
                "    list slag loot"
        }, new AMTCommandLogger());
    }

    private void logBarrelRecipes(IPlayer player) {
        for (Map.Entry<Fluid, Fluid> entry: BrewingRecipe.instance.recipeMap().entrySet()) {
            if (player != null)
                player.sendChat(getItemDeclaration(entry.getKey()) + " ---> " + getItemDeclaration(entry.getValue()));
            MineTweakerAPI.logCommand("mods.amt.Barrel.addRecipe(" + getItemDeclaration(entry.getValue()) + ", " + getItemDeclaration(entry.getKey()) + ");");
        }
    }

    private void logFondueRecipes(IPlayer player) {
        for (IFondueRecipe entry: RecipeRegisterManager.fondueRecipe.getRecipeList()) {
            String input = getObjectDeclaration(entry.getInput());
            if (player != null)
                player.sendChat(String.format("%s ---(%d, %s)---> %s", input, entry.getType().id, entry.getType().display, getItemDeclaration(entry.getOutput())));
            MineTweakerAPI.logCommand(String.format("mods.amt.Pan.addFondueRecipe(%s, %s, %d);", getItemDeclaration(entry.getOutput()), input, entry.getType().id));
        }
    }

    private void logEvaporatorRecipes() {
        for (IEvaporatorRecipe recipe: RecipeRegisterManager.evaporatorRecipe.getRecipeList()) {
            ItemStack input = recipe.getInput();
            ItemStack output = recipe.getOutput();
            FluidStack secondary = recipe.getSecondary();
            String o = "mods.amt.Evaporator.addRecipe(";
            if (output != null)
                o += getFullObjectDeclaration(output) + ", ";
            if (secondary != null)
                o += getItemDeclaration(secondary) + ", ";
            o += getFullObjectDeclaration(input);
            if (!recipe.returnContainer())
                o += ", false";
            o += ");";
            MineTweakerAPI.logCommand(o);
        }
    }

    private void logIceRecipes() {
        for (IIceRecipe recipe: RecipeRegisterManager.iceRecipe.getRecipeList()) {
            String output = "mods.amt.IceMaker.addRecipe(" + getFullObjectDeclaration(recipe.getOutput()) + ", " + getItemDeclaration(recipe.getInput());
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
            ArrayList<String> inputs = new ArrayList<>();
            for (Object x: recipe.getInput())
                inputs.add(getObjectDeclaration(x));
            
            String o = String.format("mods.amt.Processor.addRecipe(%s, %s, [%s], %s, %f, %s, %d);", 
                    getFullObjectDeclaration(recipe.getOutput()), getFullObjectDeclaration(recipe.getSecondary()),
                    Joiner.on(", ").join(inputs), getBoolean(recipe.isFoodRecipe()), recipe.getChance(),
                    getBoolean(recipe.forceReturnContainer()), recipe.getRecipeTier());
            MineTweakerAPI.logCommand(o);
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
            if ((milk != null) && (!recipe.getTex().equals(recipe.getMilkTex()))) {
                o += ", \"" + recipe.getMilkTex() + "\"";
            }
            MineTweakerAPI.logCommand(o + ");");
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
            } else if (arguments[0].equalsIgnoreCase("fondue")) {
                logBoth(player, "Fondue Recipes:");
                logFondueRecipes(player);
            } else if (arguments[0].equalsIgnoreCase("soup")) {
                logBoth(player, "Fondue Soup Types:");
                for (SoupType s: SoupType.types) {
                    logBoth(player, String.format("%d: %s", s.id, s.display));
                }
            } else if (arguments[0].equalsIgnoreCase("source")) {
                logBoth(player, "Fondue Sources:");
                for (IFondueSource s: RecipeRegisterManager.fondueRecipe.getSourceList()) {
                    logBoth(player, String.format("%s (%d) --- %s ---> %s (%d)", s.beforeType().display, s.beforeType().id, getObjectDeclaration(s.getInput()), s.afterType().display, s.afterType().id));
                }
            } else if (arguments[0].equalsIgnoreCase("heat")) {
                logBoth(player, "Pan:");
                for (ICookingHeatSource i: RecipeRegisterManager.panRecipe.getHeatSourcesList()) {
                    logBoth(player, getItemDeclaration(new ItemStack(i.getBlock(), 1, i.getMetadata())) + " -- " + i.getBlock().getLocalizedName());
                }
                logBoth(player, "Plate:");
                for (ICookingHeatSource i: RecipeRegisterManager.plateRecipe.getHeatSourcesList()) {
                    logBoth(player, getItemDeclaration(new ItemStack(i.getBlock(), 1, i.getMetadata())) + " -- " + i.getBlock().getLocalizedName());
                }
            } else if (arguments[0].equalsIgnoreCase("recipes")) {
                MineTweakerAPI.logCommand("Barrel brewing recipes:");
                logBarrelRecipes(null);
                MineTweakerAPI.logCommand("Fondue recipes:");
                logFondueRecipes(null);
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

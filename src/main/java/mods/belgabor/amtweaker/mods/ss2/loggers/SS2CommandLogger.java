package mods.belgabor.amtweaker.mods.ss2.loggers;

import com.google.common.base.Joiner;
import minetweaker.MineTweakerAPI;
import minetweaker.api.player.IPlayer;
import minetweaker.api.server.ICommandFunction;
import mods.belgabor.amtweaker.mods.mce.MCE;
import mods.belgabor.amtweaker.mods.vanilla.util.VanillaAccessHelper;
import mods.belgabor.amtweaker.util.CommandLoggerBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import shift.sextiarysector.SSRecipes;
import shift.sextiarysector.api.recipe.IFluidRecipe;
import shift.sextiarysector.api.recipe.INormalRecipe;
import shift.sextiarysector.api.recipe.RecipeAPI;
import shift.sextiarysector.recipe.FurnaceCraftingManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Belgabor on 27.05.2016.
 */
public class SS2CommandLogger extends CommandLoggerBase implements ICommandFunction {
    public static void register() {
        if (MCE.available) {
            MineTweakerAPI.server.addMineTweakerCommand("ss2", new String[] {
                    "/minetweaker ss2 fuels",
                    "    list Sextiary Sector 2 special fuel items",
                    "/minetweaker ss2 recipes",
                    "    list Sextiary Sector 2 recipes to minetwekaer log"
            }, new SS2CommandLogger());
        }
    }

    private void logFuels(IPlayer player) {
        logBoth(player, "Freezer (Ice Fuel):");
        for (Map.Entry<ItemStack, Integer> item : SSRecipes.iceFuel.getMetaList().entrySet()) {
            player.sendChat(String.format("%s - %d", getItemDeclaration(item.getKey()), item.getValue()));
            MineTweakerAPI.logCommand(String.format("mods.ss2.Fuel.addIce(%s, %d);", getItemDeclaration(item.getKey()), item.getValue()));
        }
        for (Map.Entry<String, Integer> item : SSRecipes.iceFuel.getOreList().entrySet()) {
            player.sendChat(String.format("%s - %d", getObjectDeclaration(item.getKey()), item.getValue()));
            MineTweakerAPI.logCommand(String.format("mods.ss2.Fuel.addIce(%s, %d);", getObjectDeclaration(item.getKey()), item.getValue()));
        }
        logBoth(player, "Magic Furnace (Magic Fuel):");
        for (Map.Entry<ItemStack, Integer> item : SSRecipes.magicFuel.getMetaList().entrySet()) {
            player.sendChat(String.format("%s - %d", getItemDeclaration(item.getKey()), item.getValue()));
            MineTweakerAPI.logCommand(String.format("mods.ss2.Fuel.addMagic(%s, %d);", getItemDeclaration(item.getKey()), item.getValue()));
        }
        for (Map.Entry<String, Integer> item : SSRecipes.magicFuel.getOreList().entrySet()) {
            player.sendChat(String.format("%s - %d", getObjectDeclaration(item.getKey()), item.getValue()));
            MineTweakerAPI.logCommand(String.format("mods.ss2.Fuel.addMagic(%s, %d);", getObjectDeclaration(item.getKey()), item.getValue()));
        }
    }
    
    private void logNormalRecipeList(IPlayer player, INormalRecipe handler, String name) {
        logBoth(null, name + ":");
        for (Map.Entry<ItemStack, ItemStack> item : handler.getMetaList().entrySet()) {
            //player.sendChat(String.format("  %s -> %s", getItemDeclaration(item.getKey()), getFullObjectDeclaration(item.getValue())));
            MineTweakerAPI.logCommand(String.format("mods.ss2.%s.add(%s, %s);", name, getFullObjectDeclaration(item.getValue()), getItemDeclaration(item.getKey())));
        }
        for (Map.Entry<String, ItemStack> item : handler.getOreList().entrySet()) {
            //player.sendChat(String.format("  %s -> %s", getObjectDeclaration(item.getKey()), getFullObjectDeclaration(item.getValue())));
            MineTweakerAPI.logCommand(String.format("mods.ss2.%s.add(%s, %s);", name, getFullObjectDeclaration(item.getValue()), getObjectDeclaration(item.getKey())));
        }
    }

    private void logFluidRecipeList(IPlayer player, IFluidRecipe handler, String name) {
        logBoth(null, name + ":");
        for (Map.Entry<ItemStack, Object[]> item : handler.getMetaList().entrySet()) {
            ItemStack out1 = (ItemStack) item.getValue()[0];
            FluidStack out2 = (FluidStack) item.getValue()[1]; 
            //player.sendChat(String.format("  %s -> %s + %s", getItemDeclaration(item.getKey()), getFullObjectDeclaration(out2), getFullObjectDeclaration(out1)));
            MineTweakerAPI.logCommand(String.format("mods.ss2.%s.add(%s, %s, %s);", name, getFullObjectDeclaration(out2), getFullObjectDeclaration(out1), getItemDeclaration(item.getKey())));
        }
        for (Map.Entry<String, Object[]> item : handler.getOreList().entrySet()) {
            ItemStack out1 = (ItemStack) item.getValue()[0];
            FluidStack out2 = (FluidStack) item.getValue()[1];
            //player.sendChat(String.format("  %s -> %s + %s", getObjectDeclaration(item.getKey()), getFullObjectDeclaration(out2), getFullObjectDeclaration(out1)));
            MineTweakerAPI.logCommand(String.format("mods.ss2.%s.add(%s, %s, %s);", name, getFullObjectDeclaration(out2), getFullObjectDeclaration(out1), getObjectDeclaration(item.getKey())));
        }
    }
    
    private void logFurnaceRecipes() {
        MineTweakerAPI.logCommand("Large Furnace:");
        for (Object xRecipe: FurnaceCraftingManager.getInstance().getRecipeList()) {
            if (xRecipe instanceof ShapelessRecipes) {
                ShapelessRecipes recipe = (ShapelessRecipes) xRecipe;
                ArrayList<String> inputs = new ArrayList<>();
                for (Object item : recipe.recipeItems)
                    inputs.add(getObjectDeclaration(item));
                MineTweakerAPI.logCommand(
                        String.format("mods.ss2.LargeFurnace.addShapeless(%s, [%s]);", getFullObjectDeclaration(recipe.getRecipeOutput()), Joiner.on(", ").join(inputs))
                );
            } else if (xRecipe instanceof ShapedRecipes) {
                MineTweakerAPI.logCommand("ShapedRecipes skipped (there shouldn't be any)");
            } else if (xRecipe instanceof ShapelessOreRecipe) {
                ShapelessOreRecipe recipe = (ShapelessOreRecipe) xRecipe;
                ArrayList<String> inputs = recipe.getInput().stream().map(CommandLoggerBase::getObjectDeclaration).collect(Collectors.toCollection(ArrayList::new));
                MineTweakerAPI.logCommand(
                        String.format("mods.ss2.LargeFurnace.addShapeless(%s, [%s]);", getFullObjectDeclaration(recipe.getRecipeOutput()), Joiner.on(", ").join(inputs))
                );
            } else if (xRecipe instanceof ShapedOreRecipe) {
                ShapedOreRecipe recipe = (ShapedOreRecipe) xRecipe;
                if (VanillaAccessHelper.available) {
                    int h = VanillaAccessHelper.getHeight(recipe);
                    int w = VanillaAccessHelper.getWidth(recipe);
                    Object[] items = recipe.getInput();
                    
                    ArrayList<String> outer = new ArrayList<>();
                    for (int ih = 0; ih < h; ih++) {
                        ArrayList<String> inner = new ArrayList<>();
                        for (int iw = 0; iw < w; iw++) {
                            inner.add(getObjectDeclaration(items[iw + (ih * w)]));
                        }
                        outer.add("[" + Joiner.on(", ").join(inner) + "]");
                    }
                    MineTweakerAPI.logCommand(
                            String.format("mods.ss2.LargeFurnace.addShaped(%s, [%s]);", getFullObjectDeclaration(recipe.getRecipeOutput()), Joiner.on(", ").join(outer))
                    );
                } else {
                    MineTweakerAPI.logCommand("ShapedOreRecipe skipped (no access)");
                }
            } else {
                MineTweakerAPI.logCommand("Unknown entry of class " + xRecipe.getClass().getCanonicalName());
            }
        }
    }


    private void logRecipes(IPlayer player) {
        logFluidRecipeList(player, RecipeAPI.extractor, "Extractor");
        logFluidRecipeList(player, SSRecipes.fluidFurnace, "FluidFurnace");
        logFluidRecipeList(player, SSRecipes.foodSmokers, "FoodSmokers");
        logNormalRecipeList(player, SSRecipes.freezer, "Freezer");
        logNormalRecipeList(player, RecipeAPI.loom, "Loom");
        logNormalRecipeList(player, SSRecipes.magicFurnace, "MagicFurnace");
        logFluidRecipeList(player, RecipeAPI.manaSqueezer, "ManaSqueezer");
        logNormalRecipeList(player, RecipeAPI.millstone, "Millstone");
        logNormalRecipeList(player, RecipeAPI.pulverizer, "Pulverizer");
        logNormalRecipeList(player, RecipeAPI.rollingMachine, "RollingMachine");
        logNormalRecipeList(player, RecipeAPI.sawmill, "Sawmill");
        logNormalRecipeList(player, RecipeAPI.spinning_machine, "SpinningMachine");
        logNormalRecipeList(player, RecipeAPI.timeMachine, "TimeMachine");
        logFurnaceRecipes();
        player.sendChat("SS2 recipes have been written to the MineTweaker log.");
    }

    @Override
    public void execute(String[] arguments, IPlayer player) {
        if (arguments.length > 0) {
            if (arguments[0].equalsIgnoreCase("fuels")) {
                logFuels(player);
            } else if (arguments[0].equalsIgnoreCase("recipes")) {
                logRecipes(player);
            } else {
                player.sendChat("Unknown subcommand: "+arguments[0]);
            }
        } else {
            player.sendChat("Please use a subcommand.");
        }

    }
}

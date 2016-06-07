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

package mods.belgabor.amtweaker.mods.emt.loggers;

import defeatedcrow.addonforamt.economy.api.RecipeManagerEMT;
import defeatedcrow.addonforamt.economy.api.energy.IFuelFluid;
import defeatedcrow.addonforamt.economy.api.order.IOrder;
import minetweaker.MineTweakerAPI;
import minetweaker.api.player.IPlayer;
import minetweaker.api.server.ICommandFunction;
import mods.belgabor.amtweaker.mods.emt.configuration.EMTConfiguration;
import mods.belgabor.amtweaker.mods.emt.configuration.OrderData;
import mods.belgabor.amtweaker.util.CommandLoggerBase;

import java.util.List;

/**
 * Created by Belgabor on 10.02.2015.
 */
public class EMTCommandLogger extends CommandLoggerBase implements ICommandFunction {
    public static void register() {
        MineTweakerAPI.server.addMineTweakerCommand("emt", new String[] {
                "/minetweaker emt fuel",
                "    list all valid fuels for the diesel generator",
                "/minetweaker emt order",
                "    list all orders",
                "/minetweaker emt orderdump",
                "    dump orders to emt-dump.json"
        }, new EMTCommandLogger());
    }

    private void logFluidFuels(IPlayer player) {
        for (IFuelFluid entry: RecipeManagerEMT.fuelRegister.getRecipes()) {
            if (player != null)
                player.sendChat(getItemDeclaration(entry.getInput()) + " : " + Integer.toString(entry.getGenerateAmount()));
            MineTweakerAPI.logCommand(String.format("mods.amt.Barrel.addRecipe(%s, %d);", getItemDeclaration(entry.getInput()), entry.getGenerateAmount()));
        }
    }


    private void logOrderList(IPlayer player, List<? extends IOrder> list) {
        for(IOrder order : list) {
            player.sendChat(String.format("%d) %s * %d, %d MP, Biome type: %s, Season: %s, '%s'", order.getID(), getObjectDeclaration(order.getRequest()), order.getRequestNum(),
                order.getReward(), OrderData.getBiomeTypeName(order.getBiome()), OrderData.getSeasonName(order.getSeason()), order.getName()));
            MineTweakerAPI.logCommand(String.format("%d;%s;%d;%d;%s;%s;%s;\"%s\"", order.getID(), getObjectDeclaration(order.getRequest()), order.getRequestNum(),
                    order.getReward(), OrderData.getTypeName(order.getType()), OrderData.getSeasonName(order.getSeason()), OrderData.getBiomeTypeName(order.getBiome()), order.getName()));
        }
    }

    private void logOrders(IPlayer player) {
        logBoth(player, "Single Orders:");
        logOrderList(player, RecipeManagerEMT.orderRegister.getSingleOrders());
        logBoth(player, "Short Orders:");
        logOrderList(player, RecipeManagerEMT.orderRegister.getShortOrders());
        logBoth(player, "Middle Orders:");
        logOrderList(player, RecipeManagerEMT.orderRegister.getMiddleOrders());
        logBoth(player, "Long Orders:");
        logOrderList(player, RecipeManagerEMT.orderRegister.getLongOrders());
    }


    @Override
    public void execute(String[] arguments, IPlayer player) {
        if (arguments.length > 0) {
            if (arguments[0].equalsIgnoreCase("fuel")) {
                logFluidFuels(player);
            } else if (arguments[0].equalsIgnoreCase("order")) {
                logOrders(player);
            } else if (arguments[0].equalsIgnoreCase("orderdump")) {
                if (EMTConfiguration.dump()) {
                    player.sendChat("Order dump successful.");
                } else {
                    player.sendChat("Order dump failed. See log for reason.");
                }
            } else {
                player.sendChat("Unknown subcommand: "+arguments[0]);
            }
        } else {
            player.sendChat("Please use a subcommand.");
        }

    }
}

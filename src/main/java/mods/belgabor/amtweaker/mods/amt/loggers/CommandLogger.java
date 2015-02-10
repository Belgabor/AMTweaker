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
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.player.IPlayer;
import minetweaker.api.server.ICommandFunction;
import mods.defeatedcrow.api.charge.ChargeItemManager;
import mods.defeatedcrow.api.charge.IChargeItem;
import mods.defeatedcrow.api.recipe.IChargeIce;
import mods.defeatedcrow.api.recipe.RecipeRegisterManager;
import net.minecraft.item.ItemStack;

/**
 * Created by Belgabor on 10.02.2015.
 */
public class CommandLogger implements ICommandFunction {
    private void logBoth(IPlayer player, String s) {
        MineTweakerAPI.logCommand(s);
        player.sendChat(s);
    }

    private String getItemDeclaration(ItemStack stack) {
        return MineTweakerMC.getIItemStack(stack).toString();
    }

    @Override
    public void execute(String[] arguments, IPlayer player) {
        if (arguments.length > 0) {
            if (arguments[0].equalsIgnoreCase("charge")) {
                logBoth(player, "Battery:");
                for (IChargeItem i: ChargeItemManager.chargeItem.getChargeItemList()) {
                    String s = getItemDeclaration(i.getItem());
                    if (i.returnItem() != null) {
                        s += " --> " + getItemDeclaration(i.returnItem());
                    }
                    s += " --- " + i.getItem().getDisplayName() + " (" + i.chargeAmount() + ")";
                    logBoth(player, s);
                }
                logBoth(player, "Ice Maker:");
                for (IChargeIce i: RecipeRegisterManager.iceRecipe.getChargeItemList()) {
                    logBoth(player, getItemDeclaration(i.getItem()) + " --- " + i.getItem().getDisplayName() + " (" + i.chargeAmount() + ")");
                }
            } else {
                player.sendChat("Unknown subcommand: "+arguments[0]);
            }
        } else {
            player.sendChat("Please use a subcommand.");
        }

    }
}

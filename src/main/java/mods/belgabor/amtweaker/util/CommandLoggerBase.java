package mods.belgabor.amtweaker.util;

import minetweaker.MineTweakerAPI;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.player.IPlayer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Belgabor on 27.05.2016.
 */
public class CommandLoggerBase {
    protected void logBoth(IPlayer player, String s) {
        MineTweakerAPI.logCommand(s);
        if (player != null)
            player.sendChat(s);
    }

    protected String getItemDeclaration(ItemStack stack) {
        if (stack == null) {
            return "null";
        }
        return MineTweakerMC.getIItemStack(stack).toString();
    }
    protected String getItemDeclaration(Fluid stack) {
        return "<liquid:" + stack.getName() + ">";
    }
    protected String getItemDeclaration(FluidStack stack) {
        return "<liquid:" + stack.getFluid().getName() + "> * " + stack.amount;
    }
    protected String getObjectDeclaration(Object stack) {
        if (stack instanceof String) {
            return "<ore:" + stack + ">";
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

}

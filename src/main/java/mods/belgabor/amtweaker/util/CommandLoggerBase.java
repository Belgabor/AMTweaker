package mods.belgabor.amtweaker.util;

import com.google.common.base.Joiner;
import minetweaker.MineTweakerAPI;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.player.IPlayer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Belgabor on 27.05.2016.
 */
public class CommandLoggerBase {
    private static String deduceOre(ArrayList stack) {
        int count = 1;
        ArrayList<Integer> oreRefBase = new ArrayList<>();
        for (Object xItem : stack) {
            if (!(xItem instanceof ItemStack))
                return "?????";
            ItemStack item = (ItemStack) xItem;
            count = item.stackSize;
            int[] oreIds = OreDictionary.getOreIDs(item);
            ArrayList<Integer> oreRef = new ArrayList<>();
            for (int i : oreIds)
                oreRef.add(i);
            if (oreIds.length == 0) {
                return "?????";
            } else if (oreIds.length == 1) {
                return getObjectDeclaration(OreDictionary.getOreName(oreIds[0])) + ((count>1)?String.format(" * %d", count):"");
            } else {
                if (oreRefBase.size() == 0) {
                    oreRefBase = oreRef;
                } else {
                    ArrayList<Integer> toRemove = new ArrayList<>();
                    for (Integer i : oreRefBase) {
                        if (!oreRef.contains(i))
                            toRemove.add(i);
                    }
                    oreRefBase.removeAll(toRemove);
                    if (oreRefBase.size() == 1)
                        return getObjectDeclaration(OreDictionary.getOreName(oreRefBase.get(0))) + ((count>1)?String.format(" * %d", count):"");
                }
            }
        }
        
        // Damn
        ArrayList<String> theOres = oreRefBase.stream().map(OreDictionary::getOreName).collect(Collectors.toCollection(ArrayList::new));
        return getObjectDeclaration("UNKNOWN[" + Joiner.on(",").join(theOres) + "]") + ((count>1)?String.format(" * %d", count):"");
    }
    
    protected void logBoth(IPlayer player, String s) {
        MineTweakerAPI.logCommand(s);
        if (player != null)
            player.sendChat(s);
    }

    protected static String getItemDeclaration(ItemStack stack) {
        if (stack == null) {
            return "null";
        }
        return MineTweakerMC.getIItemStack(stack).toString();
    }
    protected static String getItemDeclaration(Fluid stack) {
        return "<liquid:" + stack.getName() + ">";
    }
    protected static String getItemDeclaration(FluidStack stack) {
        return "<liquid:" + stack.getFluid().getName() + "> * " + stack.amount;
    }
    public static String getObjectDeclaration(Object stack) {
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
        } else if (stack instanceof ArrayList) {
            return deduceOre((ArrayList) stack);
        } else if (stack == null) {
            return "null";
        } else {
            return "?????";
        }
    }
    public static String getFullObjectDeclaration(Object stack) {
        if (stack instanceof ItemStack) {
            ItemStack r = (ItemStack) stack;
            return getItemDeclaration(r) + ((r.stackSize>1)?String.format(" * %d", r.stackSize):"");
        } else if (stack instanceof FluidStack) {
            return getItemDeclaration((FluidStack) stack);
        } else if (stack == null) {
            return "null";
        } else {
            return "?????";
        }
    }

    protected static String getBoolean(boolean b) {
        return b?"true":"false";
    }
}

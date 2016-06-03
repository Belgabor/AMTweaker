package mods.belgabor.amtweaker.mods.vanilla.loggers;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.player.IPlayer;
import minetweaker.api.server.ICommandFunction;
import mods.belgabor.amtweaker.util.CommandLoggerBase;
import net.minecraft.item.*;

import java.util.Set;

import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;

/**
 * Created by Belgabor on 03.06.2016.
 */
public class VanillaCommandLogger extends CommandLoggerBase implements ICommandFunction {
    public static void register() {
        MineTweakerAPI.server.addMineTweakerCommand("handextra", new String[] {
                "/minetweaker mce price",
                "    list MCEconomy2 item prices",
                "/minetweaker handextra",
                "    dump extra information about the held item"
        }, new VanillaCommandLogger());
    }
    
    @Override
    public void execute(String[] arguments, IPlayer player) {
        IItemStack item = player.getCurrentItem();
        if (item != null) {
            ItemStack itemStack = toStack(item); 
            Item theItem = itemStack.getItem();
            logBoth(player, "Item: " + item.toString());
            String s;
            if (theItem.getHasSubtypes()) {
                s = "None (subtypes)";
            } else if (!theItem.isDamageable()) {
                s = "None (not damagable)";
            } else {
                s = Integer.toString(theItem.getMaxDamage());
            }
            logBoth(player, "Durability (max damage): " + s);
            logBoth(player, "Tool harvest levels:");
            Set<String> classes = theItem.getToolClasses(itemStack);
            if (classes.size() > 0) {
                for(String cl : classes) {
                    logBoth(player, String.format("  %s: %d", cl, theItem.getHarvestLevel(itemStack, cl)));
                }
            } else {
                logBoth(player, "  None");
            }
            if (theItem instanceof ItemPickaxe || theItem instanceof ItemSpade || theItem instanceof ItemAxe) {
                logBoth(player, "The item class inherits from one of the primary vanilla tool classes. It is therefore likely locked to its primary function.");
            }
        }
    }
    
}

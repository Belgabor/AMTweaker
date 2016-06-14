package mods.belgabor.amtweaker.mods.ss2.util;

import shift.sextiarysector.block.BlockSandpit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Belgabor on 27.05.2016.
 */
public class SS2AccessHelper {
    public static List<BlockSandpit.ShellEntry> shellList = null;
    private static Field ShellEntry_damage;
    private static Field ShellEntry_enchant;

    public static boolean init() {

        try {
            Class cBlockSandpit = BlockSandpit.class;

            Field f = cBlockSandpit.getDeclaredField("shellList");
            f.setAccessible(true);
            shellList = (ArrayList<BlockSandpit.ShellEntry>) f.get(null);
            
            Class cShellEntry = BlockSandpit.ShellEntry.class;
            ShellEntry_damage = cShellEntry.getDeclaredField("damage");
            ShellEntry_damage.setAccessible(true);
            ShellEntry_enchant = cShellEntry.getDeclaredField("enchant");
            ShellEntry_enchant.setAccessible(true);

        } catch (NoSuchFieldException | SecurityException | IllegalAccessException | ClassCastException e) {
            return false;
        }

        return true;
    }
    
    public static float getShellEntry_damage(BlockSandpit.ShellEntry entry) {
        try {
            return (float) ShellEntry_damage.get(entry);
        } catch (IllegalAccessException | ClassCastException e) {
            return -1;
        }
    }

    public static boolean getShellEntry_enchant(BlockSandpit.ShellEntry entry) {
        try {
            return (boolean) ShellEntry_enchant.get(entry);
        } catch (IllegalAccessException | ClassCastException e) {
            return false;
        }
    }

}

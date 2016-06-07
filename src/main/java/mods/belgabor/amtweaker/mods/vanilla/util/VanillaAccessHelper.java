package mods.belgabor.amtweaker.mods.vanilla.util;

import net.minecraftforge.oredict.ShapedOreRecipe;

import java.lang.reflect.Field;

/**
 * Created by Belgabor on 27.05.2016.
 */
public class VanillaAccessHelper {
    public static Field ShapedOreRecipe_height;
    public static Field ShapedOreRecipe_width;
    
    public static boolean available = false;

    public static boolean init() {

        try {
            Class cShapedOreRecipe = ShapedOreRecipe.class;
            
            ShapedOreRecipe_height = cShapedOreRecipe.getDeclaredField("height");
            ShapedOreRecipe_height.setAccessible(true);
            ShapedOreRecipe_width = cShapedOreRecipe.getDeclaredField("width");
            ShapedOreRecipe_width.setAccessible(true);

            available = true;
            
        } catch (NoSuchFieldException | SecurityException | ClassCastException e) {
            return false;
        }

        return true;
    }

    public static int getHeight(Object recipe) {
        try {
            return (int) ShapedOreRecipe_height.get(recipe);
        } catch (IllegalAccessException | ClassCastException e) {
            return 0;
        }
    }

    public static int getWidth(Object recipe) {
        try {
            return (int) ShapedOreRecipe_width.get(recipe);
        } catch (IllegalAccessException | ClassCastException e) {
            return 0;
        }
    }

}

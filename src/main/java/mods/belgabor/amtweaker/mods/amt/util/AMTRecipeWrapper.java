package mods.belgabor.amtweaker.mods.amt.util;

/**
 * Created by Belgabor on 08.02.2015.
 */
public abstract class AMTRecipeWrapper {
    public abstract void register();
    public abstract boolean matches(Object o);
    public abstract String getRecipeInfo();
}

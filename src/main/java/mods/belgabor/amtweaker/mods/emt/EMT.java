package mods.belgabor.amtweaker.mods.emt;

import minetweaker.MineTweakerAPI;
import mods.belgabor.amtweaker.mods.emt.handlers.Fuel;

/**
 * Created by Belgabor on 25.05.2016.
 */
public class EMT {
    public static final String MODID = "DCsEcoMT";

    public EMT() {
        MineTweakerAPI.registerClass(Fuel.class);
    }
}

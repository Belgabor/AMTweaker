package mods.belgabor.amtweaker.mods.mce;

import minetweaker.MineTweakerAPI;
import mods.belgabor.amtweaker.AMTweaker;
import mods.belgabor.amtweaker.mods.mce.handlers.Price;
import mods.belgabor.amtweaker.mods.mce.util.MCEAccessHelper;
import org.apache.logging.log4j.Level;

/**
 * Created by Belgabor on 27.05.2016.
 */
public class MCE {
    public static final String MODID = "mceconomy2";
    public static boolean available;

    public MCE() {
        available = MCEAccessHelper.init();
        if (available) {
            AMTweaker.log(Level.INFO, "Successfully gained access to MCEconomy2 classes.");
            MineTweakerAPI.registerClass(Price.class);
        } else {
            AMTweaker.log(Level.WARN, "Could not get access to MCEconomy2 classes. Support disabled");
        }
    }
}

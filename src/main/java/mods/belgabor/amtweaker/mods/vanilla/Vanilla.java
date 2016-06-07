package mods.belgabor.amtweaker.mods.vanilla;

import minetweaker.MineTweakerAPI;
import mods.belgabor.amtweaker.AMTweaker;
import mods.belgabor.amtweaker.mods.vanilla.handlers.Durability;
import mods.belgabor.amtweaker.mods.vanilla.handlers.HarvestLevel;
import mods.belgabor.amtweaker.mods.vanilla.util.VanillaAccessHelper;
import org.apache.logging.log4j.Level;


/**
 * Created by Belgabor on 03.06.2016.
 */
public class Vanilla {
    public Vanilla() {
        MineTweakerAPI.registerClass(Durability.class);
        MineTweakerAPI.registerClass(HarvestLevel.class);
        if (VanillaAccessHelper.init()) {
            AMTweaker.log(Level.INFO, "Successfully gained access to Vanilla classes.");
        } else {
            AMTweaker.log(Level.WARN, "Could not get access to Vanilla classes. Some recipe logging will not work.");
        }
    }
}

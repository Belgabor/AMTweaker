package mods.belgabor.amtweaker.mods.vanilla;

import minetweaker.MineTweakerAPI;
import mods.belgabor.amtweaker.mods.vanilla.handlers.Durability;
import mods.belgabor.amtweaker.mods.vanilla.handlers.HarvestLevel;

/**
 * Created by Belgabor on 03.06.2016.
 */
public class Vanilla {
    public Vanilla() {
        MineTweakerAPI.registerClass(Durability.class);
        MineTweakerAPI.registerClass(HarvestLevel.class);
    }
}

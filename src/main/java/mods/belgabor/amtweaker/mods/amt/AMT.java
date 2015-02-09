package mods.belgabor.amtweaker.mods.amt;

import minetweaker.MineTweakerAPI;
import mods.belgabor.amtweaker.mods.amt.handlers.Evaporator;
import mods.belgabor.amtweaker.mods.amt.handlers.Plate;
import mods.belgabor.amtweaker.mods.amt.handlers.Processor;

public class AMT {
    public AMT() {
        MineTweakerAPI.registerClass(Evaporator.class);
        MineTweakerAPI.registerClass(Plate.class);
        MineTweakerAPI.registerClass(Processor.class);
    }
}

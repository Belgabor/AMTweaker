package mods.belgabor.amtweaker.mods.ss2;

import minetweaker.MineTweakerAPI;
import mods.belgabor.amtweaker.mods.ss2.handlers.*;

/**
 * Created by Belgabor on 04.06.2016.
 */
public class SS2 {
    public static final String MODID = "SextiarySector";
    
    public SS2() {
        MineTweakerAPI.registerClass(Extractor.class);
        MineTweakerAPI.registerClass(FluidFurnace.class);
        MineTweakerAPI.registerClass(FoodSmokers.class);
        MineTweakerAPI.registerClass(Freezer.class);
        MineTweakerAPI.registerClass(LargeFurnace.class);
        MineTweakerAPI.registerClass(Loom.class);
        MineTweakerAPI.registerClass(MagicFurnace.class);
        MineTweakerAPI.registerClass(ManaSqueezer.class);
        MineTweakerAPI.registerClass(Millstone.class);
        MineTweakerAPI.registerClass(Pulverizer.class);
        MineTweakerAPI.registerClass(RollingMachine.class);
        MineTweakerAPI.registerClass(Sawmill.class);
        MineTweakerAPI.registerClass(SpinningMachine.class);
        MineTweakerAPI.registerClass(TimeMachine.class);
    }
}

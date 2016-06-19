package mods.belgabor.amtweaker.mods.ss2;

import blusunrize.immersiveengineering.api.tool.ExternalHeaterHandler;
import cpw.mods.fml.common.Loader;
import minetweaker.MineTweakerAPI;
import mods.belgabor.amtweaker.AMTweaker;
import mods.belgabor.amtweaker.mods.ie.IE;
import mods.belgabor.amtweaker.mods.ss2.handlers.*;
import mods.belgabor.amtweaker.mods.ss2.util.SS2AccessHelper;
import mods.belgabor.amtweaker.mods.ss2.xmod.IEHeaterAdapters;
import org.apache.logging.log4j.Level;
import shift.sextiarysector.tileentity.TileEntityFluidFurnace;
import shift.sextiarysector.tileentity.TileEntityFoodSmokers;
import shift.sextiarysector.tileentity.TileEntityLargeFurnace;

/**
 * Created by Belgabor on 04.06.2016.
 */
public class SS2 {
    public static final String MODID = "SextiarySector";
    public static boolean available = false;
    
    public static boolean configIEHeatFurnaces = true;
    public static boolean configIEHeatSmokers = false;
    
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
        MineTweakerAPI.registerClass(Sandpit.class);

        available = SS2AccessHelper.init(); 
        if (available) {
            AMTweaker.log(Level.INFO, "Successfully gained access to SS2 classes.");
        } else {
            AMTweaker.log(Level.WARN, "Could not get access to SS2 classes. Support incomplete.");
        }
        
        if (Loader.isModLoaded(IE.MODID) && (configIEHeatFurnaces || configIEHeatSmokers)) {
            initIEIntegration();
        }
    }

    private void initIEIntegration() {
        if (configIEHeatFurnaces) {
            ExternalHeaterHandler.registerHeatableAdapter(TileEntityFluidFurnace.class, new IEHeaterAdapters.LiquidFurnaceAdapter());
            ExternalHeaterHandler.registerHeatableAdapter(TileEntityLargeFurnace.class, new IEHeaterAdapters.LargeFurnaceAdapter());
        }
        if (configIEHeatSmokers) {
            ExternalHeaterHandler.registerHeatableAdapter(TileEntityFoodSmokers.class, new IEHeaterAdapters.LiquidFurnaceAdapter());
        }
    }
}

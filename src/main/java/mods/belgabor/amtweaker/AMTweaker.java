package mods.belgabor.amtweaker;

import mods.belgabor.amtweaker.mods.amt.AMT;
import mods.belgabor.amtweaker.util.TweakerPlugin;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = AMTweaker.MODID, version = AMTweaker.VERSION, name = "AMTweaker", dependencies = "required-after:MineTweaker3;required-after:DCsAppleMilk;after:ModTweaker;after:AWWayofTime")
public class AMTweaker
{
    public static final String MODID = "AMTweaker";
    public static final String VERSION = "0.1";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        TweakerPlugin.register("DCsAppleMilk", AMT.class);
    }
}

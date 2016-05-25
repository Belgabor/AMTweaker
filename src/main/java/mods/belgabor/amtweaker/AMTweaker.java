package mods.belgabor.amtweaker;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import minetweaker.MineTweakerImplementationAPI;
import minetweaker.util.IEventHandler;
import mods.belgabor.amtweaker.mods.amt.AMT;
import mods.belgabor.amtweaker.mods.amt.loggers.AMTCommandLogger;
import mods.belgabor.amtweaker.util.TweakerPlugin;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = AMTweaker.MODID, version = AMTweaker.VERSION, name = "AMTweaker", dependencies = "required-after:MineTweaker3;required-after:DCsAppleMilk;after:ModTweaker;after:AWWayofTime;after:DCsEcoMT")
public class AMTweaker implements IEventHandler<MineTweakerImplementationAPI.ReloadEvent>
{
    public static final String MODID = "AMTweaker";
    public static final String VERSION = "0.6";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        TweakerPlugin.register(AMT.MODID, AMT.class);
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        AMTCommandLogger.register();
        MineTweakerImplementationAPI.onReloadEvent(this);
    }

    public void handle(MineTweakerImplementationAPI.ReloadEvent event) {
        if (Loader.isModLoaded(AMT.MODID))
            AMTCommandLogger.register();
    }
}

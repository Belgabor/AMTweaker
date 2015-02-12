package mods.belgabor.amtweaker;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import minetweaker.MineTweakerAPI;
import minetweaker.MineTweakerImplementationAPI;
import minetweaker.util.IEventHandler;
import mods.belgabor.amtweaker.mods.amt.AMT;
import mods.belgabor.amtweaker.mods.amt.loggers.CommandLogger;
import mods.belgabor.amtweaker.util.TweakerPlugin;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = AMTweaker.MODID, version = AMTweaker.VERSION, name = "AMTweaker", dependencies = "required-after:MineTweaker3;required-after:DCsAppleMilk;after:ModTweaker;after:AWWayofTime")
public class AMTweaker implements IEventHandler<MineTweakerImplementationAPI.ReloadEvent>
{
    public static final String MODID = "AMTweaker";
    public static final String VERSION = "0.2";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        TweakerPlugin.register("DCsAppleMilk", AMT.class);
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        CommandLogger.register();
        MineTweakerImplementationAPI.onReloadEvent(this);
    }

    public void handle(MineTweakerImplementationAPI.ReloadEvent event) {
        CommandLogger.register();
    }
}

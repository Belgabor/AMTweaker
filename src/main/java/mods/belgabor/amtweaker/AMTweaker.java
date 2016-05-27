package mods.belgabor.amtweaker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import minetweaker.MineTweakerImplementationAPI;
import minetweaker.util.IEventHandler;
import mods.belgabor.amtweaker.mods.amt.AMT;
import mods.belgabor.amtweaker.mods.amt.loggers.AMTCommandLogger;
import mods.belgabor.amtweaker.mods.emt.EMT;
import mods.belgabor.amtweaker.mods.emt.configuration.EMTConfiguration;
import mods.belgabor.amtweaker.mods.emt.loggers.EMTCommandLogger;
import mods.belgabor.amtweaker.util.TweakerPlugin;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = AMTweaker.MODID, version = AMTweaker.VERSION, name = "AMTweaker", dependencies = "required-after:MineTweaker3;required-after:DCsAppleMilk;after:ModTweaker;after:AWWayofTime;after:DCsEcoMT")
public class AMTweaker implements IEventHandler<MineTweakerImplementationAPI.ReloadEvent>
{
    public static final String MODID = "AMTweaker";
    public static final String VERSION = "0.6";

    public static File confDir;
    public static Logger logger;
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void log(Level level, String message, Object ... args) {
        logger.log(level, String.format(message, args));
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        confDir = new File(event.getModConfigurationDirectory(), "AMTweaker/");

        if (Loader.isModLoaded(EMT.MODID)) {
            ensureConfDir();
            EMTConfiguration.read();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        TweakerPlugin.register(AMT.MODID, AMT.class);
        TweakerPlugin.register(EMT.MODID, EMT.class);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (Loader.isModLoaded(EMT.MODID)) {
            EMTConfiguration.apply();
        }
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        registerLoggers();
        MineTweakerImplementationAPI.onReloadEvent(this);
    }

    public void handle(MineTweakerImplementationAPI.ReloadEvent event) {
        registerLoggers();
    }

    private void ensureConfDir() {
        if (!confDir.exists()) {
            confDir.mkdirs();
        }
    }

    private void registerLoggers() {
        if (Loader.isModLoaded(AMT.MODID))
            AMTCommandLogger.register();
        if (Loader.isModLoaded(EMT.MODID))
            EMTCommandLogger.register();
    }
}

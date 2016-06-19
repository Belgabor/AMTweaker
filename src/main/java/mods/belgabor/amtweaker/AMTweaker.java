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
import mods.belgabor.amtweaker.mods.cw2.CW2;
import mods.belgabor.amtweaker.mods.cw2.loggers.CW2CommandLogger;
import mods.belgabor.amtweaker.mods.emt.EMT;
import mods.belgabor.amtweaker.mods.emt.configuration.EMTConfiguration;
import mods.belgabor.amtweaker.mods.emt.loggers.EMTCommandLogger;
import mods.belgabor.amtweaker.mods.mce.MCE;
import mods.belgabor.amtweaker.mods.mce.loggers.MCECommandLogger;
import mods.belgabor.amtweaker.mods.ss2.SS2;
import mods.belgabor.amtweaker.mods.ss2.loggers.SS2CommandLogger;
import mods.belgabor.amtweaker.mods.vanilla.Vanilla;
import mods.belgabor.amtweaker.mods.vanilla.commands.VanillaCommandBlockstats;
import mods.belgabor.amtweaker.mods.vanilla.loggers.VanillaCommandLoggerItem;
import mods.belgabor.amtweaker.util.TweakerPlugin;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = AMTweaker.MODID, version = AMTweaker.VERSION, name = "AMTweaker", dependencies = "required-after:MineTweaker3;after:DCsAppleMilk;after:ModTweaker;after:AWWayofTime;after:DCsEcoMT;after:mceconomy2")
public class AMTweaker implements IEventHandler<MineTweakerImplementationAPI.ReloadEvent>
{
    public static final String MODID = "AMTweaker";
    public static final String VERSION = "1.0";
    
    @Mod.Instance(MODID)
    public static AMTweaker INSTANCE;
    
    public static File confDir;
    public static File logsDir;
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
        logsDir = new File("logs/");
        
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        try {
            cfg.load();
            
            cfg.setCategoryComment(SS2.MODID, "Sextiary Sector 2 specific options");
            
            SS2.configIEHeatFurnaces = cfg.getBoolean("IEHeatFurnaces", SS2.MODID, SS2.configIEHeatFurnaces, 
                    "Allow the Immersive Engineering Furnace Heater to heat the Fluid and Large Furnace");
            SS2.configIEHeatSmokers = cfg.getBoolean("IEHeatSmokers", SS2.MODID, SS2.configIEHeatSmokers,
                    "Allow the Immersive Engineering Furnace Heater to heat the Food Smokers");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cfg.hasChanged())
                cfg.save();
        }

        if (Loader.isModLoaded(EMT.MODID)) {
            ensureConfDir();
            EMTConfiguration.read();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        new Vanilla();
        TweakerPlugin.register(AMT.MODID, AMT.class);
        TweakerPlugin.register(EMT.MODID, EMT.class);
        TweakerPlugin.register(MCE.MODID, MCE.class);
        TweakerPlugin.register(SS2.MODID, SS2.class);
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
        registerCommands();
        registerLoggers();
        MineTweakerImplementationAPI.onReloadEvent(this);
    }

    private void registerCommands() {
        MinecraftServer server = MinecraftServer.getServer();
        ICommandManager command = server.getCommandManager();
        ServerCommandManager manager = (ServerCommandManager) command;
        manager.registerCommand(new VanillaCommandBlockstats());        
    }

    public void handle(MineTweakerImplementationAPI.ReloadEvent event) {
        registerLoggers();
    }

    private void ensureConfDir() {
        if (!confDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            confDir.mkdirs();
        }
    }

    private void registerLoggers() {
        VanillaCommandLoggerItem.register();
        if (Loader.isModLoaded(AMT.MODID))
            AMTCommandLogger.register();
        if (Loader.isModLoaded(CW2.MODID))
            CW2CommandLogger.register();
        if (Loader.isModLoaded(EMT.MODID))
            EMTCommandLogger.register();
        if (Loader.isModLoaded(MCE.MODID))
            MCECommandLogger.register();
        if (Loader.isModLoaded(SS2.MODID))
            SS2CommandLogger.register();
    }
}

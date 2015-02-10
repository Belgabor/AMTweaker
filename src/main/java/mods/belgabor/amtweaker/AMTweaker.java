package mods.belgabor.amtweaker;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import minetweaker.MineTweakerAPI;
import mods.belgabor.amtweaker.mods.amt.AMT;
import mods.belgabor.amtweaker.mods.amt.loggers.CommandLogger;
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

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        MineTweakerAPI.server.addMineTweakerCommand("amt", new String[] {
                "/minetweaker amt charge",
                "    list charge items"
        }, new CommandLogger());
        /*
        if (TweakerPlugin.isLoaded("Mekanism")) {
            MineTweakerAPI.server.addMineTweakerCommand("gases", new String[] { "/minetweaker gases", "    Outputs a list of all gas names in the game to the minetweaker log" }, new GasLogger());
        }

        if (TweakerPlugin.isLoaded("Thaumcraft")) {
            MineTweakerAPI.server.addMineTweakerCommand("research", new String[] { "/minetweaker research", "/minetweaker research [CATEGORY]", "    Outputs a list of all category names in the game to the minetweaker log," + " or outputs a list of all research keys in a category to the log." }, new ResearchLogger());
        }

        if (TweakerPlugin.isLoaded("TConstruct")) {
            MineTweakerAPI.server.addMineTweakerCommand("materials", new String[] { "/minetweaker materials", "    Outputs a list of all Tinker's Construct material names in the game to the minetweaker log" }, new MaterialLogger());
        }
        */
    }
}

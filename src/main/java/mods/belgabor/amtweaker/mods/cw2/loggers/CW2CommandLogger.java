package mods.belgabor.amtweaker.mods.cw2.loggers;

import caveworld.api.BlockEntry;
import caveworld.api.CaveworldAPI;
import caveworld.api.ICaveVein;
import caveworld.api.ICaveVeinManager;
import com.google.common.base.Joiner;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.player.IPlayer;
import minetweaker.api.server.ICommandFunction;
import mods.belgabor.amtweaker.AMTweaker;
import mods.belgabor.amtweaker.util.CommandLoggerBase;
import net.minecraft.item.*;
import net.minecraftforge.oredict.OreDictionary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Set;

import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;

/**
 * Created by Belgabor on 03.06.2016.
 */
public class CW2CommandLogger extends CommandLoggerBase implements ICommandFunction {
    public static void register() {
        MineTweakerAPI.server.addMineTweakerCommand("cw2", new String[] {
                "/minetweaker cw2 veins",
                "    dump vein information to csv file"
        }, new CW2CommandLogger());
    }
    
    private boolean logData(OutputStreamWriter writer, ICaveVeinManager manager, int id, String name) {
        boolean errors = false;
        
        for (ICaveVein vein : manager.getCaveVeins()) {
            BlockEntry blockEntry = vein.getBlock();
            ItemStack stack = blockEntry.getItemStack();
            ArrayList<String> biomes = new ArrayList<>();
            for (int biome : vein.getGenBiomes())
                biomes.add((new Integer(biome)).toString());
            String ore = "";
            if (stack != null) {
                int[] ids = OreDictionary.getOreIDs(stack);
                if (ids.length != 0) {
                    ArrayList<String> ores = new ArrayList<>();
                    for (int oid : ids) {
                        ores.add(OreDictionary.getOreName(oid));
                    }
                    ore = Joiner.on(", ").join(ores);
                }
            }
            try {
                writer.write(String.format("%s;%s;\"%s\";%d;%s;%d;%d;%d;%d;%d;%s;%d;\"%s\";%s\n",
                        getObjectDeclaration(stack), blockEntry.getBlock().getUnlocalizedName(), stack==null?blockEntry.getBlock().getLocalizedName():stack.getDisplayName(),
                        blockEntry.getMetadata(), ore,
                        vein.getGenBlockCount(), vein.getGenWeight(), vein.getGenRate(), vein.getGenMinHeight(), vein.getGenMaxHeight(),
                        getObjectDeclaration(vein.getGenTargetBlock().getItemStack()), id, name, Joiner.on(',').join(biomes)));
                writer.flush();
            } catch (IOException e) {
                errors = true;
            }
        }
        
        return errors;
    }
    
    @Override
    public void execute(String[] arguments, IPlayer player) {
        if (arguments.length > 0) {
            if (arguments[0].equalsIgnoreCase("veins")) {
                OutputStreamWriter writer;
                boolean errors;
                try {
                    writer = new OutputStreamWriter(new FileOutputStream(new File(AMTweaker.logsDir,"caveworld2.csv")), "utf-8");
                    writer.write("Block;Unlocalized Name;Name;Meta;OreDict;count;weight;rate;Min Level;Max Level;Target block;Dimension ID;Dimension Name;Biomes\n");
                } catch (IOException e) {
                    player.sendChat("Error: Unable to open log file.");
                    return;
                }
                player.sendChat("Dumping vein data");
                errors = logData(writer, CaveworldAPI.veinManager, CaveworldAPI.getDimension(), "Caveworld");
                errors = logData(writer, CaveworldAPI.veinCavernManager, CaveworldAPI.getCavernDimension(), "Cavern") || errors;
                errors = logData(writer, CaveworldAPI.veinCavelandManager, CaveworldAPI.getCavelandDimension(), "Caveland") || errors;
                errors = logData(writer, CaveworldAPI.veinAquaCavernManager, CaveworldAPI.getAquaCavernDimension(), "Aqua Cavern") || errors;
                
                if (errors)
                    player.sendChat("There were errors writing the log file, it may be incomplete.");
                
            } else {
                player.sendChat("Unknown subcommand: "+arguments[0]);
            }
        } else {
            player.sendChat("Please use a subcommand.");
        }
    }
    
}

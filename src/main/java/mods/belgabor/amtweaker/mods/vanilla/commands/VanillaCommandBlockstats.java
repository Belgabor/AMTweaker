package mods.belgabor.amtweaker.mods.vanilla.commands;

import com.google.common.base.Joiner;
import mods.belgabor.amtweaker.AMTweaker;
import mods.belgabor.amtweaker.util.CommandLoggerBase;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.*;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.oredict.OreDictionary;

import java.io.*;
import java.util.*;

/**
 * Created by Belgabor on 03.06.2016.
 */
public class VanillaCommandBlockstats extends CommandBase {
    @Override
    public String getCommandName() {
        return "blockstats";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/blockstats [range] - Dump block statistics around player in a certain range";
    }
    
    private static class BlockStats {
        public final Block block;
        public final int meta;
        public int count = 0;
        public int minLevel = Integer.MAX_VALUE;
        public int maxLevel = 0;

        private BlockStats(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }
        
        protected void minmax(int y) {
            minLevel = Math.min(minLevel, y);
            maxLevel = Math.max(maxLevel, y);
        }
        
        protected String getTag() {
            try {
                return CommandLoggerBase.getObjectDeclaration(new ItemStack(block, 1, meta));
            } catch (NullPointerException e) {
                return block.getUnlocalizedName() + "#" + meta;
            }
        }
        
        protected String getName() {
            try {
                return (new ItemStack(block, 1, meta)).getDisplayName();
            } catch (NullPointerException e) {
                return block.getLocalizedName();
            }
        }
        
        protected String getOres() {
            try {
                int[] ids = OreDictionary.getOreIDs(new ItemStack(block, 1, meta));
                if (ids.length ==0)
                    return "";
                ArrayList<String> ores = new ArrayList<>();
                for (int id : ids) {
                    ores.add(OreDictionary.getOreName(id));
                }
                return Joiner.on(", ").join(ores);
            } catch (NullPointerException e) {
                return "";
            }
        }
    }
    
    private static class BlockStatsComp implements Comparator<BlockStats> {

        @Override
        public int compare(BlockStats o1, BlockStats o2) {
            return o1.count - o2.count;
        }
    }

    @Override
    public void processCommand(ICommandSender sender, String[] arguments) {
        if (arguments.length != 1) {
            getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText("Wrong number of parameters."));
            return;
        }
        int range;
        try {
            range = Integer.parseInt(arguments[0]);
        } catch (NumberFormatException e) {
            range = -1;
        }
        if (range <= 0) {
            getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText("Range must be a positive number."));
            return;
        }
        getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText("Starting to collect block statistics, radius " + range));
        World world = sender.getEntityWorld();
        ChunkCoordinates center = sender.getPlayerCoordinates();
        ChunkCoordinates from = new ChunkCoordinates(center.posX - range, 0, center.posZ - range);
        ChunkCoordinates to = new ChunkCoordinates(center.posX + range, world.getActualHeight() - 1, center.posZ + range);
        
        Map<String, BlockStats> stats = new HashMap<>();
        int total = 0;
        
        for (int x = from.posX; x <= to.posX; x++) {
            for (int z = from.posZ; z <= to.posZ; z++) {
                Chunk chunk = world.getChunkFromBlockCoords(x, z);
                int chunkX = x & 15;
                int chunkZ = z & 15;
                for (int y = from.posY; y <= to.posY; y++) {
                    Block block = chunk.getBlock(chunkX, y, chunkZ);
                    int meta = chunk.getBlockMetadata(chunkX, y, chunkZ);
                    String tag = block.getUnlocalizedName() + "#" + meta;
                    
                    BlockStats s = stats.get(tag);
                    if (s == null) {
                        s = new BlockStats(block, meta);
                        stats.put(tag, s);
                    }
                    
                    s.count++;
                    total++;
                    s.minmax(y);
                }
            }
        }

        TreeSet<BlockStats> sorted = new TreeSet<>(new BlockStatsComp());
        sorted.addAll(stats.values());
        OutputStreamWriter writer = null;
        boolean errors = false;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(new File(AMTweaker.logsDir, String.format("blockstats_%d_%d_%d_%d.csv", world.provider.dimensionId, center.posX, center.posZ, range))), "utf-8");
            writer.write("Block;Unlocalized Name;Name;Meta;OreDict;count;total;Min Level;Max Level;Dimension ID;Dimension Name\n");
        } catch (IOException e) {
            getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText("Warning: Unable to open log file."));
        }
        for (BlockStats bl : sorted) {
            getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText(String.format("%s  %d  %d-%d", bl.getTag(), bl.count, bl.minLevel, bl.maxLevel)));
            if (writer != null) {
                try {
                    writer.write(String.format("%s;%s;\"%s\";%d;%s;%d;%d;%d;%d;%d;\"%s\"\n", 
                            bl.getTag(), bl.block.getUnlocalizedName(), bl.getName(), bl.meta, bl.getOres(), bl.count, total, bl.minLevel, bl.maxLevel, world.provider.dimensionId, world.provider.getDimensionName()));
                    writer.flush();
                } catch (IOException e) {
                    errors = true;
                }
            }
        }
        getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText("Total: " + total));
        if (errors)
            getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText("There were errors writing the log file, it may be incomplete."));
    }
}

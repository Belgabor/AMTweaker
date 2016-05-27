package mods.belgabor.amtweaker.mods.emt.configuration;

import com.google.gson.JsonElement;
import defeatedcrow.addonforamt.economy.api.RecipeManagerEMT;
import defeatedcrow.addonforamt.economy.api.order.IOrder;
import defeatedcrow.addonforamt.economy.api.order.OrderBiome;
import defeatedcrow.addonforamt.economy.api.order.OrderSeason;
import defeatedcrow.addonforamt.economy.api.order.OrderType;
import mods.belgabor.amtweaker.AMTweaker;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Belgabor on 27.05.2016.
 */
public class EMTConfiguration {
    private static ArrayList<OrderData> orders;

    public static void read() {
        orders = new ArrayList<>();

        File emtConfig = new File(AMTweaker.confDir, "emt.json");
        if (emtConfig.exists()) {
            try {
                JsonElement jsonElement = AMTweaker.gson.fromJson(new FileReader(emtConfig), JsonElement.class);
                if (jsonElement != null) {
                    if (jsonElement.isJsonArray()) {
                        Iterator<JsonElement> iterator = jsonElement.getAsJsonArray().iterator();
                        while (iterator.hasNext()) {
                            JsonElement element = iterator.next();
                            if (element != null && element.isJsonObject()) {
                                orders.add(AMTweaker.gson.fromJson(element, OrderData.class));
                            }
                        }
                    } else {
                        AMTweaker.log(Level.WARN, "Unable to read emt.json file. Reason: Top level needs to be an array.");
                    }
                }
            } catch (IOException e) {
                AMTweaker.log(Level.WARN, "Unable to read emt.json file. Reason: %s", e.toString());
            }
        }
    }

    public static void apply() {
        int a = 0;
        int b = 0;
        for(OrderData order : orders) {
            b++;
            Object request = order.getRequest();
            OrderType type = order.getType();
            OrderSeason season = order.getSeason();
            OrderBiome biome = order.getBiome();
            if (request == null) {
                AMTweaker.log(Level.WARN, "Ignoring malformed order entry: Invalid item or oredictionary entry '%s'.", order.request);
                continue;
            }
            if ((order.amount <= 0) || (order.reward <= 0)) {
                AMTweaker.log(Level.WARN, "Ignoring malformed order entry for item '%s': Both amount and reward need to be bigger than 0.", order.request);
                continue;
            }
            if (type == null) {
                AMTweaker.log(Level.WARN, "Ignoring malformed order entry for item '%s': Invalid type '%s'.", order.request, order.type);
                continue;
            }
            if (season == null) {
                AMTweaker.log(Level.WARN, "Ignoring malformed order entry for item '%s': Invalid season '%s'.", order.request, order.season);
                continue;
            }
            if (biome == null) {
                AMTweaker.log(Level.WARN, "Ignoring malformed order entry for item '%s': Invalid biome type '%s'.", order.request, order.biome);
                continue;
            }
            if (order.name.equals("")) {
                AMTweaker.log(Level.WARN, "Ignoring malformed order entry for item '%s': No name given.", order.request);
                continue;
            }

            RecipeManagerEMT.orderRegister.addRecipe(request, order.amount, order.reward, type, season, biome, order.name);
            a++;
        }
        AMTweaker.log(Level.INFO, "%d/%d EMT orders added successfully", a, b);
    }

    private static void addDump(List<OrderData> dumps, List<? extends IOrder> source) {
        for(IOrder order : source) {
            dumps.add(new OrderData(order));
        }
    }

    public static boolean dump() {
        ArrayList<OrderData> dumps = new ArrayList<>();
        addDump(dumps, RecipeManagerEMT.orderRegister.getSingleOrders());
        addDump(dumps, RecipeManagerEMT.orderRegister.getShortOrders());
        addDump(dumps, RecipeManagerEMT.orderRegister.getMiddleOrders());
        addDump(dumps, RecipeManagerEMT.orderRegister.getLongOrders());

        File emtDump = new File(AMTweaker.confDir, "emt-dump.json");
        try {
            FileWriter f = new FileWriter(emtDump, false);
            AMTweaker.gson.toJson(dumps, f);
            f.close();
        } catch (IOException e) {
            AMTweaker.log(Level.WARN, "Unable to dump orders to emt-dump.json file. Reason: %s", e.toString());
            return false;
        }
        return true;
    }
}

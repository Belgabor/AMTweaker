package mods.belgabor.amtweaker.mods.emt.configuration;

import cpw.mods.fml.common.registry.GameRegistry;
import defeatedcrow.addonforamt.economy.api.order.IOrder;
import defeatedcrow.addonforamt.economy.api.order.OrderBiome;
import defeatedcrow.addonforamt.economy.api.order.OrderSeason;
import defeatedcrow.addonforamt.economy.api.order.OrderType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Belgabor on 27.05.2016.
 */
public class OrderData {
    public String request = "";
    public Integer amount = 0;
    public Integer reward = 0;
    public String type = "";
    public String season = "";
    public String biome = "";
    public String name = "";

    public OrderData(IOrder order) {
        Object req = order.getRequest();
        if (req instanceof String) {
            request = (String) req;
        } else if (req instanceof ItemStack) {
            ItemStack item = (ItemStack) req;
            request = String.format("%s:%d", Item.itemRegistry.getNameForObject(item.getItem()), item.getItemDamage());
        } else {
            request = "INVALID";
        }
        amount = order.getRequestNum();
        reward = order.getReward();
        type = getTypeName(order.getType());
        season = getSeasonName(order.getSeason());
        biome = getBiomeTypeName(order.getBiome());
        name = order.getName();
    }

    public OrderType getType() {
        switch (type) {
            case "single":
                return OrderType.SINGLE;
            case "short":
                return OrderType.SHORT;
            case "middle":
                return OrderType.MIDDLE;
            case "long":
                return OrderType.LONG;
            default:
                return null;
        }
    }

    public OrderSeason getSeason() {
        switch (season) {
            case "spring":
                return OrderSeason.SPRING;
            case "summer":
                return OrderSeason.SUMMER;
            case "autumn":
                return OrderSeason.AUTUMN;
            case "winter":
                return OrderSeason.WINTER;
            case "none":
                return OrderSeason.NONE;
            default:
                return null;
        }
    }

    public OrderBiome getBiome() {
        switch (biome) {
            case "plane":
                return OrderBiome.PLANE;
            case "cold":
                return OrderBiome.COLD;
            case "arid":
                return OrderBiome.ARID;
            case "damp":
                return OrderBiome.DAMP;
            case "hell":
                return OrderBiome.HELL;
            case "none":
                return OrderBiome.NONE;
            default:
                return null;
        }
    }

    public Object getRequest() {
        if (request.equals(""))
            return null;
        if (request.contains(":")) {
            String[] split = request.split(":");
            if (split.length !=3)
                return null;
            Item item = GameRegistry.findItem(split[0], split[1]);
            int m;
            try {
                m = Integer.parseInt(split[2]);
            } catch (NumberFormatException e) {
                return null;
            }
            if (item != null)
                return new ItemStack(item, 1, m);
        } else {
            if (OreDictionary.doesOreNameExist(request)) {
                return request;
            }
        }
        return null;
    }

    public static String getBiomeTypeName(OrderBiome biome) {
        switch (biome) {
            case ARID:
                return "arid";
            case COLD:
                return "cold";
            case DAMP:
                return "damp";
            case HELL:
                return "hell";
            case PLANE:
                return "plane";
            case NONE:
                return "none";
            default:
                return "UNKNOWN";
        }
    }

    public static String getSeasonName(OrderSeason season) {
        switch (season) {
            case AUTUMN:
                return "autumn";
            case SPRING:
                return "spring";
            case SUMMER:
                return "summer";
            case WINTER:
                return "winter";
            case NONE:
                return "none";
            default:
                return "UNKNOWN";
        }
    }

    public static String getTypeName(OrderType type) {
        switch (type) {
            case SINGLE:
                return "single";
            case SHORT:
                return "short";
            case MIDDLE:
                return "middle";
            case LONG:
                return "long";
            default:
                return "UNKNOWN";
        }
    }
}

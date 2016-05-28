package mods.belgabor.amtweaker.mods.mce.loggers;

import minetweaker.MineTweakerAPI;
import minetweaker.api.player.IPlayer;
import minetweaker.api.server.ICommandFunction;
import mods.belgabor.amtweaker.mods.mce.MCE;
import mods.belgabor.amtweaker.mods.mce.util.MCEAccessHelper;
import mods.belgabor.amtweaker.util.CommandLoggerBase;
import net.minecraftforge.oredict.OreDictionary;
import shift.mceconomy2.api.MCEconomyAPI;
import shift.mceconomy2.api.purchase.IPurchaseItem;
import shift.mceconomy2.api.purchase.PurchaseItemStack;
import shift.mceconomy2.api.purchase.PurchaseOreDictionary;
import shift.mceconomy2.api.shop.IProduct;
import shift.mceconomy2.api.shop.IShop;
import shift.mceconomy2.api.shop.ShopAdapter;

/**
 * Created by Belgabor on 27.05.2016.
 */
public class MCECommandLogger extends CommandLoggerBase implements ICommandFunction {
    public static void register() {
        if (MCE.available) {
            MineTweakerAPI.server.addMineTweakerCommand("mce", new String[] {
                    "/minetweaker mce price",
                    "    list MCEconomy2 item prices",
                    "/minetweaker mce shop",
                    "    list supported shops"
            }, new MCECommandLogger());
        }
    }

    private void logPrices(IPlayer player) {
        for (IPurchaseItem item : MCEAccessHelper.purchaseItems) {
            String sItem = "";
            int price = 0;
            try {
                if (item instanceof PurchaseItemStack) {
                    sItem = getItemDeclaration(MCEAccessHelper.getPurchaseItemStack_itemStack((PurchaseItemStack) item));
                    price = MCEAccessHelper.getPurchaseItemStack_price((PurchaseItemStack) item);
                } else if (item instanceof PurchaseOreDictionary) {
                    sItem = getObjectDeclaration(OreDictionary.getOreName(MCEAccessHelper.getPurchaseOreDictionary_oreId((PurchaseOreDictionary) item)));
                    price = MCEAccessHelper.getPurchaseOreDictionary_price((PurchaseOreDictionary) item);
                }
                logBoth(player, String.format("%s: %d", sItem, price));
            } catch (NullPointerException e) {
                logBoth(player, "Invalid item skipped (probably block without proper item)");
            }
        }
    }

    private void logShops(IPlayer player) {
        logBoth(player, "Shops:");
        for (IShop shop : MCEconomyAPI.ShopManager.getShops()) {
            if (shop == null)
                continue;
            String sShop = "ERROR";
            if (shop instanceof ShopAdapter) {
                sShop = shop.getShopName(null, null);
            } else if (shop.getClass().getCanonicalName().startsWith("mods.defeatedcrow")) {
                sShop = "<AMT> " + shop.getShopName(null, null);
            } else if (shop.getClass().getCanonicalName().startsWith("defeatedcrow.addonforamt.economy")) {
                sShop = "<EMT> " + shop.getShopName(null, null);
            } else {
                try {
                    sShop = shop.getShopName(null, null);
                } catch (NullPointerException e) {

                }
                sShop = "Unsupported Shop: " + sShop;
            }
            logBoth(player, sShop);
            try {
                for (IProduct product :
                        shop.getProductList(null, null)) {
                    try {
                        logBoth(player, String.format("- %s : %d", getItemDeclaration(product.getItem(shop, null, null)), product.getCost(shop, null, null)));
                    } catch (NullPointerException e) {
                        logBoth(player, "- Unable to get product data");
                    }
                }
            } catch (NullPointerException e) {
                logBoth(player, "- Unable to get product list");
            }
        }
    }

    @Override
    public void execute(String[] arguments, IPlayer player) {
        if (arguments.length > 0) {
            if (arguments[0].equalsIgnoreCase("price")) {
                logPrices(player);
            } else if (arguments[0].equalsIgnoreCase("shop")) {
                logShops(player);
            } else {
                player.sendChat("Unknown subcommand: "+arguments[0]);
            }
        } else {
            player.sendChat("Please use a subcommand.");
        }

    }
}

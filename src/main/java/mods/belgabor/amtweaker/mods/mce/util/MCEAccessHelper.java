package mods.belgabor.amtweaker.mods.mce.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import shift.mceconomy2.ShopManager;
import shift.mceconomy2.api.MCEconomyAPI;
import shift.mceconomy2.api.purchase.IPurchaseItem;
import shift.mceconomy2.api.purchase.PurchaseItemStack;
import shift.mceconomy2.api.purchase.PurchaseOreDictionary;
import shift.mceconomy2.api.shop.IProduct;
import shift.mceconomy2.api.shop.IShop;
import shift.mceconomy2.api.shop.ShopAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static mods.belgabor.amtweaker.helpers.StackHelper.areEqual;

/**
 * Created by Belgabor on 27.05.2016.
 */
public class MCEAccessHelper {
    public static List<IPurchaseItem> purchaseItems;
    public static Field PurchaseItemStack_itemStack;
    public static Field PurchaseItemStack_price;
    public static Field PurchaseOreDictionary_oreId;
    public static Field PurchaseOreDictionary_price;
    public static Field ShopManager_cachedItem;

    public static boolean init() {

        try {
            Class cShopManager = ShopManager.class;

            Field f = cShopManager.getDeclaredField("purchaseItems");
            f.setAccessible(true);
            purchaseItems = (ArrayList<IPurchaseItem>) f.get(null);
            ShopManager_cachedItem = cShopManager.getDeclaredField("cachedItem");
            ShopManager_cachedItem.setAccessible(true);

            Class cPurchaseItemStack = PurchaseItemStack.class;
            PurchaseItemStack_itemStack = cPurchaseItemStack.getDeclaredField("itemStack");
            PurchaseItemStack_itemStack.setAccessible(true);
            PurchaseItemStack_price = cPurchaseItemStack.getDeclaredField("price");
            PurchaseItemStack_price.setAccessible(true);

            Class cPurchaseOreDictionary = PurchaseOreDictionary.class;
            PurchaseOreDictionary_oreId = cPurchaseOreDictionary.getDeclaredField("oreId");
            PurchaseOreDictionary_oreId.setAccessible(true);
            PurchaseOreDictionary_price = cPurchaseOreDictionary.getDeclaredField("price");
            PurchaseOreDictionary_price.setAccessible(true);

        } catch (NoSuchFieldException | SecurityException | IllegalAccessException | ClassCastException e) {
            return false;
        }

        return true;
    }

    public static ItemStack getPurchaseItemStack_itemStack(PurchaseItemStack item) {
        try {
            return (ItemStack) PurchaseItemStack_itemStack.get(item);
        } catch (IllegalAccessException | ClassCastException e) {
            return null;
        }
    }

    public static int getPurchaseItemStack_price(PurchaseItemStack item) {
        try {
            return (int) PurchaseItemStack_price.get(item);
        } catch (IllegalAccessException | ClassCastException e) {
            return -999;
        }
    }

    public static int getPurchaseOreDictionary_oreId(PurchaseOreDictionary item) {
        try {
            return (int) PurchaseOreDictionary_oreId.get(item);
        } catch (IllegalAccessException | ClassCastException e) {
            return -999;
        }
    }

    public static int getPurchaseOreDictionary_price(PurchaseOreDictionary item) {
        try {
            return (int) PurchaseOreDictionary_price.get(item);
        } catch (IllegalAccessException | ClassCastException e) {
            return -999;
        }
    }

    private static IPurchaseItem findPurchaseItemOre(int oreId) {
        for(IPurchaseItem theItem: purchaseItems) {
            if (theItem instanceof PurchaseOreDictionary) {
                if (oreId == getPurchaseOreDictionary_oreId((PurchaseOreDictionary) theItem))
                    return theItem;
            }
        }
        return null;
    }

    private static IPurchaseItem findPurchaseItemItem(ItemStack item) {
        for(IPurchaseItem theItem: purchaseItems) {
            if (theItem instanceof PurchaseItemStack) {
                if (areEqual(item, getPurchaseItemStack_itemStack((PurchaseItemStack) theItem)))
                    return theItem;
            }
        }
        return null;
    }

    public static IPurchaseItem findPurchaseItem(Object item) {
        if (item instanceof String) {
            return findPurchaseItemOre(OreDictionary.getOreID((String) item));
        } else if (item instanceof ItemStack) {
            return findPurchaseItemItem((ItemStack) item);
        } else {
            return null;
        }
    }

    public static Integer getPrice(IPurchaseItem item) {
        if (item instanceof PurchaseOreDictionary) {
            return getPurchaseOreDictionary_price((PurchaseOreDictionary) item);
        } else if (item instanceof PurchaseItemStack) {
            return getPurchaseItemStack_price((PurchaseItemStack) item);
        }
        return null;
    }

    public static void setPrice(IPurchaseItem item, int price) {
        try {
            if (item instanceof PurchaseOreDictionary) {
                PurchaseOreDictionary_price.set(item, price);
            } else if (item instanceof PurchaseItemStack) {
                PurchaseItemStack_price.set(item, price);
            }
            ShopManager_cachedItem.set(null, null);
        } catch (IllegalAccessException e) {}
    }

    public static IShop findShop(String name) {
        for (IShop shop : MCEconomyAPI.ShopManager.getShops()) {
            if (shop == null)
                continue;
            String sShop = null;
            if (shop instanceof ShopAdapter) {
                sShop = shop.getShopName(null, null);
            } else if (shop.getClass().getCanonicalName().startsWith("mods.defeatedcrow")) {
                sShop = shop.getShopName(null, null);
            } else if (shop.getClass().getCanonicalName().startsWith("defeatedcrow.addonforamt.economy")) {
                sShop = shop.getShopName(null, null);
            }
            if (sShop == null)
                continue;
            if (sShop.equals(name))
                return shop;
        }
        return null;
    }

    public static IProduct findProduct(IShop shop, ItemStack item) {
        for (IProduct product : shop.getProductList(null, null)) {
            if (areEqual(item, product.getItem(shop, null, null)))
                return product;
        }
        return null;
    }
}

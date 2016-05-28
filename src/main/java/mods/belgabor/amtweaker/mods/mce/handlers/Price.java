package mods.belgabor.amtweaker.mods.mce.handlers;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import mods.belgabor.amtweaker.mods.mce.util.MCEAccessHelper;
import mods.belgabor.amtweaker.util.CommandLoggerBase;
import net.minecraft.item.ItemStack;
import shift.mceconomy2.api.MCEconomyAPI;
import shift.mceconomy2.api.purchase.IPurchaseItem;
import shift.mceconomy2.api.shop.IProduct;
import shift.mceconomy2.api.shop.IShop;
import shift.mceconomy2.api.shop.ProductBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static mods.belgabor.amtweaker.helpers.InputHelper.toObject;
import static mods.belgabor.amtweaker.helpers.InputHelper.toStack;

/**
 * Created by Belgabor on 27.05.2016.
 */
@ZenClass("mods.mce.Price")
public class Price {
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Adding a new charge item for the ice maker
    @ZenMethod
    public static void set(IIngredient item, int price) {
        Object citem = toObject(item);
        if (citem == null) {
            MineTweakerAPI.getLogger().logError("Set price: item must not be null!");
            return;
        }
        if (!((citem instanceof String) || (citem instanceof ItemStack))) {
            MineTweakerAPI.getLogger().logError("Set price: unsupported item type!");
            return;
        }
        MineTweakerAPI.apply(new PriceSet(citem, price));
    }

    @ZenMethod
    public static void set(String shop, IItemStack item, int price) {
        IShop cShop = MCEAccessHelper.findShop(shop);
        if (cShop == null) {
            MineTweakerAPI.getLogger().logError(String.format("Set shop price: Unknown or unsupported shop '%s'.", shop));
            return;
        }
        ItemStack cItem = toStack(item, true);
        if (cItem == null) {
            MineTweakerAPI.getLogger().logError(String.format("Set shop price: Item must not be null."));
            return;
        }
        if (MCEAccessHelper.findProduct(cShop, cItem) != null) {
            MineTweakerAPI.getLogger().logError(String.format("Set shop price: Item already sold (changing prices of existing items is not supported)."));
            return;
        }
        MineTweakerAPI.apply(new ProductPriceSet(cShop, cItem, price));
    }


    private static class PriceSet implements IUndoableAction {
        private final Object item;
        private final Integer price;
        private Integer oldPrice;
        private Boolean applied;

        public PriceSet(Object item, int price)  {
            this.item = item;
            this.price = price;
            this.applied = false;
        }

        @Override
        public void apply() {
            if (!applied) {
                IPurchaseItem current = MCEAccessHelper.findPurchaseItem(item);
                if (current != null) {
                    oldPrice = MCEAccessHelper.getPrice(current);
                    MCEAccessHelper.setPrice(current, price);
                } else {
                    if (item instanceof String) {
                        MCEconomyAPI.ShopManager.addPurchaseItem((String) item, price);
                    } else {
                        MCEconomyAPI.ShopManager.addPurchaseItem((ItemStack) item, price);
                    }
                }
                applied = true;
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            if (applied) {
                IPurchaseItem current = MCEAccessHelper.findPurchaseItem(item);
                if (current == null) {
                    MineTweakerAPI.getLogger().logError("Set price: item unexpectedly not found during undo!");
                }
                if (oldPrice != null) {
                    MCEAccessHelper.setPrice(current, oldPrice);
                } else {
                    MCEAccessHelper.purchaseItems.remove(current);
                }
                applied = false;
            }
        }

        @Override
        public String describe() {
            return String.format("Setting price for %s: %d", CommandLoggerBase.getObjectDeclaration(item), price);
        }

        @Override
        public String describeUndo() {
            return String.format("Reverting price for %s", CommandLoggerBase.getObjectDeclaration(item));
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class ProductPriceSet implements IUndoableAction {
        private final IShop shop;
        //private final ItemStack item;
        //private final Integer price;
        private final ProductBase product;
        private Boolean applied;

        public ProductPriceSet(IShop shop, ItemStack item, int price)  {
            this.shop = shop;
            this.product = new ProductBase(item, price);
            //this.item = item;
            //this.price = price;
            this.applied = false;
        }

        @Override
        public void apply() {
            if (!applied) {
                shop.addProduct(product);
                applied = true;
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            if (applied) {
                shop.getProductList(null, null).remove(product);
                applied = false;
            }
        }

        @Override
        public String describe() {
            return String.format("Setting price for %s at shop '%s': %d", CommandLoggerBase.getObjectDeclaration(product.getItem(shop, null, null)), shop.getShopName(null, null), product.getCost(shop, null, null));
        }

        @Override
        public String describeUndo() {
            return String.format("Removing item %s from shop '%s': %d", CommandLoggerBase.getObjectDeclaration(product.getItem(shop, null, null)), shop.getShopName(null, null), product.getCost(shop, null, null));
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @ZenMethod
    public static void remove(IIngredient item) {
        Object citem = toObject(item, true);
        if (citem == null) {
            MineTweakerAPI.getLogger().logError("Price removal: Item may not be null!");
            return;
        }
        if (MCEAccessHelper.findPurchaseItem(citem) == null) {
            MineTweakerAPI.getLogger().logError("Price removal: Item has no price!");
            return;
        }
        MineTweakerAPI.apply(new PriceRemove(citem));
    }

    @ZenMethod
    public static void remove(String shop, IItemStack item) {
        IShop cShop = MCEAccessHelper.findShop(shop);
        if (cShop == null) {
            MineTweakerAPI.getLogger().logError(String.format("Remove shop price: Unknown or unsupported shop '%s'.", shop));
            return;
        }
        ItemStack cItem = toStack(item, true);
        if (cItem == null) {
            MineTweakerAPI.getLogger().logError("Remove shop price: Item must not be null.");
            return;
        }
        IProduct product = MCEAccessHelper.findProduct(cShop, cItem);
        if (product == null) {
            MineTweakerAPI.getLogger().logError("Remove shop price: Item isn't sold.");
            return;
        }
        MineTweakerAPI.apply(new ProductPriceRemove(cShop, product));
    }

    //Removes a recipe, apply is never the same for anything, so will always need to override it
    private static class PriceRemove implements IUndoableAction {
        private IPurchaseItem pItem = null;
        private final Object item;
        private Boolean applied = false;

        public PriceRemove(Object item) {
            this.item = item;
        }

        @Override
        public void apply() {
            if (!applied) {
                pItem = MCEAccessHelper.findPurchaseItem(item);
                if (pItem == null) {
                    MineTweakerAPI.getLogger().logError("Price removal: Couldn't apply price removal (item unexpectedly has no price)");
                    return;
                }
                MCEAccessHelper.purchaseItems.remove(pItem);
                applied = true;
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            if (applied) {
                MCEconomyAPI.ShopManager.addPurchaseItem(pItem);
                pItem = null;
                applied = false;
            }
        }

        @Override
        public String describeUndo() {
            String x;
            if (item instanceof String) {
                x = "<ore:" + item + ">";
            } else {
                x = ((ItemStack) item).getDisplayName();
            }
            return String.format("Restoring price for %s", x);
        }

        @Override
        public String describe() {
            String x;
            if (item instanceof String) {
                x = "<ore:" + item + ">";
            } else {
                x = ((ItemStack) item).getDisplayName();
            }
            return String.format("Removing price for %s", x);
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }

    }

    private static class ProductPriceRemove implements IUndoableAction {
        private final IShop shop;
        private final IProduct product;
        private Boolean applied = false;

        public ProductPriceRemove(IShop shop, IProduct product) {
            this.shop = shop;
            this.product = product;
        }

        @Override
        public void apply() {
            if (!applied) {
                shop.getProductList(null, null).remove(product);
                applied = true;
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            if (applied) {
                shop.addProduct(product);
                applied = false;
            }
        }

        @Override
        public String describeUndo() {
            return String.format("Restoring price for %s at shop '%s': %d", CommandLoggerBase.getObjectDeclaration(product.getItem(shop, null, null)), shop.getShopName(null, null), product.getCost(shop, null, null));
        }

        @Override
        public String describe() {
            return String.format("Removing item %s from shop '%s': %d", CommandLoggerBase.getObjectDeclaration(product.getItem(shop, null, null)), shop.getShopName(null, null), product.getCost(shop, null, null));
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }

    }
}

package pt.morais.holoshop.manager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import pt.morais.holoshop.HoloShop;
import pt.morais.holoshop.dao.ShopDao;
import pt.morais.holoshop.model.Hologram;
import pt.morais.holoshop.model.Shop;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShopManager {

    private static final List<String> sell = HoloShop.getInstance().getConfig().getStringList("sell");
    private static final List<String> buy = HoloShop.getInstance().getConfig().getStringList("buy");
    private static final List<String> sellBuy = HoloShop.getInstance().getConfig().getStringList("sell-buy");

    public static boolean create(String key, int amount, Location location, ItemStack item, double price, double sellPrice) {

        if (ShopDao.getShopByKey(key) != null) return false;

        location.setX(location.getBlockX() + 0.5);
        location.setZ(location.getBlockZ() + 0.5);
        location.setYaw(0);
        location.setPitch(0);

        if (price == 0) {
            ShopDao.create(key, amount, new Hologram(location, sell.stream()
                    .map(s -> ChatColor.translateAlternateColorCodes('&', s)
                            .replace("{amount}", Integer.toString(amount))
                            .replace("{sell}", Double.toString(sellPrice))
                            .replace("{name}", Objects.requireNonNull(item.getItemMeta()).getDisplayName()))
                    .collect(Collectors.toList())), item, price, sellPrice);
        } else if (sellPrice == 0) {
            ShopDao.create(key, amount, new Hologram(location, buy.stream()
                    .map(s -> ChatColor.translateAlternateColorCodes('&', s)
                            .replace("{amount}", Integer.toString(amount))
                            .replace("{buy}", Double.toString(price))
                            .replace("{name}", Objects.requireNonNull(item.getItemMeta()).getDisplayName()))
                    .collect(Collectors.toList())), item, price, sellPrice);
        } else {
            ShopDao.create(key, amount, new Hologram(location, sellBuy.stream()
                    .map(s -> ChatColor.translateAlternateColorCodes('&', s)
                            .replace("{amount}", Integer.toString(amount))
                            .replace("{sell}", Double.toString(sellPrice))
                            .replace("{buy}", Double.toString(price))
                            .replace("{name}", Objects.requireNonNull(item.getItemMeta()).getDisplayName()))
                    .collect(Collectors.toList())), item, price, sellPrice);
        }

        return true;
    }

    public static void remove(Shop shop) {
        shop.getHologram().destroy();
        ShopDao.remove(shop);
    }

    public static List<Hologram> getHolograms() {
        return ShopDao.getHolograms().values().stream().map(Shop::getHologram).collect(Collectors.toList());
    }

}

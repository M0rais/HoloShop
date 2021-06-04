package pt.morais.holoshop.dao;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import pt.morais.holoshop.model.Hologram;
import pt.morais.holoshop.model.Shop;

import java.util.HashMap;

public class ShopDao {

    private final HashMap<Location, Shop> holograms = new HashMap<>();

    public void create(String key, int amount, Hologram hologram, ItemStack item, double price, double sellPrice) {
        holograms.put(hologram.getLocation(), new Shop(key, amount, hologram.getLocation(), item, hologram, price, sellPrice));
    }

    public void remove(Shop shop) {
        holograms.remove(shop);
    }

    public Shop getShopByEntityID(int id) {
        return holograms.values().stream()
                .filter(shop -> shop.getHologram().getEntityIDS().contains(id)).findAny().orElse(null);
    }

    public Shop getShopByKey(String key) {
        return holograms.values().stream().filter(shop -> shop.getKey().equalsIgnoreCase(key)).findAny().orElse(null);
    }

    public HashMap<Location, Shop> getHolograms() {
        return holograms;
    }
}

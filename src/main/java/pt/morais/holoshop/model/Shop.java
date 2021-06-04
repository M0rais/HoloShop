package pt.morais.holoshop.model;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Shop {

    private final String key;
    private final Location location;
    private final Hologram hologram;
    private final int amount;
    private final double price;
    private final double sellPrice;

    public Shop(String key, int amount, Location location, ItemStack itemStack, Hologram hologram, double price, double sellPrice) {
        this.key = key;
        this.amount = amount;
        this.location = location;
        this.hologram = hologram;
        hologram.setItem(itemStack);
        this.price = price;
        this.sellPrice = sellPrice;
    }

    public String getKey() {
        return key;
    }

    public Location getLocation() {
        return location;
    }

    public double getPrice() {
        return price;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public int getAmount() {
        return amount;
    }
}
package pt.morais.holoshop.manager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pt.morais.holoshop.HoloShop;
import pt.morais.holoshop.dao.ShopDao;
import pt.morais.holoshop.hologram.PacketHologram;
import pt.morais.holoshop.hook.VaultHook;
import pt.morais.holoshop.model.Hologram;
import pt.morais.holoshop.model.Shop;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShopManager {

    private final ShopDao shopDao;
    private static final Economy economy = VaultHook.getEcon();
    private final HologramManager hologramManager;

    public ShopManager(HoloShop plugin) {
        this.shopDao = new ShopDao();
        this.hologramManager = new HologramManager(shopDao);
        hologramManager.loadTask(plugin);
    }

    public Shop getShopByKey(String key) {
        return shopDao.getShopByKey(key);
    }

    public Shop getShopByEntityID(int id) {
        return shopDao.getShopByEntityID(id);
    }

    public void hide(Player player) {
        hologramManager.hide(player);
    }

    public boolean create(String key, int amount, Location location, ItemStack item, double price, double sellPrice) {

        if (shopDao.getShopByKey(key) != null) return false;

        location.setX(location.getBlockX() + 0.5);
        location.setZ(location.getBlockZ() + 0.5);
        location.setYaw(0);
        location.setPitch(0);

        if (price == 0) {
            shopDao.create(key, amount, hologramManager.create(location,
                    Arrays.asList("§b§lServer§f§lMC", item.getItemMeta().getDisplayName() + " §7[x" + amount + "]",
                            "§c§lS §f$" + sellPrice)), item, price, sellPrice);
        } else if (sellPrice == 0) {
            shopDao.create(key, amount, hologramManager.create(location,
                    Arrays.asList("§b§lServer§f§lMC", item.getItemMeta().getDisplayName() + " §7[x" + amount + "]",
                            "§a§lB §f$" + price)), item, price, sellPrice);
        } else {
            shopDao.create(key, amount, hologramManager.create(location,
                    Arrays.asList("§b§lServer§f§lMC", item.getItemMeta().getDisplayName() + " §7[x" + amount + "]",
                            "§a§lB §f$" + price + " §7| §c§lS §f$" + sellPrice)), item, price, sellPrice);
        }

        return true;
    }

    public void remove(Shop shop) {
        hologramManager.remove(shop);
        shopDao.remove(shop);
    }

    public void sell(Player player, PacketHologram packetHologram, Shop shop) {
        Inventory inventory = player.getInventory();
        Material material = shop.getHologram().getItem().getType();
        int amount = shop.getAmount();
        double price = shop.getSellPrice();

        int playerAmount = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack content = inventory.getItem(i);
            if (content != null && content.getType() == material) {
                playerAmount += content.getAmount();
                if (playerAmount > amount) {
                    content.setAmount(playerAmount - amount);
                    break;
                } else
                    inventory.setItem(i, null);

            }
        }

        playerAmount = Math.min(playerAmount, amount);

        price = Math.ceil((playerAmount * price) / amount);

        packetHologram.sendRandomHologram(shop.getHologram().getLocation().clone(),  "§a+$" + price);
        player.sendMessage("§aYou sold " + playerAmount + " for $" + price);
        economy.depositPlayer(player, price);
    }

    public void buy(Player player, PacketHologram packetHologram, Shop shop) {
        int amount = shop.getAmount();
        double price = shop.getPrice();

        if (economy.getBalance(player) < price) {
            player.sendMessage("§cInsufficient money.");
            return;
        }

        ItemStack item = shop.getHologram().getItem();

        int freeSpace = 0;

        Inventory inventory = player.getInventory();

        ItemStack[] items = IntStream.range(0, 36).boxed().map(inventory::getItem).toArray(ItemStack[]::new);

        for (ItemStack itemStack : items) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                freeSpace += 64;
                continue;
            }
            if (itemStack.getType() == item.getType()) freeSpace += itemStack.getMaxStackSize() - itemStack.getAmount();
        }

        int floorDrop = freeSpace < amount ? amount - freeSpace : 0;
        Location playerLocation = player.getLocation();

        if (floorDrop > 0) {
            while (floorDrop > 0) {
                boolean higher = floorDrop > 64;

                item.setAmount(higher ? 64 : floorDrop);

                floorDrop = higher ? floorDrop - 64 : 0;

                playerLocation.getWorld().dropItem(playerLocation, item);
            }
        }

        packetHologram.sendRandomHologram(shop.getHologram().getLocation().clone(), "§c-$" + price);

        item.setAmount(amount - floorDrop);
        economy.withdrawPlayer(player, price);
        player.getInventory().addItem(item);
        player.sendMessage("§aYou bought " + amount + " for $" + price);
    }

}
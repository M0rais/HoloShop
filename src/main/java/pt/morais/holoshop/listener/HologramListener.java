package pt.morais.holoshop.listener;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pt.morais.holoshop.dao.ShopDao;
import pt.morais.holoshop.events.HologramInteractEvent;
import pt.morais.holoshop.hologram.PacketHologram;
import pt.morais.holoshop.hook.VaultHook;
import pt.morais.holoshop.model.Shop;
import pt.morais.holoshop.model.enums.HologramInteractType;

import java.util.stream.IntStream;

public class HologramListener implements Listener {

    private final Economy economy = VaultHook.getEcon();

    @EventHandler
    public void onHologramInteract(HologramInteractEvent event) {
        Shop shop = ShopDao.getShopByEntityID(event.getEntityID());

        if (event.getHologramInteractType() == HologramInteractType.ATTACK) {
            if (shop.getSellPrice() == 0) return;
            sell(event.getPlayer(), event.getPacketHologram(), shop);
            return;
        }
        if (shop.getPrice() == 0) return;
        buy(event.getPlayer(), event.getPacketHologram(), shop);
    }

    private void sell(Player player, PacketHologram packetHologram, Shop shop) {
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

    private void buy(Player player, PacketHologram packetHologram, Shop shop) {
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

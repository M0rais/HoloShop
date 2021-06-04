package pt.morais.holoshop.listener;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pt.morais.holoshop.events.HologramInteractEvent;
import pt.morais.holoshop.hologram.PacketHologram;
import pt.morais.holoshop.hook.VaultHook;
import pt.morais.holoshop.manager.ShopManager;
import pt.morais.holoshop.model.Shop;
import pt.morais.holoshop.model.enums.HologramInteractType;

public class HologramListener implements Listener {

    private final ShopManager shopManager;

    public HologramListener(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @EventHandler
    public void onHologramInteract(HologramInteractEvent event) {
        Shop shop = shopManager.getShopByEntityID(event.getEntityID());

        if (event.getHologramInteractType() == HologramInteractType.ATTACK) {
            if (shop.getSellPrice() == 0) return;
            shopManager.sell(event.getPlayer(), event.getPacketHologram(), shop);
            return;
        }
        if (shop.getPrice() == 0) return;
        shopManager.buy(event.getPlayer(), event.getPacketHologram(), shop);
    }

}

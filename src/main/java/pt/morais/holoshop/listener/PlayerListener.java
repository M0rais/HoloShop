package pt.morais.holoshop.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pt.morais.holoshop.manager.HologramManager;
import pt.morais.holoshop.manager.ShopManager;

public class PlayerListener implements Listener {

    private final ShopManager shopManager;

    public PlayerListener(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        shopManager.hide(event.getPlayer());
    }


}

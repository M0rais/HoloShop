package pt.morais.holoshop.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pt.morais.holoshop.HoloShop;
import pt.morais.holoshop.model.Hologram;

public class HologramManager {

    public static void show(Player player) {
        for (Hologram hologram : ShopManager.getHolograms()) {
            if (hologram.show(player)) continue;
            hologram.hide(player);
        }
    }

    public static void hide(Player player) {
        for (Hologram hologram : ShopManager.getHolograms()) {
            hologram.hide(player);
        }
    }

    public static void loadTask(HoloShop plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                show(player);
            }
        }, 40, 40);
    }

}
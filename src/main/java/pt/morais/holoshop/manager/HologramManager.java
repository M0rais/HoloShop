package pt.morais.holoshop.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pt.morais.holoshop.HoloShop;
import pt.morais.holoshop.dao.ShopDao;
import pt.morais.holoshop.hologram.PacketHologram;
import pt.morais.holoshop.hologram.packethologram.PacketHologram_V1_13_2;
import pt.morais.holoshop.hologram.packethologram.PacketHologram_V1_14_1;
import pt.morais.holoshop.hologram.packethologram.PacketHologram_V1_15_1;
import pt.morais.holoshop.hologram.packethologram.PacketHologram_V1_16_3;
import pt.morais.holoshop.model.Hologram;
import pt.morais.holoshop.model.Shop;

import java.util.List;
import java.util.stream.Collectors;

public class HologramManager {

    private final ShopDao shopDao;

    public HologramManager(ShopDao shopDao) {
        this.shopDao = shopDao;
    }

    public Hologram create(Location location, List<String> lines) {
        return new Hologram(location, lines);
    }

    public void show(Player player) {
        for (Hologram hologram : getHolograms()) {
            if (hologram.show(player)) continue;
            hologram.hide(player);
        }
    }

    public void hide(Player player) {
        for (Hologram hologram : getHolograms()) {
            hologram.hide(player);
        }
    }

    public void remove(Shop shop) {
        shop.getHologram().destroy();
    }

    public void loadTask(HoloShop plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                show(player);
            }
        }, 40, 40);
    }

    public List<Hologram> getHolograms() {
        return shopDao.getHolograms().values().stream().map(Shop::getHologram).collect(Collectors.toList());
    }

}
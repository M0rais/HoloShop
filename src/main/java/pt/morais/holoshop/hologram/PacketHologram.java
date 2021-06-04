package pt.morais.holoshop.hologram;


import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public interface PacketHologram {

    void create();

    void show();

    void hide();

    void sendRandomHologram(Location location, String s);

    Player getPlayer();

    Set<Integer> getEntities();

}

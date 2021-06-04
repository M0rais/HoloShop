package pt.morais.holoshop.model;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pt.morais.holoshop.HoloShop;
import pt.morais.holoshop.hologram.PacketHologram;
import pt.morais.holoshop.hologram.packethologram.PacketHologram_V1_13_2;
import pt.morais.holoshop.hologram.packethologram.PacketHologram_V1_14_1;
import pt.morais.holoshop.hologram.packethologram.PacketHologram_V1_15_1;
import pt.morais.holoshop.hologram.packethologram.PacketHologram_V1_16_3;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    private final Location location;
    private List<String> lines;
    private ItemStack item;
    private final List<PacketHologram> packetHolograms = new ArrayList<>();
    private final List<Integer> entityIDS = new ArrayList<>();

    public Hologram(Location location, List<String> lines) {
        this.location = location;
        this.lines = lines;
    }

    public Location getLocation() {
        return location;
    }

    public List<String> getLines() {
        return lines;
    }

    public List<PacketHologram> getPacketHolograms() {
        return packetHolograms;
    }

    public List<Integer> getEntityIDS() {
        return entityIDS;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public void destroy() {
        for (PacketHologram packetHologram : packetHolograms) {
            packetHologram.hide();
        }
    }

    public boolean show(Player player) {
        Location playerLocation = player.getLocation();
        if (player.getLocation().getWorld() == this.location.getWorld() && this.location.distance(playerLocation) < 16) {
            if (packetHolograms.stream().anyMatch(pH -> pH.getPlayer() == player)) return true;
            PacketHologram packetHologram = getPacketHologram(player);
            packetHologram.create();
            packetHologram.show();
            this.packetHolograms.add(packetHologram);
            return true;
        }
        return false;
    }

    public void hide(Player player) {
        PacketHologram packetHologram = packetHolograms.stream().filter(pH -> pH.getPlayer() == player).findFirst().orElse(null);
        if (packetHologram == null) return;
        packetHologram.hide();
        this.packetHolograms.remove(packetHologram);
    }

    private PacketHologram getPacketHologram(Player player) {
        switch (HoloShop.getVersion()) {
            case "v1_13_R2":
                return new PacketHologram_V1_13_2(player, this);
            case "v1_14_R1":
                return new PacketHologram_V1_14_1(player, this);
            case "v1_15_R1":
                return new PacketHologram_V1_15_1(player, this);
            default:
                return new PacketHologram_V1_16_3(player, this);
        }
    }


}
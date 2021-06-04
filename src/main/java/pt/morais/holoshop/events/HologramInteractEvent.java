package pt.morais.holoshop.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pt.morais.holoshop.hologram.PacketHologram;
import pt.morais.holoshop.model.enums.HologramInteractType;

public class HologramInteractEvent extends Event implements Cancellable {

    private final Player player;
    private boolean isCancelled = false;
    private final int entityID;
    private final PacketHologram packetHologram;
    private final HologramInteractType hologramInteractType;
    private static final HandlerList HANDLERS = new HandlerList();


    public HologramInteractEvent(Player player, int entityID, PacketHologram packetHologram, HologramInteractType hologramInteractType) {
        this.player = player;
        this.entityID = entityID;
        this.packetHologram = packetHologram;
        this.hologramInteractType = hologramInteractType;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PacketHologram getPacketHologram() {
        return packetHologram;
    }

    public Player getPlayer() {
        return player;
    }

    public int getEntityID() {
        return entityID;
    }

    public HologramInteractType getHologramInteractType() {
        return hologramInteractType;
    }
}

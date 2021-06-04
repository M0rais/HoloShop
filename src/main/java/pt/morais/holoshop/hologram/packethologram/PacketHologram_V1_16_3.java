package pt.morais.holoshop.hologram.packethologram;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import pt.morais.holoshop.HoloShop;
import pt.morais.holoshop.hologram.PacketHologram;
import pt.morais.holoshop.hologram.packetreader.PacketReader_V1_16_3;
import pt.morais.holoshop.model.Hologram;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PacketHologram_V1_16_3 implements PacketHologram {

    private final PacketReader_V1_16_3 packetReader = new PacketReader_V1_16_3();
    private final Player player;
    private final Hologram hologram;
    private final HashMap<Integer, EntityArmorStand> entityArmorStands = new HashMap<>();

    public PacketHologram_V1_16_3(Player player, Hologram hologram) {
        this.player = player;
        this.hologram = hologram;
    }

    @Override
    public void create() {
        Location location = hologram.getLocation();
        WorldServer worldServer = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();

        double x = location.getX();
        double z = location.getZ();

        double y = (location.getY() - 1) + (0.25 * hologram.getLines().size());
        for (String line : hologram.getLines()) {

            EntityArmorStand entityArmorStand = new EntityArmorStand(worldServer, location.getX(), y, location.getZ());

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("text", line);

            entityArmorStand.setCustomName(IChatBaseComponent.ChatSerializer.a(jsonObject));
            entityArmorStand.setCustomNameVisible(true);
            entityArmorStand.setNoGravity(true);
            entityArmorStand.setInvisible(true);

            entityArmorStands.put(entityArmorStand.getId(), entityArmorStand);

            hologram.getEntityIDS().add(entityArmorStand.getId());

            y -= 0.25;
        }

        y -= 0.1;

        EntityArmorStand entityArmorStand = new EntityArmorStand(worldServer, x, y, z);
        entityArmorStand.setCustomNameVisible(false);
        entityArmorStand.setNoGravity(true);
        entityArmorStand.setInvisible(true);

        entityArmorStands.put(entityArmorStand.getId(), entityArmorStand);
        hologram.getEntityIDS().add(entityArmorStand.getId());

    }

    @Override
    public void show() {
        if (entityArmorStands.isEmpty()) {
            throw new IllegalStateException("EntityArmorStand doesn't exist, you must create it " +
                    "(PacketHologram#create(name, location).");
        }
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        for (Map.Entry<Integer, EntityArmorStand> entry : entityArmorStands.entrySet()) {
            connection.sendPacket(new PacketPlayOutSpawnEntityLiving(entry.getValue()));
            if (!entry.getValue().hasCustomName()) {
                connection.sendPacket(new PacketPlayOutEntityEquipment(entry.getKey(),
                        Collections.singletonList(Pair.of(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(hologram.getItem())))));
            }
            connection.sendPacket(new PacketPlayOutEntityMetadata(entry.getKey(), entry.getValue().getDataWatcher(), true));
        }
        packetReader.inject(player, this);
    }

    @Override
    public void hide() {
        if (entityArmorStands.isEmpty()) {
            throw new IllegalStateException("EntityArmorStand doesn't exist, you must create it " +
                    "(PacketHologram#create(name, location).");
        }
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        for (int id : entityArmorStands.keySet()) {
            connection.sendPacket(new PacketPlayOutEntityDestroy(id));
        }
        packetReader.uninject(player);
    }

    ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

    @Override
    public void sendRandomHologram(Location location, String s) {
        location.add(threadLocalRandom.nextDouble(-0.75, 0.75), 0.30,
                threadLocalRandom.nextDouble(-0.75, 0.75));
        WorldServer worldServer = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();

        EntityArmorStand entityArmorStand = new EntityArmorStand(worldServer, location.getX(), location.getY(), location.getZ());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", s);

        entityArmorStand.setCustomName(IChatBaseComponent.ChatSerializer.a(jsonObject));
        entityArmorStand.setCustomNameVisible(true);
        entityArmorStand.setNoGravity(true);
        entityArmorStand.setInvisible(true);

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(entityArmorStand));
        connection.sendPacket(new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true));

        Bukkit.getScheduler().runTaskLaterAsynchronously(HoloShop.getInstance(), () -> connection.sendPacket(new PacketPlayOutEntityDestroy(entityArmorStand.getId())), 20);
    }


    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Set<Integer> getEntities() {
        return entityArmorStands.keySet();
    }
}
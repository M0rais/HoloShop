package pt.morais.holoshop.hologram;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public interface PacketReader<T> {

    void inject(Player player, PacketHologram packetHologram);

    void uninject(Player player);

    void readPacket(Player player, T t);


    default Object getValue(Object o, String name) {
        Object result = null;

        try {
            Field field = o.getClass().getDeclaredField(name);
            field.setAccessible(true);

            result = field.get(o);
            field.setAccessible(false);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;
    }

}

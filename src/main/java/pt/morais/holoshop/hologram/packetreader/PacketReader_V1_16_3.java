package pt.morais.holoshop.hologram.packetreader;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import pt.morais.holoshop.HoloShop;
import pt.morais.holoshop.dao.ShopDao;
import pt.morais.holoshop.events.HologramInteractEvent;
import pt.morais.holoshop.hologram.PacketHologram;
import pt.morais.holoshop.hologram.PacketReader;
import pt.morais.holoshop.model.enums.HologramInteractType;

import java.util.List;

public class PacketReader_V1_16_3 implements PacketReader<Packet<?>> {

    public Channel channel;
    public PacketHologram packetHologram;

    @Override
    public void inject(Player player, PacketHologram packetHologram) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        channel = entityPlayer.playerConnection.networkManager.channel;

        if (channel.pipeline().get("PacketInjector") != null) return;

        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, List<Object> list) throws Exception {
                list.add(packet);
                readPacket(player, packet);
            }
        });

        this.packetHologram = packetHologram;

    }

    @Override
    public void uninject(Player player) {
        if (channel.pipeline().get("PacketInjector") == null) return;
        channel.pipeline().remove("PacketInjector");
    }

    @Override
    public void readPacket(Player player, Packet<?> packet) {
        if (!packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) return;

        int id = (int) getValue(packet, "a");

        if (!packetHologram.getEntities().contains(id)) return;

        String actionType = getValue(packet, "action").toString();

        if (actionType.equals("INTERACT")) return;

        Bukkit.getScheduler().runTask(HoloShop.getInstance(), () -> Bukkit.getPluginManager().callEvent(new HologramInteractEvent(
                player, id, packetHologram, actionType.equals("ATTACK") ? HologramInteractType.ATTACK : HologramInteractType.INTERACT)));
    }


}

package cn.evolvefield.mods.morechickens.common.util;


import cn.evolvefield.mods.morechickens.common.net.Message;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class NetUtils {
    public NetUtils() {
    }

    public static void sendTo(SimpleChannel channel, ServerPlayer player, Message<?> message) {
        channel.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}

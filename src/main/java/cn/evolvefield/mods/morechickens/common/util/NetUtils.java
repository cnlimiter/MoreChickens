package cn.evolvefield.mods.morechickens.common.util;

import cn.evolvefield.mods.morechickens.common.net.Message;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetUtils {
    public NetUtils() {
    }

    public static void sendTo(SimpleChannel channel, ServerPlayerEntity player, Message<?> message) {
        channel.sendTo(message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}

package cn.evolvefield.mods.morechickens.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;


import java.util.function.Consumer;

public class EntityUtils {
    public EntityUtils() {
    }

    public static void forEachPlayerAround(ServerLevel world, BlockPos pos, double radius, Consumer<ServerPlayer> playerEntityConsumer) {
        world.getPlayers((player) -> {
            return player.distanceToSqr((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) <= radius * radius;
        }).forEach(playerEntityConsumer);
    }
}

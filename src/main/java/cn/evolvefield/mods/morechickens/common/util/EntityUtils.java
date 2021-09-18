package cn.evolvefield.mods.morechickens.common.util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Consumer;

public class EntityUtils {
    public EntityUtils() {
    }

    public static void forEachPlayerAround(ServerWorld world, BlockPos pos, double radius, Consumer<ServerPlayerEntity> playerEntityConsumer) {
        world.getPlayers((player) -> {
            return player.distanceToSqr((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) <= radius * radius;
        }).forEach(playerEntityConsumer);
    }
}

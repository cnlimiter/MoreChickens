package cn.evolvefield.mods.morechickens.common.tile.base;


import cn.evolvefield.mods.morechickens.common.util.EntityUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SyncableTileEntity extends BlockEntity {

    public SyncableTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void sync() {
        if (level instanceof ServerLevel) {
            EntityUtils.forEachPlayerAround((ServerLevel) level, getBlockPos(), 128D, this::syncContents);
        }
    }

    public void syncContents(ServerPlayer player) {
        player.connection.send(getUpdatePacket());
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return save(new CompoundTag());
    }

}
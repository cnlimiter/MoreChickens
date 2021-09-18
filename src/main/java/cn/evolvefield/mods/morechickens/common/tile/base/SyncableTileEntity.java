package cn.evolvefield.mods.morechickens.common.tile.base;

import cn.evolvefield.mods.morechickens.common.util.EntityUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;

public class SyncableTileEntity extends TileEntity {
    public SyncableTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public void sync() {
        if (level instanceof ServerWorld) {
            EntityUtils.forEachPlayerAround((ServerWorld) level, getBlockPos(), 128D, this::syncContents);
        }
    }

    public void syncContents(ServerPlayerEntity player) {
        player.connection.send(getUpdatePacket());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(getBlockState(), pkt.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }
}

package cn.evolvefield.mods.morechickens.common.net;

import cn.evolvefield.mods.atomlib.common.net.IPacket;
import cn.evolvefield.mods.morechickens.common.entity.core.ChickenIns;
import cn.evolvefield.mods.morechickens.init.handler.ChickenInsRegistryHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/6 21:47
 * Version: 1.0
 */
public class SyncChickenInsPacket extends IPacket<SyncChickenInsPacket> {
    private List<ChickenIns> chickens;

    public SyncChickenInsPacket() {
    }

    public SyncChickenInsPacket(List<ChickenIns> chickenIns) {
        this.chickens = chickenIns;
    }

    public List<ChickenIns> getChickens() {
        return this.chickens;
    }


    @Override
    public SyncChickenInsPacket read(FriendlyByteBuf buf) {
        var singularities = ChickenInsRegistryHandler.getInstance().readFromBuffer(buf);

        return new SyncChickenInsPacket(singularities);
    }

    @Override
    public void write(SyncChickenInsPacket msg, FriendlyByteBuf buf) {
        ChickenInsRegistryHandler.getInstance().writeToBuffer(buf);
    }

    @Override
    public void run(SyncChickenInsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().getReceptionSide().isServer())
            return;

        ctx.get().enqueueWork(() -> {
            ChickenInsRegistryHandler.getInstance().loadSingularities(msg);
        });

        ctx.get().setPacketHandled(true);
    }
}

package cn.evolvefield.mods.morechickens.common.net;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraftforge.fmllegacy.network.NetworkEvent;

public interface Message <T extends Message>{
    Dist getExecutingSide();

    default void executeServerSide(NetworkEvent.Context context) {
    }

    default void executeClientSide(NetworkEvent.Context context) {
    }

    T fromBytes(FriendlyByteBuf var1);

    void toBytes(FriendlyByteBuf var1);
}

package cn.evolvefield.mods.morechickens.init.registry;

import cn.evolvefield.mods.morechickens.common.net.Message;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class CommonRegistry {
    public CommonRegistry() {
    }

    public static SimpleChannel registerChannel(String modId, String name, int protocolVersion) {
        String protocolVersionString = String.valueOf(protocolVersion);
        return NetworkRegistry.newSimpleChannel(new ResourceLocation(modId, name), () -> {
            return protocolVersionString;
        }, (s) -> {
            return s.equals(protocolVersionString);
        }, (s) -> {
            return s.equals(protocolVersionString);
        });
    }
    public static SimpleChannel registerChannel(String modId, String name) {
        return NetworkRegistry.newSimpleChannel(new ResourceLocation(modId, name), () -> {
            return "1.0.0";
        }, (s) -> {
            return true;
        }, (s) -> {
            return true;
        });
    }


}

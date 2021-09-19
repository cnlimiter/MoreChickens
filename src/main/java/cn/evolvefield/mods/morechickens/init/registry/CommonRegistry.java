package cn.evolvefield.mods.morechickens.init.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

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

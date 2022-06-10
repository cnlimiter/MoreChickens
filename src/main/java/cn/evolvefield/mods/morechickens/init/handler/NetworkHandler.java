package cn.evolvefield.mods.morechickens.init.handler;

import cn.evolvefield.mods.atomlib.init.handler.NetBaseHandler;
import cn.evolvefield.mods.morechickens.Static;
import cn.evolvefield.mods.morechickens.common.net.SyncChickenInsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/6 21:55
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    public static final NetBaseHandler INSTANCE = new NetBaseHandler(new ResourceLocation(Static.MODID, "main"));

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        INSTANCE.register(SyncChickenInsPacket.class, new SyncChickenInsPacket());
    }
}

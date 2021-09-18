package cn.evolvefield.mods.morechickens.init.event;


import cn.evolvefield.mods.morechickens.MoreChickens;

import cn.evolvefield.mods.morechickens.client.render.entity.BaseChickenEntityRender;
import cn.evolvefield.mods.morechickens.client.render.tile.BaitRenderer;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import cn.evolvefield.mods.morechickens.init.ModTileEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventBus {

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        //entity
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.BASE_CHICKEN.get(), BaseChickenEntityRender::new);

        //tile
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.BAIT, BaitRenderer::new);


    }






}

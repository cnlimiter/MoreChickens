package cn.evolvefield.mods.morechickens.init.event;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.client.gui.ScreenBreeder;
import cn.evolvefield.mods.morechickens.client.gui.ScreenCollector;
import cn.evolvefield.mods.morechickens.client.gui.ScreenRoost;
import cn.evolvefield.mods.morechickens.client.render.entity.BaseChickenEntityRender;
import cn.evolvefield.mods.morechickens.client.render.tile.BaitRenderer;
import cn.evolvefield.mods.morechickens.init.*;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventBus {

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        //entity
        EntityRenderers.register(ModEntities.BASE_CHICKEN.get(), BaseChickenEntityRender::new);

        //tile
        BlockEntityRenderers.register(ModTileEntities.BAIT,BaitRenderer::new);
        //ClientRegistry.registerEntityShader(ModTileEntities.BAIT, BaitRenderer::new);


    }


    @SubscribeEvent
    public static void onRenderTypeSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOCK_ROOST, RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOCK_COLLECTOR, RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOCK_BREEDER, RenderType.cutout());

        });
    }

    @SubscribeEvent
    public static void ScreenInit(final FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            MenuScreens.register(ModContainers.CONTAINER_ROOST, ScreenRoost::new);
            MenuScreens.register(ModContainers.CONTAINER_BREEDER, ScreenBreeder::new);
            MenuScreens.register(ModContainers.CONTAINER_COLLECTOR, ScreenCollector::new);

        });
    }

}

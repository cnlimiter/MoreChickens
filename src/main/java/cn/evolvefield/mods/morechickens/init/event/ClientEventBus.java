package cn.evolvefield.mods.morechickens.init.event;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.client.gui.ScreenBreeder;
import cn.evolvefield.mods.morechickens.client.gui.ScreenCollector;
import cn.evolvefield.mods.morechickens.client.gui.ScreenRoost;
import cn.evolvefield.mods.morechickens.client.render.entity.BaseChickenEntityRender;
import cn.evolvefield.mods.morechickens.client.render.tile.BaitRenderer;
import cn.evolvefield.mods.morechickens.init.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
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
        RenderingRegistry.registerEntityRenderingHandler(ModDefaultEntities.BASE_CHICKEN.get(), BaseChickenEntityRender::new);

        //tile
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.BAIT, BaitRenderer::new);


    }


    @SubscribeEvent
    public static void onRenderTypeSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RenderTypeLookup.setRenderLayer(ModBlocks.BLOCK_ROOST, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.BLOCK_COLLECTOR, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.BLOCK_BREEDER, RenderType.cutout());

        });
    }

    @SubscribeEvent
    public static void ScreenInit(final FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            ScreenManager.register(ModContainers.CONTAINER_ROOST, ScreenRoost::new);
            ScreenManager.register(ModContainers.CONTAINER_BREEDER, ScreenBreeder::new);
            ScreenManager.register(ModContainers.CONTAINER_COLLECTOR, ScreenCollector::new);

        });
    }

}

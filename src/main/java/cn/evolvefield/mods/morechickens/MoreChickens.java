package cn.evolvefield.mods.morechickens;

import cn.evolvefield.mods.morechickens.common.data.ChickenRegistry;
import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.init.*;
import cn.evolvefield.mods.morechickens.init.registry.CommonRegistry;
import cn.evolvefield.mods.morechickens.integrations.top.TOPPlugin;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod("chickens")
public class MoreChickens {

    public static final String MODID = "chickens";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static SimpleChannel SIMPLE_CHANNEL;


    public MoreChickens() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ModItems::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ModBlocks::registerBlockItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, ModBlocks::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, ModContainers::registerContainers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueueEvent);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStared);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITIES.register(modEventBus);

        //config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, cn.evolvefield.mods.morechickens.init.ModConfig.CONFIG_SPEC, "more_chickens.toml");

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(MoreChickens.this::clientSetup));

    }


    public void onCommonSetup(FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GlobalEntityTypeAttributes.put(ModEntities.BASE_CHICKEN.get(), BaseChickenEntity.setAttributes().build());
            ChickenData.matchConfig();
            ModItems.matchConfig();
            ModEntities.registerPlacements();


        });
        SIMPLE_CHANNEL = CommonRegistry.registerChannel(MODID, "default");

    }


    public void onServerStarting(AddReloadListenerEvent event) {
        ChickenReloadListener.recipeManager = event.getDataPackRegistries().getRecipeManager();
        event.addListener(ChickenReloadListener.INSTANCE);
    }


    public void onServerStared(FMLServerStartedEvent event) {
        if (event.getServer().isDedicatedServer()){
            ChickenRegistry.buildFamilyTree();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        ModTileEntities.clientSetup();
        ModContainers.clientSetup();

    }


    private void onInterModEnqueueEvent(final InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe")) TOPPlugin.register();
    }

}

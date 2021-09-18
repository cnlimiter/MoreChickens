package cn.evolvefield.mods.morechickens;

import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
import cn.evolvefield.mods.morechickens.init.*;
import cn.evolvefield.mods.morechickens.init.registry.CommonRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;


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

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);


        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITIES.register(modEventBus);

        //config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, cn.evolvefield.mods.morechickens.init.ModConfig.CONFIG_SPEC, "more_chickens.toml");

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(MoreChickens.this::clientSetup));

    }


    public void onCommonSetup(FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            //Entity attribute assignments
            GlobalEntityTypeAttributes.put(ModEntities.BASE_CHICKEN.get(), BaseChickenEntity.setAttributes().build());
            ChickenType.matchConfig();
            ModItems.matchConfig();
            ModEntities.registerPlacements();

        });
        SIMPLE_CHANNEL = CommonRegistry.registerChannel(MODID, "default");

    }

    public void onServerStarting(AddReloadListenerEvent event) {
        ChickenReloadListener.recipeManager = event.getDataPackRegistries().getRecipeManager();
        event.addListener(ChickenReloadListener.INSTANCE);
    }

    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        ModTileEntities.clientSetup();
        ModContainers.clientSetup();

//        MinecraftForge.EVENT_BUS.register(new ModSoundEvents());
//        MinecraftForge.EVENT_BUS.register(new GuiEvents());

//        PICKUP_KEY = ClientRegistry.registerKeyBinding("key.easy_villagers.pick_up", "category.easy_villagers", GLFW.GLFW_KEY_V);
//        CYCLE_TRADES_KEY = ClientRegistry.registerKeyBinding("key.easy_villagers.cycle_trades", "category.easy_villagers", GLFW.GLFW_KEY_C);
//
    }

}

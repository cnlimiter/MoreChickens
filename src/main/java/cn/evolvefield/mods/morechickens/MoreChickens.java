package cn.evolvefield.mods.morechickens;

import cn.evolvefield.mods.morechickens.core.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.core.entity.util.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.init.*;
import cn.evolvefield.mods.morechickens.core.entity.util.main.ChickenType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod("chickens")
public class MoreChickens {

    public static final String MODID = "chickens";
    public static final Logger LOGGER = LogManager.getLogger(MODID);


    public MoreChickens() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);


        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModDefaultEntities.ENTITIES.register(modEventBus);

        //config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, cn.evolvefield.mods.morechickens.init.ModConfig.CONFIG_SPEC, "more_chickens.toml");

    }


    public void onCommonSetup(FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            //Entity attribute assignments
            GlobalEntityTypeAttributes.put(ModDefaultEntities.BASE_CHICKEN.get(), BaseChickenEntity.setAttributes().build());
            ChickenType.matchConfig();
            ModDefaultEntities.registerPlacements();

        });
    }

    public void onServerStarting(AddReloadListenerEvent event) {
        ChickenReloadListener.recipeManager = event.getDataPackRegistries().getRecipeManager();
        event.addListener(ChickenReloadListener.INSTANCE);
    }

}

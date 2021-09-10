package cn.evolvefield.mods.morechickens;


import cn.evolvefield.mods.morechickens.core.data.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.core.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.core.util.main.ChickenType;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@Mod("chickens")
public class MoreChickens {

    public static final String MODID = "chickens";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public MoreChickens() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addAttributes);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITIES.register(modEventBus);

        //config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, cn.evolvefield.mods.morechickens.init.ModConfig.CONFIG_SPEC, "more_chickens.toml");

    }
    private void addAttributes(final EntityAttributeCreationEvent event) {
        event.put(ModEntities.BASE_CHICKEN.get(), BaseChickenEntity.setAttributes().build());
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {

            ChickenType.matchConfig();
            ModEntities.registerPlacements();
    }

    public void onServerStarting(AddReloadListenerEvent event) {
        ChickenReloadListener.recipeManager = event.getDataPackRegistries().getRecipeManager();
        event.addListener(ChickenReloadListener.INSTANCE);
    }
}

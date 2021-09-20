package cn.evolvefield.mods.morechickens;


import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.item.ColorEggItem;
import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
import cn.evolvefield.mods.morechickens.init.*;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addAttributes);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ModItems::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ModBlocks::registerBlockItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, ModBlocks::registerBlocks);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
       // MinecraftForge.EVENT_BUS.addListener(this::EggRegistry);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITIES.register(modEventBus);

        //config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, cn.evolvefield.mods.morechickens.init.ModConfig.CONFIG_SPEC, "more_chickens.toml");

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup));

    }

    private void addAttributes(final EntityAttributeCreationEvent event) {
        event.put(ModEntities.BASE_CHICKEN.get(), BaseChickenEntity.setAttributes().build());
    }

    private void EggRegistry(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(ModEntities.COLOR_EGG.get(), ThrownItemRenderer::new);

    }

    public void onCommonSetup(FMLCommonSetupEvent event) {

            ChickenType.matchConfig();
            ModItems.matchConfig();
            ModEntities.registerPlacements();

            event.enqueueWork(() -> {
                ColorEggItem.registerDispenser();
            });
    }

    public void onServerStarting(AddReloadListenerEvent event) {
        ChickenReloadListener.recipeManager = event.getDataPackRegistries().getRecipeManager();
        event.addListener(ChickenReloadListener.INSTANCE);
    }

    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        ModTileEntities.clientSetup();
        ModContainers.clientSetup();
        EntityRenderers.register(ModEntities.COLOR_EGG.get(),ThrownItemRenderer::new);
    }


}

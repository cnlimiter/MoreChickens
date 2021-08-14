package cn.evolvefield.mods.morechickens;


import cn.evolvefield.mods.morechickens.core.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.core.entity.util.ChickenType;
import cn.evolvefield.mods.morechickens.init.ModDefaultEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("chickens")
public class MoreChickens {

    public static final String MODID = "chickens";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public MoreChickens() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }


    public void onCommonSetup(FMLCommonSetupEvent event) {
        //DeferredWorkQueue.lookup(() -> {
            //Entity attribute assignments
            Map<EntityType<? extends LivingEntity>, AttributeSupplier> SUPPLIERS = ObfuscationReflectionHelper.getPrivateValue(DefaultAttributes.class,null,"f_22294_");
            assert SUPPLIERS != null;
            SUPPLIERS.put(ModDefaultEntities.BASE_CHICKEN.get(), BaseChickenEntity.setAttributes().build());
            //GlobalEntityTypeAttributes.put(ModDefaultEntities.BASE_CHICKEN.get(), BaseChickenEntity.setAttributes().build());
            ChickenType.matchConfig();
            //ModItems.matchConfig();
            ModDefaultEntities.registerPlacements();

        //});
    }


}

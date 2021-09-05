package cn.evolvefield.mods.morechickens.init.event;


import cn.evolvefield.mods.morechickens.MoreChickens;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventBus {

    private static final Logger logger = LogManager.getLogger();

//    @SubscribeEvent
//    public static void biomeGeneration(BiomeLoadingEvent event){
//        Biome.Category category = event.getCategory();
//        int min = ModConfig.COMMON.chickenMin.get();
//        int max = ModConfig.COMMON.chickenMax.get();
//        if(min > max){
//            int tmp = min;
//            min = max;
//            max = tmp;
//        }
//        if(category != Biome.Category.NETHER && category != Biome.Category.THEEND && category != Biome.Category.OCEAN){
//            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(ModEntities.BASE_CHICKEN,
//                    ModConfig.COMMON.chickenWeight.get(),
//                    min,
//                    max));
//        }
//    }



}

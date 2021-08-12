package cn.evolvefield.mods.morechickens.init.event;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

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

package cn.evolvefield.mods.morechickens.init;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.core.entity.ColorEggEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.BiFunction;
import java.util.function.Supplier;


@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
//    public static EntityType<BaseChickenEntity> BASE_CHICKEN;


    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        final IForgeRegistry<EntityType<?>> registry = event.getRegistry();

//        registry.register(
//
//                BASE_CHICKEN = registerEntity(EntityType.Builder.of(BaseChickenEntity::new, EntityClassification.CREATURE)
//                        .sized(0.375f, 0.625f)
//                        ,new ResourceLocation(MoreChickens.MODID, "base_chicken").toString()
//                        , "base_chicken")
//
//        );

    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> EntityType<T> registerEntity(EntityType.Builder<T> builder, int trackingRange, BiFunction<FMLPlayMessages.SpawnEntity, World, T> customClientFactory, String resourceLocation, String name) {
        return (EntityType<T>) builder
                .setTrackingRange(trackingRange)
                .setCustomClientFactory(customClientFactory)
                .build(resourceLocation)
                .setRegistryName(name);
    }


    @SuppressWarnings("unchecked")
    private static <T extends Entity> EntityType<T> registerEntity(EntityType.Builder<T> builder, String resourceLocation,String name) {
        return (EntityType<T>) builder.build(resourceLocation).setRegistryName(name);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> EntityType<T> registerEntity(EntityType.Builder<T> builder,String name) {
        return (EntityType<T>) builder.build(name).setRegistryName(name);
    }



    //

}

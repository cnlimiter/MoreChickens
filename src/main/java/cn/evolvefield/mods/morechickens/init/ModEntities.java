package cn.evolvefield.mods.morechickens.init;


import cn.evolvefield.mods.morechickens.MoreChickens;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.FMLPlayMessages;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.BiFunction;


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
    private static <T extends Entity> EntityType<T> registerEntity(EntityType.Builder<T> builder, int trackingRange, BiFunction<FMLPlayMessages.SpawnEntity, Level, T> customClientFactory, String resourceLocation, String name) {
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

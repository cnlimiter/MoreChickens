package cn.evolvefield.mods.morechickens.init;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.entity.ColorEggEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MoreChickens.MODID);

    public static final RegistryObject<EntityType<BaseChickenEntity>> BASE_CHICKEN = ENTITIES.register("base_chicken",
            () -> EntityType.Builder.of(BaseChickenEntity::new, EntityClassification.CREATURE).sized(0.375f, 0.625f)
                    .build(new ResourceLocation(MoreChickens.MODID, "base_chicken").toString()));


    public static final RegistryObject<EntityType<ColorEggEntity>> COLOR_EGG = ENTITIES.register("color_egg",
            () -> EntityType.Builder.<ColorEggEntity>of(ColorEggEntity::new, EntityClassification.MISC).sized(0.25f, 0.25f)
                    .setTrackingRange(4)
                    .setCustomClientFactory(ColorEggEntity::new)
                    .build(new ResourceLocation(MoreChickens.MODID, "color_egg").toString()));

    public static void registerPlacements() {
        EntitySpawnPlacementRegistry.register(BASE_CHICKEN.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::checkAnimalSpawnRules);
    }
}

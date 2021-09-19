package cn.evolvefield.mods.morechickens.init;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.entity.ColorEggEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MoreChickens.MODID);

    public static final RegistryObject<EntityType<BaseChickenEntity>> BASE_CHICKEN = ENTITIES.register("base_chicken",
            () -> EntityType.Builder.of(BaseChickenEntity::new, MobCategory.CREATURE).sized(0.375f, 0.625f)
                    .build(new ResourceLocation(MoreChickens.MODID, "base_chicken").toString()));

    public static final RegistryObject<EntityType<ColorEggEntity>> COLOR_EGG = ENTITIES.register("color_egg",
            () -> EntityType.Builder.<ColorEggEntity>of(ColorEggEntity::new, MobCategory.MISC).sized(0.25f, 0.25f)
                    .setTrackingRange(4)
                    .setCustomClientFactory(ColorEggEntity::new)
                    .build(new ResourceLocation(MoreChickens.MODID, "color_egg").toString()));

    public static void registerPlacements() {
        SpawnPlacements.register(BASE_CHICKEN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
    }
}

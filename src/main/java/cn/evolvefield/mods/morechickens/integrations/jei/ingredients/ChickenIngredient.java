package cn.evolvefield.mods.morechickens.integrations.jei.ingredients;

import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ChickenIngredient
{
    private static Map<ChickenIngredient, Entity> cache = new HashMap<>();

    private EntityType<? extends Entity> chicken;
    private ChickenType chickenType;

    public ChickenIngredient(EntityType<? extends Entity> chicken) {
        this.chicken = chicken;
    }

    public ChickenIngredient(EntityType<? extends Entity> chicken, ChickenType chickenType) {
        this(chicken);
        this.chickenType = chickenType;
    }

    public EntityType<? extends Entity> getChickenEntity() {
        return chicken;
    }

    public Entity getCachedEntity(World world) {
        if (!cache.containsKey(this)) {
            Entity newChicken = getChickenEntity().create(world);

                ((BaseChickenEntity) newChicken).setType(chickenType);
                BaseChickenEntity.setAttributes();

            cache.put(this, newChicken);
        }
        Entity cachedEntity = cache.get(this);
        if (cachedEntity instanceof ChickenEntity) {
            return cachedEntity;
        }
        return null;
    }


    public String getChickenType() {
        return chickenType.name;
    }

    public static ChickenIngredient fromNetwork(PacketBuffer buffer) {
        String chickenName = buffer.readUtf();

        return new ChickenIngredient(ModEntities.BASE_CHICKEN.get(), ChickenType.Types.get(chickenName));
    }

    public final void toNetwork(PacketBuffer buffer) {
        buffer.writeUtf("" + chicken.getRegistryName());
        buffer.writeUtf(chickenType.name);
    }

    @Override
    public String toString() {
        return "ChickenIngredient{" +
                "chicken=" + chicken +
                ", chickenType=" + chickenType.name +
                '}';
    }

}

package cn.evolvefield.mods.morechickens.integrations.jei.ingredients;

import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
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
    private ResourceLocation chickenType;

    public ChickenIngredient(EntityType<? extends Entity> chicken) {
        this.chicken = chicken;
    }

    public ChickenIngredient(EntityType<? extends Entity> chicken, ResourceLocation chickenType) {
        this(chicken);
        this.chickenType = chickenType;
    }



    public EntityType<? extends Entity> getChickenEntity() {
        return chicken;
    }

    public BaseChickenEntity getCachedEntity(World world) {
        if (!cache.containsKey(this)) {
            Entity newChicken = getChickenEntity().create(world);
            if (newChicken instanceof BaseChickenEntity) {
                ((BaseChickenEntity) newChicken).setChickenName(getChickenType().toString());
                BaseChickenEntity.setAttributes();
            }
            cache.put(this, newChicken);
        }
        return null;
    }

    /**
     * productivebees:osmium, prouctivebees:leafcutter_bee
     */
    public ResourceLocation getChickenType() {
        return chickenType != null ? chickenType : chicken.getRegistryName();
    }

    public static ChickenIngredient fromNetwork(PacketBuffer buffer) {
        String chickenName = buffer.readUtf();

        return new ChickenIngredient(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(chickenName)), buffer.readResourceLocation());
    }

    public final void toNetwork(PacketBuffer buffer) {
        buffer.writeUtf("" + chicken.getRegistryName());
        buffer.writeResourceLocation(getChickenType());
    }

    @Override
    public String toString() {
        return "ChickenIngredient{" +
                "chicken=" + chicken +
                ", chickenType=" + chickenType +
                '}';
    }

}

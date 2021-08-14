package cn.evolvefield.mods.morechickens.core.data;


import cn.evolvefield.mods.morechickens.init.ModConfig;
import cn.evolvefield.mods.morechickens.init.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class DataChickenVanilla extends DataChicken {
    public DataChickenVanilla() {
        super("vanilla", "entity.Chicken.name");
    }


    private static final String VANILLA_TYPE = "minecraft:chicken";

    public static DataChicken getDataFromStack(ItemStack stack) {
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound == null || !tagCompound.getString(CHICKEN_ID_KEY).equals(VANILLA_TYPE)) return null;
        return new DataChickenVanilla();
    }

    public static DataChicken getDataFromTooltipNBT(CompoundTag tagCompound) {
        if (tagCompound == null || !tagCompound.getString(CHICKEN_ID_KEY).equals(VANILLA_TYPE)) return null;
        return new DataChickenVanilla();
    }

    public static DataChicken getDataFromEntity(Entity entity) {
        if (entity instanceof Chicken) return new DataChickenVanilla();
        return null;
    }

    public static DataChicken getDataFromName(String name) {
        if (name.equals("minecraft:vanilla")) return new DataChickenVanilla();
        return null;
    }

    public static void addAllChickens(List<DataChicken> chickens) {
        chickens.add(new DataChickenVanilla());
    }


    @Override
    public boolean isEqual(DataChicken other) {
        return (other instanceof DataChickenVanilla);
    }

    @Override
    public ItemStack createDropStack() {

        Item item = rand.nextInt(3) > 0 && !ModConfig.COMMON.disableEggLaying.get() ? Items.EGG : Items.FEATHER;
        return new ItemStack(item, 1);
    }

    @Override
    public Chicken buildEntity(Level world) {
        return new Chicken(EntityType.CHICKEN,world);
    }

    @Override
    public void spawnEntity(Level world, BlockPos pos) {
        Chicken chicken = new Chicken(EntityType.CHICKEN,world);
        chicken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        chicken.finalizeSpawn(world.getServer().overworld(),world.getCurrentDifficultyAt(pos), null,null,null);
        chicken.setAge(getLayTime());
        world.getServer().overworld().addFreshEntity(chicken);
    }

    @Override
    public ItemStack buildChickenStack() {
        ItemStack stack = new ItemStack(ModItems.ITEM_CHICKEN);
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.putString(CHICKEN_ID_KEY, VANILLA_TYPE);
        stack.setTag(tagCompound);
        return stack;
    }

    @Override
    public CompoundTag buildTooltipNBT() {
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.putString(CHICKEN_ID_KEY, VANILLA_TYPE);
        return tagCompound;
    }

    @Override
    public boolean hasParents() {
        return true;
    }

    @Override
    public List<ItemStack> buildParentChickenStack() {
        return Arrays.asList(buildChickenStack(), buildChickenStack());
    }

    @Override
    public ItemStack buildCaughtFromStack() {
        ItemStack stack = new ItemStack(Items.SPAWNER);
        //ItemMonsterPlacer.applyEntityIdToItemStack(stack, new ResourceLocation("chicken"));
        return stack;
    }

    @Override
    public String toString() {
        return "DataChickenVanilla [name=" + getName() + "]";
    }

    public String getChickenType() {
        return "minecraft:vanilla";
    }
}

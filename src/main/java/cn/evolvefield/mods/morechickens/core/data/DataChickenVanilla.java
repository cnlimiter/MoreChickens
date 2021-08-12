package cn.evolvefield.mods.morechickens.core.data;


import cn.evolvefield.mods.morechickens.init.ModConfig;
import cn.evolvefield.mods.morechickens.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class DataChickenVanilla extends DataChicken {
    public DataChickenVanilla() {
        super("vanilla", "entity.Chicken.name");
    }


    private static final String VANILLA_TYPE = "minecraft:chicken";

    public static DataChicken getDataFromStack(ItemStack stack) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound == null || !tagCompound.getString(CHICKEN_ID_KEY).equals(VANILLA_TYPE)) return null;
        return new DataChickenVanilla();
    }

    public static DataChicken getDataFromTooltipNBT(CompoundNBT tagCompound) {
        if (tagCompound == null || !tagCompound.getString(CHICKEN_ID_KEY).equals(VANILLA_TYPE)) return null;
        return new DataChickenVanilla();
    }

    public static DataChicken getDataFromEntity(Entity entity) {
        if (entity instanceof ChickenEntity) return new DataChickenVanilla();
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
    public ChickenEntity buildEntity(World world) {
        return new ChickenEntity(EntityType.CHICKEN,world);
    }

    @Override
    public void spawnEntity(World world, BlockPos pos) {
        ChickenEntity chicken = new ChickenEntity(EntityType.CHICKEN,world);
        chicken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        chicken.finalizeSpawn(world.getServer().overworld(),world.getCurrentDifficultyAt(pos), null,null,null);
        chicken.setAge(getLayTime());
        world.getServer().overworld().addFreshEntity(chicken);
    }

    @Override
    public ItemStack buildChickenStack() {
        ItemStack stack = new ItemStack(ModItems.ITEM_CHICKEN);
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putString(CHICKEN_ID_KEY, VANILLA_TYPE);
        stack.setTag(tagCompound);
        return stack;
    }

    @Override
    public CompoundNBT buildTooltipNBT() {
        CompoundNBT tagCompound = new CompoundNBT();
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

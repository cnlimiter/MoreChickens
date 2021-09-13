package cn.evolvefield.mods.morechickens.common.data;

import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import cn.evolvefield.mods.morechickens.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataChickenModded extends DataChicken {


    private ChickenType chicken;

    public DataChickenModded(ChickenType chickenIn, CompoundNBT compound) {
        super(chickenIn.name, "entity." + chickenIn.name + ".name");
    }

    public static DataChicken getDataFromEntity(Entity entity) {
        if (entity instanceof BaseChickenEntity) {
            CompoundNBT tagCompound = entity.saveWithoutId(new CompoundNBT());
            ChickenType chicken = ChickenType.Types.get(tagCompound.getString(CHICKEN_ID_KEY));
            if (chicken != null) return new DataChickenModded(chicken, tagCompound);
        }
        return null;
    }

    public static DataChicken getDataFromTooltipNBT(CompoundNBT tagCompound) {
        ChickenType chicken = ChickenType.Types.get(tagCompound.getString(CHICKEN_ID_KEY));
        if (chicken != null) return new DataChickenModded(chicken, tagCompound);
        return null;
    }

    public static DataChicken getDataFromStack(ItemStack stack) {
        ChickenType chicken = chickensRegistryItemForStack(stack);
        if (chicken != null) return new DataChickenModded(chicken, stack.getTag());

        return null;
    }
    public static DataChicken getDataFromName(String name) {
        ChickenType chicken = ChickenType.Types.get(name);
        if (chicken != null) return new DataChickenModded(chicken, null);

        return null;
    }

    public static void addAllChickens(List<DataChicken> chickens) {
        for (ChickenType item : getChickenRegistryItems()) {
            chickens.add(new DataChickenModded(item, null));
        }
    }

    private static List<ChickenType> getChickenRegistryItems() {
        List<ChickenType> list = new ArrayList<>(ChickenType.Types.values());
        return list;
    }

    private static ChickenType chickensRegistryItemForStack(ItemStack stack) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound == null) return null;
        return ChickenType.Types.get(tagCompound.getString(CHICKEN_ID_KEY));
    }

    public String getChickenType() {
        return chicken.name;
    }


    @Override
    public AnimalEntity buildEntity(World world) {
        BaseChickenEntity chicken = new BaseChickenEntity(ModEntities.BASE_CHICKEN.get(),world);
        chicken.setType(ChickenType.Types.get(getChickenType()));
        return chicken;
    }

    @Override
    public void spawnEntity(World world, BlockPos pos) {
        BaseChickenEntity chicken = new BaseChickenEntity(ModEntities.BASE_CHICKEN.get(),world);
        chicken.setDeltaMovement(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        chicken.setType(ChickenType.Types.get(getChickenType()));
        //chicken.ageUp(getLayTime());
        world.addFreshEntity(chicken);
    }

    @Override
    public ItemStack buildChickenStack() {
        ItemStack stack = new ItemStack(ModItems.ITEM_CHICKEN);
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putString(CHICKEN_ID_KEY, chicken.name);
        stack.setTag(tagCompound);
        return stack;
    }

    @Override
    public boolean hasParents() {
        return chicken.parent1 != null && chicken.parent2 != null;
    }

    @Override
    public List<ItemStack> buildParentChickenStack() {
        if (!hasParents()) return null;
        DataChicken parent1 = new DataChickenModded(ChickenType.Types.get(chicken.parent1), null);
        DataChicken parent2 = new DataChickenModded(ChickenType.Types.get(chicken.parent2), null);
        return Arrays.asList(parent1.buildChickenStack(), parent2.buildChickenStack());

    }
}

package cn.evolvefield.mods.morechickens.core.data;//package cn.evolvefield.mods.morechickens.core.data;
//
//import cn.evolvefield.mods.morechickens.core.entity.BaseChickenEntity;
//import cn.evolvefield.mods.morechickens.core.util.main.ChickenType;
//import cn.evolvefield.mods.morechickens.init.ModEntities;
//import cn.evolvefield.mods.morechickens.init.ModItems;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.passive.AnimalEntity;
//import net.minecraft.entity.passive.ChickenEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//import java.util.*;
//
//public class DataChickenModded extends DataChicken{
//
//    private static final String BREED_NAME = "breed";
//    private ChickenType chicken;
//
//    public DataChickenModded(ChickenType chickenIn, CompoundNBT compound) {
//        super(chickenIn.getTypeName(), "entity." + chickenIn.getTypeName() + ".name");
//    }
//
//    public static DataChicken getDataFromEntity(Entity entity) {
//        if (entity instanceof BaseChickenEntity) {
//            CompoundNBT tagCompound = entity.saveWithoutId(new CompoundNBT());
//            ChickenType chicken = ChickenType.getByChickenTypeName(tagCompound.getString(BREED_NAME));
//            if (chicken != null) return new DataChickenModded(chicken, tagCompound);
//        }
//        return null;
//    }
//
//    public static DataChicken getDataFromTooltipNBT(CompoundNBT tagCompound) {
//        ChickenType chicken = ChickenType.getByChickenTypeName(tagCompound.getString(CHICKEN_ID_KEY));
//        if (chicken != null) return new DataChickenModded(chicken, tagCompound);
//        return null;
//    }
//
//    public static DataChicken getDataFromStack(ItemStack stack) {
//        ChickenType chicken = chickensRegistryItemForStack(stack);
//        if (chicken != null) return new DataChickenModded(chicken, stack.getTag());
//
//        return null;
//    }
//    public static DataChicken getDataFromName(String name) {
//        ChickenType chicken = ChickenType.getByChickenTypeName(name);
//        if (chicken != null) return new DataChickenModded(chicken, null);
//
//        return null;
//    }
//
//    public static void addAllChickens(List<DataChicken> chickens) {
//        for (ChickenType item : getChickenRegistryItems()) {
//            chickens.add(new DataChickenModded(item, null));
//        }
//    }
//
//    private static List<ChickenType> getChickenRegistryItems() {
//        Comparator<ChickenType> comparator = new Comparator<ChickenType>() {
//            @Override
//            public int compare(ChickenType left, ChickenType right) {
//                if (left.getTier() != right.getTier()) return left.getTier() - right.getTier();
//                return left.getTypeName().compareTo(right.getTypeName());
//            }
//        };
//
//        Collection<ChickenType> chickens = ChickenType.getItems();
//        List<ChickenType> list = new ArrayList<ChickenType>(chickens);
//        Collections.sort(list, comparator);
//        return list;
//    }
//
//    private static ChickenType chickensRegistryItemForStack(ItemStack stack) {
//        CompoundNBT tagCompound = stack.getTag();
//        if (tagCompound == null) return null;
//        return ChickenType.getByChickenTypeName(tagCompound.getString(CHICKEN_ID_KEY));
//    }
//
//    public String getChickenType() {
//        return chicken.getTypeName();
//    }
//
//    public CompoundNBT createTagCompound() {
//        CompoundNBT tagCompound = new CompoundNBT();
////        tagCompound.setInteger(GAIN_KEY, gain);
////        tagCompound.setInteger(GROWTH_KEY, growth);
////        tagCompound.setInteger(STRENGTH_KEY, strength);
//        return tagCompound;
//    }
//
//    @Override
//    public AnimalEntity buildEntity(World world) {
//        BaseChickenEntity chicken = new BaseChickenEntity(ModEntities.BASE_CHICKEN,world);
//        //chicken.readAdditionalSaveData(createTagCompound());
//        chicken.setBreed(Objects.requireNonNull(ChickenType.getByChickenTypeName(getChickenType())));
//        return chicken;
//    }
//
//    @Override
//    public void spawnEntity(World world, BlockPos pos) {
//        BaseChickenEntity chicken = new BaseChickenEntity(ModEntities.BASE_CHICKEN,world);
//        //chicken.readAdditionalSaveData(createTagCompound());
//        chicken.setDeltaMovement(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
//        chicken.setBreed(Objects.requireNonNull(ChickenType.getByChickenTypeName(getChickenType())));
//        chicken.ageUp(getLayTime());
//        world.addFreshEntity(chicken);
//    }
//
//    @Override
//    public ItemStack buildChickenStack() {
//        ItemStack stack = new ItemStack(ModItems.ITEM_CHICKEN);
//        CompoundNBT tagCompound = new CompoundNBT();
//        tagCompound.putString(CHICKEN_ID_KEY, chicken.getTypeName());
//        stack.setTag(tagCompound);
//        return stack;
//    }
//
//    @Override
//    public boolean hasParents() {
//        return chicken.getParent1() != null && chicken.getParent2() != null;
//    }
//
//    @Override
//    public List<ItemStack> buildParentChickenStack() {
//        if (!hasParents()) return null;
//        DataChicken parent1 = new DataChickenModded(chicken.getParent1(), null);
//        DataChicken parent2 = new DataChickenModded(chicken.getParent2(), null);
//        return Arrays.asList(parent1.buildChickenStack(), parent2.buildChickenStack());
//
//    }
//}

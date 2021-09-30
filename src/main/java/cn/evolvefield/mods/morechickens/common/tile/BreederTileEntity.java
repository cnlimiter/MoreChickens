package cn.evolvefield.mods.morechickens.common.tile;

import cn.evolvefield.mods.morechickens.common.container.BreederContainer;
import cn.evolvefield.mods.morechickens.common.container.base.ItemListInventory;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.tile.base.FakeWorldTileEntity;
import cn.evolvefield.mods.morechickens.common.data.Gene;
import cn.evolvefield.mods.morechickens.init.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class BreederTileEntity extends FakeWorldTileEntity implements ITickableTileEntity, INamedContainerProvider {

    private NonNullList<ItemStack> foodInventory;
    private NonNullList<ItemStack> outputInventory;
    private ItemStack chicken1;
    private BaseChickenEntity chickenEntity1;
    private ItemStack chicken2;
    private BaseChickenEntity chickenEntity2;
    Random random = new Random();
    private int progress;
    private int timeElapsed = 0;
    private int timeUntilNextSpawn = 0;

    public final IIntArray dataAccess = new IIntArray() {
        public int get(int id) {
            switch (id) {
                case 0:
                    return progress;
                default:
                    return 0;
            }
        }

        public void set(int id, int value) {
            switch (id) {
                case 0:
                    progress = value;
                    break;
            }
        }

        public int getCount() {
            return 2;
        }
    };

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new BreederContainer(ModContainers.BREEDER_CONTAINER,id, playerInventory, getFoodInventory(), getOutputInventory(),dataAccess,this);
    }

    public BreederTileEntity() {
        super(ModTileEntities.BREEDER, ModBlocks.BLOCK_BREEDER.defaultBlockState());
        foodInventory = NonNullList.withSize(1, ItemStack.EMPTY);
        outputInventory = NonNullList.withSize(4, ItemStack.EMPTY);
        chicken1 = ItemStack.EMPTY;
        chicken2 = ItemStack.EMPTY;
    }

    public BaseChickenEntity getChicken(World world, ItemStack stack) {
        String type = getChicken1().getOrCreateTag().getString("Type");
        CompoundNBT compound = stack.getOrCreateTag();
//        AnimalEntity chicken;
//        if (type.equals("vanilla")){
//            chicken = EntityType.CHICKEN.create(world);
//            return chicken;
//        }

        BaseChickenEntity chicken = new BaseChickenEntity(ModEntities.BASE_CHICKEN.get(), world);
        chicken.readAdditionalSaveData(compound);
        return chicken;
    }

    public void setChicken(ItemStack stack, AnimalEntity chickenEntity) {
        CompoundNBT compound = stack.getOrCreateTagElement("ChickenData");
        if (chickenEntity instanceof ChickenEntity){
            compound.putString("Type", "vanilla");
        }
        else {
            chickenEntity.addAdditionalSaveData(compound);
            compound.putString("Type", "modded");
        }
        stack.setTag(compound);
    }

    public ItemStack getChicken1() {
        return chicken1;
    }

    public ItemStack getChicken2() {
        return chicken2;
    }

    public String getChicken1Name(){
        return getChicken1().getOrCreateTag().getString("Name");
    }

    public String getChicken2Name(){
        return getChicken2().getOrCreateTag().getString("Name");
    }

    public boolean hasChicken1() {
        return !chicken1.isEmpty();
    }

    public boolean hasChicken2() {
        return !chicken2.isEmpty();
    }

    public BaseChickenEntity getChickenEntity1() {
        if (chickenEntity1 == null && !chicken1.isEmpty()) {
            chickenEntity1 = getChicken(level, chicken1);
        }
        return chickenEntity1;
    }

    public BaseChickenEntity getChickenEntity2() {
        if (chickenEntity2 == null && !chicken2.isEmpty()) {
            chickenEntity2 = getChicken(level, chicken2);
        }
        return chickenEntity2;
    }

    public void setChicken1(ItemStack villager) {
        this.chicken1 = villager;
        if (villager.isEmpty()) {
            chickenEntity1 = null;
        } else {
            chickenEntity1 = getChicken(level, villager);
        }
        setChanged();
        sync();
    }

    public void setChicken2(ItemStack villager) {
        this.chicken2 = villager;
        if (villager.isEmpty()) {
            chickenEntity2 = null;
        } else {
            chickenEntity2 = getChicken(level, villager);
        }
        setChanged();
        sync();
    }

    public ItemStack removeChicken1() {
        ItemStack v = chicken1;
        setChicken1(ItemStack.EMPTY);
        return v;
    }

    public ItemStack removeChicken2() {
        ItemStack v = chicken2;
        setChicken2(ItemStack.EMPTY);
        return v;
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            return;
        }

        updateProgress();
        updateTimerIfNeeded();
        spawnChickenDropIfNeeded();
    }

    private void updateTimerIfNeeded() {
        if ( seedIsFull() && canBreed() && !outputIsFull()) {
            timeElapsed += 20;
            setChanged();
        }
    }

    private void updateProgress() {
        progress = timeUntilNextSpawn == 0 ? 0 : (timeElapsed * 1000 / timeUntilNextSpawn);
    }

    public double getProgress() {
        return progress / 1000.0;
    }


    private void spawnChickenDropIfNeeded() {
        if (canBreed() && seedIsFull()&& (timeElapsed >= timeUntilNextSpawn)) {
            if (timeUntilNextSpawn > 0) {
                removeBreedingItems();
                if(addChicken()){
                    getLevel().playSound(null, getBlockPos(), SoundEvents.CHICKEN_EGG, SoundCategory.NEUTRAL, 0.5F, 0.8F);
                    spawnParticles();
                }
            }
            resetTimer();
        }
    }

    private void resetTimer() {
        timeElapsed = 0;
        timeUntilNextSpawn = 6000;

        setChanged();
    }



    public void spawnParticles() {
        spawnParticle(-0.1d, 0.5d, 0, 0.2d);
        spawnParticle(0.5d, -0.1d, 0.2d, 0);
        spawnParticle(1.1d, 0.5d, 0, 0.2d);
        spawnParticle(0.5d, 1.1d, 0.2d, 0);
    }

    private void spawnParticle(double x, double z, double xOffset, double zOffset) {
        if (getLevel() instanceof ServerWorld) {
            ServerWorld worldServer = (ServerWorld) getLevel();
            worldServer.addParticle(ParticleTypes.HEART, false,getBlockPos().getX() + x, getBlockPos().getY() + 0.5d, getBlockPos().getZ() + z,  xOffset, 0.2d, zOffset);
        }
    }


    private boolean addChicken() {
        for (int i = 0; i < outputInventory.size(); i++) {
            if (outputInventory.get(i).isEmpty()) {
                String typeA = getChicken1().getOrCreateTag().getString("Type");
                String typeB = getChicken2().getOrCreateTag().getString("Type");
                ItemStack chickenItem = new ItemStack(ModItems.ITEM_CHICKEN);
                if(typeA.equals("modded") && typeB.equals("modded")) {
                    BaseChickenEntity child = ModEntities.BASE_CHICKEN.get().create(level);
                    if(child != null) {
                        Gene childA = getChickenEntity1().alleleA.crossover(getChickenEntity1().alleleB, random);
                        Gene childB = getChickenEntity2().alleleA.crossover(getChickenEntity2().alleleB, random);
                        child.setAlleles(childA, childB);
                        child.setType(getChickenEntity1().type.getOffspring(getChickenEntity2().type, random));
                    }
                    CompoundNBT tagCompound = chickenItem.getOrCreateTagElement("ChickenData");
                    child.addAdditionalSaveData(tagCompound);
                    tagCompound.putString("Type", "modded");
                    chickenItem.setTag(tagCompound);

                }
                else if(typeA.equals("vanilla") || typeB.equals("vanilla")){
                    CompoundNBT tagCompound = chickenItem.getOrCreateTagElement("ChickenData");
                    tagCompound.putString("Type", "vanilla");
                    chickenItem.setTag(tagCompound);

                }
                outputInventory.set(i, chickenItem);
                return true;
            }
        }
        return false;
    }

    public boolean canBreed() {
        if (!hasChicken1() || !hasChicken2()) {
            return false;
        }
        return !getChickenEntity1().isBaby() && !getChickenEntity2().isBaby();
    }

    public boolean seedIsFull(){
       return foodInventory.get(0).getCount()>=2;
    }

    private boolean outputIsFull() {
        int max = outputInventory.size();

        for (int i = 0; i < max; i++) {
            ItemStack stack = outputInventory.get(i);
            if (stack.getCount() < stack.getMaxStackSize()) return false;
        }
        return true;
    }

    private void removeBreedingItems() {
        for (ItemStack stack : foodInventory) {
                stack.shrink(2);
            }
        }


    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if (hasChicken1()) {
            CompoundNBT comp = new CompoundNBT();
            if (chickenEntity1 != null) {
                setChicken(chicken1, chickenEntity1);
            }
            chicken1.save(comp);
            compound.put("Chicken1", comp);
        }
        if (hasChicken2()) {
            CompoundNBT comp = new CompoundNBT();
            if (chickenEntity2 != null) {
                setChicken(chicken2, chickenEntity2);
            }
            chicken2.save(comp);
            compound.put("Chicken2", comp);
        }
        compound.putInt("TimeElapsed",this.timeElapsed);
        compound.putInt("TimeUntilNextChild", timeUntilNextSpawn);
        compound.put("FoodInventory", ItemStackHelper.saveAllItems(new CompoundNBT(), foodInventory, true));
        compound.put("OutputInventory", ItemStackHelper.saveAllItems(new CompoundNBT(), outputInventory, true));
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        if (compound.contains("Chicken1")) {
            CompoundNBT comp = compound.getCompound("Chicken1");
            chicken1 = ItemStack.of(comp);
            chickenEntity1 = null;
        } else {
            removeChicken1();
        }
        if (compound.contains("Chicken2")) {
            CompoundNBT comp = compound.getCompound("Chicken2");
            chicken2 = ItemStack.of(comp);
            chickenEntity2 = null;
        } else {
            removeChicken2();
        }
        this.timeUntilNextSpawn = compound.getInt("TimeUntilNextChild");
        this.timeElapsed = compound.getInt("TimeElapsed");
        ItemStackHelper.loadAllItems(compound.getCompound("FoodInventory"), foodInventory);
        ItemStackHelper.loadAllItems(compound.getCompound("OutputInventory"), outputInventory);
        super.load(state, compound);
    }

    public IInventory getFoodInventory() {
        return new ItemListInventory(foodInventory, this::setChanged);
    }

    public IInventory getOutputInventory() {
        return new ItemListInventory(outputInventory, this::setChanged);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side != null && side.equals(Direction.DOWN)) {
                return LazyOptional.of(this::getOutputInventoryItemHandler).cast();
            } else {
                return LazyOptional.of(this::getFoodInventoryItemHandler).cast();
            }

        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable foodInventoryHandler;

    public IItemHandlerModifiable getFoodInventoryItemHandler() {
        if (foodInventoryHandler == null) {
            foodInventoryHandler = new ItemStackHandler(foodInventory);
        }
        return foodInventoryHandler;
    }

    private IItemHandlerModifiable outputInventoryHandler;

    public IItemHandlerModifiable getOutputInventoryItemHandler() {
        if (outputInventoryHandler == null) {
            outputInventoryHandler = new ItemStackHandler(outputInventory);
        }
        return outputInventoryHandler;
    }




}

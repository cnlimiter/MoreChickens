package cn.evolvefield.mods.morechickens.common.tile;

import cn.evolvefield.mods.morechickens.common.container.RoostContainer;
import cn.evolvefield.mods.morechickens.common.container.base.ItemListInventory;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.common.data.ChickenUtils;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.tile.base.FakeWorldTileEntity;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import cn.evolvefield.mods.morechickens.init.ModContainers;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import cn.evolvefield.mods.morechickens.init.ModTileEntities;
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
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoostTileEntity extends FakeWorldTileEntity implements ITickableTileEntity, INamedContainerProvider {


    private ItemStack chickenItem;
    public NonNullList<ItemStack> outputInventory;
    private AnimalEntity chickenEntity;
    Random rand = new Random();
    private int progress;
    private int timeElapsed = 0;
    private int timeUntilNextLay = 0;
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
        return new RoostContainer(ModContainers.ROOST_CONTAINER,id, playerInventory, getOutputInventory(),dataAccess,this);
    }

    public RoostTileEntity() {
        super(ModTileEntities.ROOST, ModBlocks.BLOCK_ROOST.defaultBlockState());
        outputInventory = NonNullList.withSize(4, ItemStack.EMPTY);
        chickenItem = ItemStack.EMPTY;
    }

    public AnimalEntity getChicken(World world, ItemStack stack) {
        CompoundNBT compound = stack.getOrCreateTag();
        String type = compound.getString("Type");
        AnimalEntity chicken;
        if(type.equals("vanilla")){
            chicken = new ChickenEntity(EntityType.CHICKEN,world);
            return chicken;

        }
        chicken = new BaseChickenEntity(ModEntities.BASE_CHICKEN.get(), world);
        chicken.readAdditionalSaveData(compound);
        return chicken;
    }

    public void setChicken(ItemStack stack, AnimalEntity chickenEntity) {
        CompoundNBT compound = stack.getOrCreateTag();
        if (chickenEntity instanceof BaseChickenEntity){
            chickenEntity.addAdditionalSaveData(compound);
            compound.putString("Type","modded");
            stack.setTag(compound);
        }
        else if (chickenEntity instanceof ChickenEntity){
            compound.putString("Type","vanilla");
            stack.setTag(compound);
        }

    }

    public void setChickenItem(ItemStack chicken1) {
        this.chickenItem = chicken1;
        if (chicken1.isEmpty()) {
            chickenEntity = null;
        } else {
            chickenEntity = getChicken(level, chicken1);
        }
        setChanged();
        sync();
    }

    public ItemStack getChickenItem() {
        return chickenItem;
    }

    public String getChickenItemName(){
       return getChickenItem().getOrCreateTag().getString("Name");
    }

    public boolean hasChickenItem() {
        return !chickenItem.isEmpty();
    }

    public AnimalEntity getChickenEntity() {
        if (chickenEntity == null && !chickenItem.isEmpty()) {
            chickenEntity = getChicken(level, chickenItem);
        }
        return chickenEntity;
    }

    public ItemStack removeChickenItem() {
        ItemStack v = chickenItem;
        setChickenItem(ItemStack.EMPTY);
        return v;
    }




    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if (hasChickenItem()) {
            CompoundNBT comp = new CompoundNBT();
            if (chickenEntity != null) {
                setChicken(chickenItem, chickenEntity);
            }
            chickenItem.save(comp);
            compound.put("ChickenItem", comp);
        }
        compound.putInt("TimeElapsed",this.timeElapsed);
        compound.putInt("TimeUntilNextLay", timeUntilNextLay);
        compound.put("OutputInventory", ItemStackHelper.saveAllItems(new CompoundNBT(), outputInventory, true));
        return super.save(compound);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        if (compound.contains("ChickenItem")) {
            CompoundNBT comp = compound.getCompound("ChickenItem");
            chickenItem = ItemStack.of(comp);
            chickenEntity = null;
        } else {
            removeChickenItem();
        }
        this.timeUntilNextLay = compound.getInt("TimeUntilNextLay");
        this.timeElapsed = compound.getInt("TimeElapsed");
        ItemStackHelper.loadAllItems(compound.getCompound("OutputInventory"), outputInventory);
        super.load(state, compound);
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
        if (  canLay() && !outputIsFull()) {
            timeElapsed += 5;
            setChanged();
        }
    }
    public boolean canLay() {
        if (!hasChickenItem() ) {
            return false;
        }
        return !getChickenEntity().isBaby() ;
    }


    private void updateProgress() {
        if (hasChickenItem()){
            this.progress = timeUntilNextLay == 0 ? 0 : (timeElapsed * 1000 / timeUntilNextLay);
        }
        else {
            this.progress = 0;
        }
    }

    public double getProgress() {
        return progress / 1000.0;
    }


    private void spawnChickenDropIfNeeded() {
        if ((timeElapsed >= timeUntilNextLay)) {
            if (timeUntilNextLay > 0) {
                if(addLoot()){
                    getLevel().playSound(null, getBlockPos(), SoundEvents.CHICKEN_EGG, SoundCategory.NEUTRAL, 0.5F, 0.8F);
                    //spawnParticles();
                }
            }
            resetTimer();
        }
    }

    private void resetTimer() {
        String type = getChickenItem().getOrCreateTag().getString("Type");
        String name = getChickenItem().getOrCreateTag().getString("Name");
        ChickenData data = ChickenUtils.getChickenDataByName(name);
        int growth = getChickenItem().getOrCreateTag().getInt("ChickenGrowth");
        timeElapsed = 0;
        if (type.equals("vanilla")){
            timeUntilNextLay = rand.nextInt(6000) + 6000;
        }
        else
            if(type.equals("modded"))
            {
                BaseChickenEntity chicken = (BaseChickenEntity) getChickenEntity();
                timeUntilNextLay = ChickenUtils.calcNewEggLayTime(rand, data, growth);
                timeUntilNextLay = Math.max(600, timeUntilNextLay) + 6000;
            }
        setChanged();
    }

    private boolean addLoot() {
        for (int i = 0; i < outputInventory.size(); i++) {
            if (outputInventory.get(i).isEmpty()) {
                String type = getChickenItem().getOrCreateTag().getString("Type");
                String name = getChickenItem().getOrCreateTag().getString("Name");
                ChickenData data = ChickenUtils.getChickenDataByName(name);
                int gain = getChickenItem().getOrCreateTag().getInt("ChickenGain");
                if(type.equals("modded")) {
                    BaseChickenEntity chicken = (BaseChickenEntity) getChickenEntity();
                    ItemStack layItem = getRandItemStack(ChickenUtils.calcDrops(gain, data, 0, rand),rand);
                    outputInventory.set(i, layItem);

                }
                else
                    if(type.equals("vanilla"))
                    {
                    List<ItemStack> vanillaLay = new ArrayList<>();
                    vanillaLay.add(new ItemStack(Items.EGG));
                    vanillaLay.add(new ItemStack(Items.FEATHER));
                    outputInventory.set(i, getRandItemStack(vanillaLay,rand));
                    }
                return true;
            }
        }
        return false;
    }


    private ItemStack getRandItemStack(List<ItemStack> list, Random random){
       return list.get(random.nextInt(list.size()));
    }


    private boolean outputIsFull() {
        int max = outputInventory.size();

        for (int i = 0; i < max; i++) {
            ItemStack stack = outputInventory.get(i);
            if (stack.getCount() < stack.getMaxStackSize()) return false;
        }
        return true;
    }



    public IInventory getOutputInventory() {
        return new ItemListInventory(outputInventory, this::setChanged);
    }


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side != null && side.equals(Direction.DOWN)) {
                return LazyOptional.of(this::getOutputInventoryItemHandler).cast();
            }
        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable outputInventoryHandler;

    public IItemHandlerModifiable getOutputInventoryItemHandler() {
        if (outputInventoryHandler == null) {
            outputInventoryHandler = new ItemStackHandler(outputInventory);
        }
        return outputInventoryHandler;
    }







}

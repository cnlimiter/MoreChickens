package cn.evolvefield.mods.morechickens.common.tile;


import cn.evolvefield.mods.morechickens.common.container.RoostContainer;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.tile.base.FakeWorldTileEntity;
import cn.evolvefield.mods.morechickens.common.container.base.ItemListInventory;
import cn.evolvefield.mods.morechickens.common.util.blockentity.IServerTickableBlockEntity;
import cn.evolvefield.mods.morechickens.init.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoostTileEntity extends FakeWorldTileEntity implements IServerTickableBlockEntity,MenuProvider {

    private ItemStack chickenItem;
    public NonNullList<ItemStack> outputInventory;
    private Animal chickenEntity;
    Random rand = new Random();
    private int progress;
    private int timeElapsed = 0;
    private int timeUntilNextLay = 0;
    public final ContainerData dataAccess = new ContainerData() {
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
    public Component getDisplayName() {
        return new TranslatableComponent(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new RoostContainer(ModContainers.CONTAINER_ROOST,id, playerInventory, getOutputInventory(),dataAccess,this);
    }

    public RoostTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.TILE_ROOST, ModBlocks.BLOCK_ROOST.defaultBlockState(),pos,state);
        outputInventory = NonNullList.withSize(4, ItemStack.EMPTY);
        chickenItem = ItemStack.EMPTY;
    }

    public Animal getChicken(Level world, ItemStack stack) {
        CompoundTag compound = stack.getOrCreateTag();
        String type = compound.getString("Type");
        Animal chicken;
        if(type.equals("vanilla")){
            chicken = new Chicken(EntityType.CHICKEN,world);
            return chicken;

        }
        chicken = new BaseChickenEntity(ModEntities.BASE_CHICKEN.get(), world);
        chicken.readAdditionalSaveData(compound);
        return chicken;
    }

    public void setChicken(ItemStack stack, Animal chickenEntity) {
        CompoundTag compound = stack.getOrCreateTag();
        if (chickenEntity instanceof BaseChickenEntity){
            chickenEntity.addAdditionalSaveData(compound);
            compound.putString("Type","modded");
            stack.setTag(compound);
        }
        else if (chickenEntity instanceof Chicken){
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

    public boolean hasChickenItem() {
        return !chickenItem.isEmpty();
    }

    public Animal getChickenEntity() {
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
    public CompoundTag save(CompoundTag compound) {
        if (hasChickenItem()) {
            CompoundTag comp = new CompoundTag();
            if (chickenEntity != null) {
                setChicken(chickenItem, chickenEntity);
            }
            chickenItem.save(comp);
            compound.put("ChickenItem", comp);
        }
        compound.putInt("TimeElapsed",this.timeElapsed);
        compound.putInt("TimeUntilNextLay", timeUntilNextLay);
        compound.put("OutputInventory", ContainerHelper.saveAllItems(new CompoundTag(), outputInventory, true));
        return super.save(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        if (compound.contains("ChickenItem")) {
            CompoundTag comp = compound.getCompound("ChickenItem");
            chickenItem = ItemStack.of(comp);
            chickenEntity = null;
        } else {
            removeChickenItem();
        }
        this.timeUntilNextLay = compound.getInt("TimeUntilNextLay");
        this.timeElapsed = compound.getInt("TimeElapsed");
        ContainerHelper.loadAllItems(compound.getCompound("OutputInventory"), outputInventory);
        super.load(compound);
    }

    @Override
    public void tickServer() {
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
                    getLevel().playSound(null, getBlockPos(), SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 0.5F, 0.8F);
                    //spawnParticles();
                }
            }
            resetTimer();
        }
    }

    private void resetTimer() {
        String type = getChickenItem().getOrCreateTag().getString("Type");
        timeElapsed = 0;
        if (type.equals("modded")){
            BaseChickenEntity chicken = (BaseChickenEntity) getChickenEntity();
            timeUntilNextLay = chicken.type.layTime + rand.nextInt(chicken.type.layTime + 1);
            timeUntilNextLay *= chicken.getGene().layTime + rand.nextFloat() * chicken.getGene().layRandomTime;
            timeUntilNextLay = Math.max(600, timeUntilNextLay) + 6000;
            setChanged();
        }
        else if(type.equals("vanilla")){
            timeUntilNextLay = rand.nextInt(6000) + 6000;
            setChanged();
        }
    }

    private boolean addLoot() {
        for (int i = 0; i < outputInventory.size(); i++) {
            if (outputInventory.get(i).isEmpty()) {
                String type = getChickenItem().getOrCreateTag().getString("Type");
                if(type.equals("modded")) {
                    BaseChickenEntity chicken = (BaseChickenEntity) getChickenEntity();
                    ItemStack layItem = chicken.type.getLoot(rand,chicken.getGene());
                    outputInventory.set(i, layItem);
                    return true;

                }
                else if(type.equals("vanilla")){
                    List<ItemStack> vanillaLay = new ArrayList<>();
                    vanillaLay.add(new ItemStack(Items.EGG));
                    vanillaLay.add(new ItemStack(Items.FEATHER));
                    outputInventory.set(i, new ItemStack(Items.EGG));
                    return true;
                }
            }
        }
        return false;
    }




    private boolean outputIsFull() {
        int max = outputInventory.size();

        for (int i = 0; i < max; i++) {
            ItemStack stack = outputInventory.get(i);
            if (stack.getCount() < stack.getMaxStackSize()) return false;
        }
        return true;
    }



    public Container getOutputInventory() {
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

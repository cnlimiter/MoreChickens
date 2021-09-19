package cn.evolvefield.mods.morechickens.common.tile;


import cn.evolvefield.mods.morechickens.MoreChickens;

import cn.evolvefield.mods.morechickens.common.container.CollectorContainer;
import cn.evolvefield.mods.morechickens.init.ModTileEntities;
import cn.evolvefield.mods.morechickens.init.util.InventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CollectorTileEntity extends BlockEntity implements WorldlyContainer, BlockEntityTicker, MenuProvider {

    private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);
    private int searchOffset = 0;

    public CollectorTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.TILE_COLLECTOR,pos,state);
    }


    public String getName() {
        return "container." + MoreChickens.MODID + ".collector";
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent(getName());
    }

    
    
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new CollectorContainer(id,playerInventory,this);
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public ItemStack getItem(int index) {
        return inventory.get(index);
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : inventory) {
            if (!itemStack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return ContainerHelper.removeItem(inventory, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(inventory, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        inventory.set(index, stack);

        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
    }


    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean stillValid(Player player) {
        if (getLevel().getBlockEntity(getBlockPos()) != this) {
            return false;
        } else {
            return player.distanceToSqr(getBlockPos().getX() + 0.5D, getBlockPos().getY() + 0.5D, getBlockPos().getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void startOpen(Player p_174889_1_) {
    }

    @Override
    public void stopOpen(Player p_174886_1_) {
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override//isItemValidForSlot
    public boolean canPlaceItem(int index, ItemStack stack) {
        return true;
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }





    @Override
    public int[] getSlotsForFace(Direction direction) {
        int[] itemSlots = new int[27];
        for (int i = 0; i < 27; i++) {
            itemSlots[i] = i;
        }
        return itemSlots;
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state, BlockEntity entity) {
        if (!getLevel().isClientSide) {
            updateSearchOffset();
            gatherItems();
        }
    }


    @Override
    public void load( CompoundTag nbt) {
        super.load( nbt);
        //ItemStackHelper.loadAllItems(nbt, inventory);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        super.save(nbt);
        ContainerHelper.saveAllItems(nbt, inventory);
        return nbt;
    }

    private IItemHandler itemHandler;

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (itemHandler == null) itemHandler = new InvWrapper(this);
            return (LazyOptional<T>) itemHandler;
        }
        return super.getCapability(cap, side);
    }

    private void updateSearchOffset() {
        searchOffset = (searchOffset + 1) % 27;
    }

    private void gatherItems() {
        for (int x = -4; x < 5; x++) {
            int y = searchOffset / 9;
            int z = (searchOffset % 9) - 4;
            gatherItemAtPos(getBlockPos().offset(x, y, z));
        }
    }

    private void gatherItemAtPos(BlockPos pos) {
        BlockEntity tileEntity = getLevel().getBlockEntity(pos);
        if (!(tileEntity instanceof RoostTileEntity)) return;

        RoostTileEntity tileEntityRoost = (RoostTileEntity) getLevel().getBlockEntity(pos);

        int[] slots =new int[4];

        for (int i : slots) {
            if (pullItemFromSlot(tileEntityRoost, i)) return;
        }
    }

    private boolean pullItemFromSlot(RoostTileEntity tileRoost, int index) {
        ItemStack itemStack = tileRoost.outputInventory.get(index);

        if (!itemStack.isEmpty() ) {
            ItemStack itemStack1 = itemStack.copy();
            ItemStack itemStack2 = InventoryUtil.putStackInInventoryAllSlots(tileRoost, this,
                    tileRoost.getOutputInventory().removeItem(index, 1),null);

            if (itemStack2.isEmpty()) {
                tileRoost.setChanged();
                setChanged();
                return true;
            }

            tileRoost.getOutputInventory().setItem(index, itemStack1);
        }

        return false;
    }


}

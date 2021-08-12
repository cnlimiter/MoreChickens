package cn.evolvefield.mods.morechickens.core.tile;


import cn.evolvefield.mods.morechickens.MoreChickens;

import cn.evolvefield.mods.morechickens.core.container.ContainerCollector;
import cn.evolvefield.mods.morechickens.init.ModTileEntities;
import cn.evolvefield.mods.morechickens.init.util.InventoryUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityCollector extends TileEntity implements ISidedInventory, ITickableTileEntity, INamedContainerProvider, IIntArray {

    private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(getContainerSize(), ItemStack.EMPTY);
    private int searchOffset = 0;

    public TileEntityCollector() {
        super(ModTileEntities.TILE_COLLECTOR);
    }


    public String getName() {
        return "container." + MoreChickens.MODID + ".collector";
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(getName());
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new ContainerCollector(id,playerInventory,this);
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
        return ItemStackHelper.removeItem(inventory, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ItemStackHelper.takeItem(inventory, index);
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
    public boolean stillValid(PlayerEntity player) {
        if (getLevel().getBlockEntity(getBlockPos()) != this) {
            return false;
        } else {
            return player.distanceToSqr(getBlockPos().getX() + 0.5D, getBlockPos().getY() + 0.5D, getBlockPos().getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void startOpen(PlayerEntity p_174889_1_) {
    }

    @Override
    public void stopOpen(PlayerEntity p_174886_1_) {
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
    public int get(int p_221476_1_) {
        return 0;
    }

    @Override
    public void set(int p_221477_1_, int p_221477_2_) {
    }

    @Override
    public int getCount() {
        return 0;
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
    public void tick() {
        if (!getLevel().isClientSide) {
            updateSearchOffset();
            gatherItems();
        }
    }


    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        //ItemStackHelper.loadAllItems(nbt, inventory);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        ItemStackHelper.saveAllItems(nbt, inventory);
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
        TileEntity tileEntity = getLevel().getBlockEntity(pos);
        if (!(tileEntity instanceof TileEntityRoost)) return;

        TileEntityRoost tileEntityRoost = (TileEntityRoost) getLevel().getBlockEntity(pos);

        int[] slots = tileEntityRoost.getSlotsForFace(null);

        for (int i : slots) {
            if (pullItemFromSlot(tileEntityRoost, i)) return;
        }
    }

    private boolean pullItemFromSlot(TileEntityRoost tileRoost, int index) {
        ItemStack itemStack = tileRoost.getItem(index);

        if (!itemStack.isEmpty() && tileRoost.canTakeItemThroughFace(index, itemStack, null)) {
            ItemStack itemStack1 = itemStack.copy();
            ItemStack itemStack2 = InventoryUtil.putStackInInventoryAllSlots(tileRoost, this,
                    tileRoost.removeItem(index, 1),null);

            if (itemStack2.isEmpty()) {
                tileRoost.setChanged();
                setChanged();
                return true;
            }

            tileRoost.setItem(index, itemStack1);
        }

        return false;
    }



}

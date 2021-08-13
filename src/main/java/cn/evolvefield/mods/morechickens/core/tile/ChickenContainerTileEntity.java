package cn.evolvefield.mods.morechickens.core.tile;


import cn.evolvefield.mods.morechickens.core.data.DataChicken;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;

public abstract class ChickenContainerTileEntity extends TileEntity implements ISidedInventory, ITickableTileEntity, IInventory, IIntArray {
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0%");

    private NonNullList<ItemStack> inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    private boolean mightNeedToUpdateChickenInfo = true;
    private boolean skipNextTimerReset = false;
    private int timeUntilNextDrop = 0;
    private int timeElapsed = 0;
    private int progress = 0; // 0 - 1000

    private final DataChicken[] chickenData = new DataChicken[getSizeChickenInventory()];
    private boolean fullOfChickens = false;
    private boolean fullOfSeeds = false;



    public ChickenContainerTileEntity(TileEntityType<?> type) {
        super(type);
    }

    public void willNeedToUpdateChickenInfo() {
        mightNeedToUpdateChickenInfo = true;
    }

    private void notifyBlockUpdate() {
        final BlockState state = getLevel().getBlockState(getBlockPos());
        getLevel().markAndNotifyBlock(getBlockPos(), (Chunk) getLevel().getChunk(getBlockPos()),state, state, 2,2);
    }

    private void updateChickenInfoIfNeeded() {
        if (!mightNeedToUpdateChickenInfo) return;

        if (fullOfChickens != isFullOfChickens()) {
            fullOfChickens = !fullOfChickens;
            notifyBlockUpdate();
        }

        if (fullOfSeeds != isFullOfSeeds()) {
            fullOfSeeds = !fullOfSeeds;
            notifyBlockUpdate();
        }

        mightNeedToUpdateChickenInfo = false;
    }

    private void updateChickenInfoIfNeededForSlot(int slot) {
        DataChicken oldChicken = chickenData[slot];
        DataChicken newChicken = createChickenData(slot);

        boolean wasCreated = oldChicken == null && newChicken != null;
        boolean wasDeleted = oldChicken != null && newChicken == null;
        boolean wasChanged = oldChicken != null && newChicken != null && !oldChicken.isEqual(newChicken);

        if (wasCreated || wasChanged || wasDeleted) {
            chickenData[slot] = newChicken;
            if (!skipNextTimerReset) resetTimer();
        }
        if (wasChanged) notifyBlockUpdate();
    }

    protected DataChicken getChickenData(int slot) {
        if (slot >= chickenData.length || slot < 0) return null;
        return chickenData[slot];
    }

    protected DataChicken createChickenData(int slot) {
        return DataChicken.getDataFromStack(getItem(slot));
    }

    private void updateTimerIfNeeded() {
        if (fullOfChickens && fullOfSeeds && !outputIsFull()) {
            timeElapsed += getTimeElapsed();
            setChanged();
        }
    }

    private void updateProgress() {
        progress = timeUntilNextDrop == 0 ? 0 : (timeElapsed * 1000 / timeUntilNextDrop);
    }

    private int getTimeElapsed() {
        int time = Integer.MAX_VALUE;
        for (int i = 0; i < chickenData.length; i++) {
            if (chickenData[i] == null) return 0;
            time = Math.min(time, chickenData[i].getAddedTime(getItem(i)));
        }
        return time;
    }

    public double getProgress() {
        return progress / 1000.0;
    }

    public String getFormattedProgress() {
        return formatProgress(getProgress());
    }

    public String formatProgress(double progress) {
        return FORMATTER.format(progress);
    }

    private void spawnChickenDropIfNeeded() {
        if (fullOfChickens && fullOfSeeds && (timeElapsed >= timeUntilNextDrop)) {
            if (timeUntilNextDrop > 0) {
                removeItem(getSizeChickenInventory(), requiredSeedsForDrop());
                spawnChickenDrop();
            }
            resetTimer();
        }
    }

    private void resetTimer() {
        timeElapsed = 0;
        timeUntilNextDrop = 0;
        for (int i = 0; i < chickenData.length; i++) {
            if (chickenData[i] != null) {
                timeUntilNextDrop = Math.max(timeUntilNextDrop, chickenData[i].getLayTime());
                timeUntilNextDrop /= speedMultiplier();
            }
        }
        setChanged();
    }
    
    protected abstract void spawnChickenDrop();

    protected abstract int getSizeChickenInventory();

    protected abstract int requiredSeedsForDrop();

    protected abstract double speedMultiplier();

    public boolean isFullOfChickens() {
        for (int i = 0; i < chickenData.length; i++) {
            updateChickenInfoIfNeededForSlot(i);
            if (chickenData[i] == null) return false;
        }
        return true;
    }

    public boolean isFullOfSeeds() {
        int needed = requiredSeedsForDrop();
        if (needed == 0) return true;
        ItemStack stack = getItem(getSizeChickenInventory());
        return stack.getCount() >= needed;
    }

    private boolean outputIsFull() {
        int max = getContainerSize();

        for (int i = getOutputStackIndex(); i < max; i++) {
            ItemStack stack = getItem(i);
            if (stack.getCount() < stack.getMaxStackSize()) return false;
        }
        return true;
    }

    private int getOutputStackIndex() {
        if (requiredSeedsForDrop() > 0) {
            return getSizeChickenInventory() + 1;
        }
        return getSizeChickenInventory();
    }

    protected ItemStack putStackInOutput(ItemStack stack) {
        int max = getContainerSize();

        for (int i = getOutputStackIndex(); i < max && !stack.isEmpty(); i++) {
            stack = insertStack(stack, i);
        }

        setChanged();

        return stack;
    }

    private ItemStack insertStack(ItemStack stack, int index) {
        int max = Math.min(stack.getMaxStackSize(), getMaxStackSize());

        ItemStack outputStack = getItem(index);
        if (outputStack.isEmpty()) {
            if (stack.getCount() >= max) {
                inventory.set(index, stack);
                //setInventorySlotContents(index, stack);
                stack = ItemStack.EMPTY;
            } else {
                inventory.set(index, stack.split(max));
                //setInventorySlotContents(index, stack.splitStack(max));
            }
        } else if (canCombine(outputStack, stack)) {
            if (outputStack.getCount() < max) {
                int itemsToMove = Math.min(stack.getCount(), max - outputStack.getCount());
                stack.shrink(itemsToMove);
                outputStack.grow(itemsToMove);
            }
        }

        return stack;
    }

    private boolean canCombine(ItemStack a, ItemStack b) {
        if (a.getItem() != b.getItem()) return false;
        //if (a.getMetadata() != b.getMetadata()) return false;
        if (a.getCount() > a.getMaxStackSize()) return false;
        return ItemStack.tagMatches(a, b);
    }



    @Override
    public int getContainerSize() {
        return 5;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : inventory) {
            if (!itemStack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int i) {
        return inventory.get(i);
    }



    @Override
    public ItemStack removeItem(int i, int count) {
        if (i < getOutputStackIndex()) willNeedToUpdateChickenInfo();
        return ItemStackHelper.removeItem(inventory, i, count);
    }



    @Override
    public void setItem(int index, ItemStack stack) {
        inventory.set(index, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        if (index < getOutputStackIndex()) willNeedToUpdateChickenInfo();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override//isUsableByPlayer
    public boolean stillValid(PlayerEntity playerEntity) {
        if (getLevel().getBlockEntity(getBlockPos()) != this) {
            return false;
        } else {
            return playerEntity.distanceToSqr(getBlockPos().getX() + 0.5D, getBlockPos().getY() + 0.5D, getBlockPos().getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void startOpen(PlayerEntity playerEntity) {
    }

    @Override
    public void stopOpen(PlayerEntity playerEntity) {
    }

    @Override//canInsertItem
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return canPlaceItem(index, stack);
    }

    @Override//canExtractItem
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index >= getOutputStackIndex();
    }

    @Override//isItemValidForSlot
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index < getSizeChickenInventory()) return DataChicken.isChicken(stack);
        if (index < getOutputStackIndex()) return isSeed(stack);
        return false;
    }

    public static boolean isSeed(ItemStack stack) {
        Item item = stack.getItem();
        return (item == Items.WHEAT_SEEDS || item == Items.MELON_SEEDS || item == Items.PUMPKIN_SEEDS || item == Items.BEETROOT_SEEDS);
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Override
    public int countItem(Item p_213901_1_) {
        return ISidedInventory.super.countItem(p_213901_1_);
    }


    @Override
    public int get(int id) {
        switch (id) {
            case 0:
                return progress;
            default:
                return 0;
        }
    }

    @Override
    public void set(int id, int value) {
        switch (id) {
            case 0:
                progress = value;
                break;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }



    @Override
    public int[] getSlotsForFace(Direction direction) {
        int count = getContainerSize();
        int[] itemSlots = new int[count];
        for (int i = 0; i < count; i++) {
            itemSlots[i] = i;
        }
        return itemSlots;
    }



    @Override
    public ItemStack removeItemNoUpdate(int id) {
        return null;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(getBlockState(),pkt.getTag());
        notifyBlockUpdate();
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        clearContent();
        ItemStackHelper.loadAllItems(nbt, inventory);
        timeUntilNextDrop = nbt.getInt("TimeUntilNextChild");
        timeElapsed = nbt.getInt("TimeElapsed");
        skipNextTimerReset = true;
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        ItemStackHelper.saveAllItems(nbt, inventory);
        nbt.putInt("TimeUntilNextChild", timeUntilNextDrop);
        nbt.putInt("TimeElapsed", timeElapsed);
        return nbt;
    }

    private IItemHandler itemHandler;

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (itemHandler == null) itemHandler = new SidedInvWrapper(this, side.DOWN);
            return (LazyOptional<T>) itemHandler;
        }
        return super.getCapability(cap, side);
    }




    @Override
    public void tick() {
        if (!level.isClientSide) {
            updateChickenInfoIfNeeded();
            updateTimerIfNeeded();
            spawnChickenDropIfNeeded();
            updateProgress();
            skipNextTimerReset = false;
        }

    }
}

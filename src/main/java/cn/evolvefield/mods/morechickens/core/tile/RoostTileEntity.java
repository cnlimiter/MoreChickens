package cn.evolvefield.mods.morechickens.core.tile;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.container.ContainerRoost;
import cn.evolvefield.mods.morechickens.core.data.DataChicken;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import cn.evolvefield.mods.morechickens.init.ModTileEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class RoostTileEntity extends ChickenContainerTileEntity implements MenuProvider {

    private static final String CHICKEN_KEY = "Chicken";
    private static final String COMPLETE_KEY = "Complete";
    private static int CHICKEN_SLOT = 0;
    private SimpleContainer inventory = new SimpleContainer(5);

    public RoostTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.TILE_ROOST,pos,state);
    }


    public DataChicken createChickenData() {
        return createChickenData(0);
    }

    public boolean putChickenIn(ItemStack newStack) {
        ItemStack oldStack = getItem(CHICKEN_SLOT);

        if (!canPlaceItem(CHICKEN_SLOT, newStack)) {
            return false;
        }

        if (oldStack.isEmpty()) {
            setItem(CHICKEN_SLOT, newStack.split(16));
            setChanged();
            playPutChickenInSound();
            return true;
        }

        if (oldStack.equals(newStack) && ItemStack.tagMatches(oldStack, newStack)) {
            int itemsAfterAdding = Math.min(oldStack.getCount() + newStack.getCount(), 16);
            int itemsToAdd = itemsAfterAdding - oldStack.getCount();
            if (itemsToAdd > 0) {
                newStack.split(itemsToAdd);
                oldStack.grow(itemsToAdd);
                setChanged();
                playPutChickenInSound();
                return true;
            }
        }

        return false;
    }

    public boolean pullChickenOut(Player playerIn) {
        ItemStack spawnStack = getItem(CHICKEN_SLOT);

        if (spawnStack.isEmpty()) {
            return false;
        }

        playerIn.getInventory().add(spawnStack);
        setItem(CHICKEN_SLOT, ItemStack.EMPTY);

        setChanged();
        playPullChickenOutSound();

        return true;
    }

    private void playPutChickenInSound() {
        getLevel().playSound(null, getBlockPos(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private void playPullChickenOutSound() {
        getLevel().playSound(null, getBlockPos(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void addInfoToTooltip(List<String> tooltip, CompoundTag tag) {
        if (tag.contains(CHICKEN_KEY)) {
            DataChicken chicken = DataChicken.getDataFromTooltipNBT(tag.getCompound(CHICKEN_KEY));
            tooltip.add(chicken.getDisplaySummary());
        }

        if (tag.contains(COMPLETE_KEY)) {
            tooltip.add(new TranslatableComponent("container.chickens.roost.progress", formatProgress(tag.getDouble(COMPLETE_KEY))).getString());
        }
    }

    public void storeInfoForTooltip(CompoundTag tag) {
        DataChicken chicken = getChickenData(CHICKEN_SLOT);
        if (chicken == null) return;
        tag.put(CHICKEN_KEY, chicken.buildTooltipNBT());
        tag.putDouble(COMPLETE_KEY, getProgress());
    }

    public SimpleContainer getInventory() {
        return inventory;
    }


    public String getName() {
        return "container." + MoreChickens.MODID + ".roost";
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent(getName());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new ContainerRoost(id,playerInventory,this);
    }

    @Override
    protected void spawnChickenDrop() {
        DataChicken chicken = getChickenData(0);
        if (chicken != null) putStackInOutput(chicken.createDropStack());
    }

    @Override
    public int getContainerSize() {
        return 5;
    }

    @Override
    protected int getSizeChickenInventory() {
        return 1;
    }

    @Override
    protected int requiredSeedsForDrop() {
        return 0;
    }

    @Override
    protected double speedMultiplier() {
        return ModConfig.COMMON.roostSpeed.get();
    }


}

package cn.evolvefield.mods.morechickens.core.tile;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.container.RoostContainer;
import cn.evolvefield.mods.morechickens.core.data.DataChicken;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import cn.evolvefield.mods.morechickens.init.ModTileEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;

public class RoostTileEntity extends ChickenContainerTileEntity implements INamedContainerProvider {

    private static final String CHICKEN_KEY = "Chicken";
    private static final String COMPLETE_KEY = "Complete";
    private static int CHICKEN_SLOT = 0;
    private Inventory inventory = new Inventory(5);

    public RoostTileEntity() {
        super(ModTileEntities.TILE_ROOST);
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

    public boolean pullChickenOut(PlayerEntity playerIn) {
        ItemStack spawnStack = getItem(CHICKEN_SLOT);

        if (spawnStack.isEmpty()) {
            return false;
        }

        playerIn.inventory.add(spawnStack);
        setItem(CHICKEN_SLOT, ItemStack.EMPTY);

        setChanged();
        playPullChickenOutSound();

        return true;
    }

    private void playPutChickenInSound() {
        getLevel().playSound(null, getBlockPos(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    private void playPullChickenOutSound() {
        getLevel().playSound(null, getBlockPos(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    public void addInfoToTooltip(List<String> tooltip, CompoundNBT tag) {
        if (tag.contains(CHICKEN_KEY)) {
            DataChicken chicken = DataChicken.getDataFromTooltipNBT(tag.getCompound(CHICKEN_KEY));
            tooltip.add(chicken.getDisplaySummary());
        }

        if (tag.contains(COMPLETE_KEY)) {
            tooltip.add(new TranslationTextComponent("container.chickens.roost.progress", formatProgress(tag.getDouble(COMPLETE_KEY))).getString());
        }
    }

    public void storeInfoForTooltip(CompoundNBT tag) {
        DataChicken chicken = getChickenData(CHICKEN_SLOT);
        if (chicken == null) return;
        tag.put(CHICKEN_KEY, chicken.buildTooltipNBT());
        tag.putDouble(COMPLETE_KEY, getProgress());
    }

    public Inventory getInventory() {
        return inventory;
    }


    public String getName() {
        return "container." + MoreChickens.MODID + ".roost";
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(getName());
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new RoostContainer(id,playerInventory,this);
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

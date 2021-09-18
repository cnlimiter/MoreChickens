package cn.evolvefield.mods.morechickens.common.container.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.function.Function;

public class ItemListInventory implements IInventory {
    protected NonNullList<ItemStack> items;
    private Runnable onMarkDirty;
    private Function<PlayerEntity, Boolean> onIsUsableByPlayer;

    public ItemListInventory(NonNullList<ItemStack> items, Runnable onMarkDirty, Function<PlayerEntity, Boolean> onIsUsableByPlayer) {
        this.items = items;
        this.onMarkDirty = onMarkDirty;
        this.onIsUsableByPlayer = onIsUsableByPlayer;
    }

    public ItemListInventory(NonNullList<ItemStack> items, Runnable onMarkDirty) {
        this(items, onMarkDirty, (Function)null);
    }

    public ItemListInventory(NonNullList<ItemStack> items) {
        this(items, (Runnable)null);
    }

    public int getContainerSize() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = ItemStackHelper.removeItem(this.items, index, count);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    public ItemStack removeItemNoUpdate(int index) {
        return ItemStackHelper.takeItem(this.items, index);
    }

    public void setItem(int index, ItemStack stack) {
        this.items.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    public void setChanged() {
        if (this.onMarkDirty != null) {
            this.onMarkDirty.run();
        }

    }

    public boolean stillValid(PlayerEntity player) {
        return this.onIsUsableByPlayer != null ? (Boolean)this.onIsUsableByPlayer.apply(player) : true;
    }

    public void clearContent() {
        this.items.clear();
    }
}

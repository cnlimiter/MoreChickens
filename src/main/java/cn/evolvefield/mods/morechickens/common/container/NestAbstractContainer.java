package cn.evolvefield.mods.morechickens.common.container;

import cn.evolvefield.mods.morechickens.common.container.slot.ManualSlotItemHandler;
import cn.evolvefield.mods.morechickens.common.item.UpgradeItem;
import cn.evolvefield.mods.morechickens.common.tile.util.InventoryHandlerHelper;
import cn.evolvefield.mods.morechickens.common.tile.UpgradeableTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public abstract class NestAbstractContainer extends Container {
    protected NestAbstractContainer(@Nullable ContainerType<?> type, int id ) {
        super(type,id);
    }

    protected abstract TileEntity getTileEntity();

    @Nullable
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            final ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();

            final int containerSlots = this.slots.size() - player.inventory.items.size();

            // Move from container to player inventory.
            if (index < containerSlots) {
                if (!moveItemStackTo(slotStack, containerSlots, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                // Move from player inv into container
                int upgradeSlotCount = this.getTileEntity() instanceof UpgradeableTileEntity ? 4 : 0;
                if (upgradeSlotCount > 0 && slotStack.getItem() instanceof UpgradeItem) {
                    if (!moveItemStackTo(slotStack, containerSlots - upgradeSlotCount, containerSlots, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else {
                    if (!moveItemStackTo(slotStack, 0, containerSlots - upgradeSlotCount, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }

            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }
        return returnStack;
    }
    protected int addSlotRange(IInventory handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            if (handler instanceof InventoryHandlerHelper.ItemHandler) {
                addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) handler, index, x, y));
            }
            else {
                addSlot(new Slot(handler, index, x, y));
            }
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            if (handler instanceof InventoryHandlerHelper.ItemHandler) {
                addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) handler, index, x, y));
            }
            x += dx;
            index++;
        }
        return index;
    }

    protected void addSlotBox(IInventory handler, int startIndex, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            startIndex = addSlotRange(handler, startIndex, x, y, horAmount, dx);
            y += dy;
        }
    }

    protected void addSlotBox(IItemHandler handler, int startIndex, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            startIndex = addSlotRange(handler, startIndex, x, y, horAmount, dx);
            y += dy;
        }
    }

    protected void layoutPlayerInventorySlots(PlayerInventory inventory, int startIndex, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(inventory, startIndex + 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(inventory, startIndex, leftCol, topRow, 9, 18);
    }
}
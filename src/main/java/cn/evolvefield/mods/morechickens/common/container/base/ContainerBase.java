package cn.evolvefield.mods.morechickens.common.container.base;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;


public class ContainerBase extends AbstractContainerMenu{
    @Nullable
    protected Container inventory;
    protected Container playerInventory;

    public ContainerBase(MenuType containerType, int id, Container playerInventory, Container inventory) {
        super(containerType, id);
        this.playerInventory = playerInventory;
        this.inventory = inventory;
    }

    protected void addPlayerInventorySlots() {
        if (playerInventory != null) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + getInvOffset()));
                }
            }

            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(playerInventory, k, 8 + k * 18, 142 + getInvOffset()));
            }
        }
    }

    public int getInvOffset() {
        return 0;
    }

    public int getInventorySize() {
        if (inventory == null) {
            return 0;
        }
        return inventory.getContainerSize();
    }

    @Nullable
    public Container getPlayerInventory() {
        return playerInventory;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            if (index < getInventorySize()) {
                if (!moveItemStackTo(stack, getInventorySize(), slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, getInventorySize(), false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (inventory == null) {
            return true;
        }
        return inventory.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (inventory != null) {
            inventory.stopOpen(player);
        }
    }
}

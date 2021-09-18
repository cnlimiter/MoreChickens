package cn.evolvefield.mods.morechickens.common.container.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class ContainerBase extends Container {
    @Nullable
    protected IInventory inventory;
    protected IInventory playerInventory;

    public ContainerBase(ContainerType containerType, int id, IInventory playerInventory, IInventory inventory) {
        super(containerType, id);
        this.playerInventory = playerInventory;
        this.inventory = inventory;
    }

    protected void addPlayerInventorySlots() {
        if (this.playerInventory != null) {
            int k;
            for(k = 0; k < 3; ++k) {
                for(int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(this.playerInventory, j + k * 9 + 9, 8 + j * 18, 84 + k * 18 + this.getInvOffset()));
                }
            }

            for(k = 0; k < 9; ++k) {
                this.addSlot(new Slot(this.playerInventory, k, 8 + k * 18, 142 + this.getInvOffset()));
            }
        }

    }

    public int getInvOffset() {
        return 0;
    }


    public static TileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final TileEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos != null) {
            return  tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    public int getInventorySize() {
        return this.inventory == null ? 0 : this.inventory.getContainerSize();
    }

    @Nullable
    public IInventory getPlayerInventory() {
        return this.playerInventory;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index < this.getInventorySize()) {
                if (!this.moveItemStackTo(stack, this.getInventorySize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, this.getInventorySize(), false)) {
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
    public boolean stillValid(PlayerEntity player) {
        return this.inventory == null ? true : this.inventory.stillValid(player);
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        if (this.inventory != null) {
            this.inventory.stopOpen(player);
        }

    }
}

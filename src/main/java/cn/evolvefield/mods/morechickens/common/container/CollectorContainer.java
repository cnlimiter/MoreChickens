package cn.evolvefield.mods.morechickens.common.container;


import cn.evolvefield.mods.morechickens.common.tile.CollectorTileEntity;
import cn.evolvefield.mods.morechickens.init.ModContainers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.Objects;

public class CollectorContainer extends AbstractContainerMenu {

    public final CollectorTileEntity tileCollector;

    public CollectorContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public CollectorContainer(final int windowId, final Inventory playerInventory, final CollectorTileEntity tileEntity) {
        this(ModContainers.CONTAINER_COLLECTOR, windowId, playerInventory, tileEntity);
    }


    public CollectorContainer(@Nullable MenuType<?> type, final int windowId, final Inventory playerInventory, final CollectorTileEntity tileEntity)
    {
        super(type, windowId);
        this.tileCollector = tileEntity;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(tileEntity, x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 85 + y * 18));
            }
        }

        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 143));
        }
    }

    private static CollectorTileEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof CollectorTileEntity) {
            return (CollectorTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }


    public BlockEntity getTileEntity() {
        return tileCollector;
    }


    @Override//canInteractWith
    public boolean stillValid(Player player) {
        return tileCollector.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int fromSlot) {
        ItemStack previous = ItemStack.EMPTY;
        Slot slot = slots.get(fromSlot);

        if (slot != null && slot.hasItem()) {
            ItemStack current = slot.getItem();
            previous = current.copy();

            if (fromSlot < tileCollector.getContainerSize()) {
                if (!moveItemStackTo(current, tileCollector.getContainerSize(), slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(current, 0, tileCollector.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (current.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return previous;
    }
}

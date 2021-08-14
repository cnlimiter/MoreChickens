package cn.evolvefield.mods.morechickens.core.container;


import cn.evolvefield.mods.morechickens.core.block.BlockRoost;
import cn.evolvefield.mods.morechickens.core.container.slot.SlotChicken;
import cn.evolvefield.mods.morechickens.core.container.slot.SlotReadOnly;
import cn.evolvefield.mods.morechickens.core.tile.RoostTileEntity;
import cn.evolvefield.mods.morechickens.init.ModContainers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ContainerRoost extends AbstractContainerMenu {
    public final RoostTileEntity tileRoost;
    private int progress;
    public final ContainerLevelAccess canInteractWithCallable;


    public ContainerRoost(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public ContainerRoost(final int windowId, final Inventory playerInventory, final RoostTileEntity tileEntity) {
        this(ModContainers.CONTAINER_ROOST, windowId, playerInventory, tileEntity);
    }


    public ContainerRoost(@Nullable MenuType<?> type, final int windowId, final Inventory playerInventory, final RoostTileEntity tileEntity)
    {
        super(type, windowId);
        this.tileRoost = tileEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());

        addSlot(new SlotChicken(tileEntity, 0, 26, 20));

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotReadOnly(tileEntity, i + 1, 80 + i * 18, 20));
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 51 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            addSlot(new Slot(playerInventory, k, 8 + k * 18, 109));
        }
    }


    private static RoostTileEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof RoostTileEntity) {
            return (RoostTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }


    public BlockEntity getTileEntity() {
        return tileRoost;
    }

    @Override//canInteractWith
    public boolean stillValid(Player playerEntity) {
        return canInteractWithCallable.evaluate(
                (world, pos) -> world.getBlockState(pos).getBlock() instanceof BlockRoost
                        && playerEntity.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }




    @Override
    public void addSlotListener(ContainerListener listener) {
        super.addSlotListener(listener);

        //listener.refreshContainer(this, tileRoost);
    }


    @Override
    public void broadcastChanges() {
        List<ContainerListener> containerListeners = ObfuscationReflectionHelper.getPrivateValue(AbstractContainerMenu.class,null,"field_75149_d");
        for (int i = 0; i < containerListeners.size(); ++i) {
            ContainerListener listener = containerListeners.get(i);
            if (progress != tileRoost.get(0)) {
                listener.dataChanged(this, 0, tileRoost.get(0));
            }
        }

        progress = tileRoost.get(0);
    }


    @Override
    public void setData(int id, int data) {
        tileRoost.set(id, data);
    }

    @Override
    public ItemStack quickMoveStack(Player playerEntity, int fromSlot) {
        ItemStack previous = ItemStack.EMPTY;
        Slot slot = slots.get(fromSlot);

        if (slot != null && slot.hasItem()) {
            ItemStack current = slot.getItem();
            previous = current.copy();

            if (fromSlot < tileRoost.getContainerSize()) {
                if (!moveItemStackTo(current, tileRoost.getContainerSize(), slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(current, 0, 1, false)) {
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

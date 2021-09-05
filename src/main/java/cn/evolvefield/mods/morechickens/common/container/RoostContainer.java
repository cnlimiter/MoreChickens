package cn.evolvefield.mods.morechickens.common.container;


import cn.evolvefield.mods.morechickens.common.block.RoostBlock;
import cn.evolvefield.mods.morechickens.common.container.slot.SlotChicken;
import cn.evolvefield.mods.morechickens.common.container.slot.SlotReadOnly;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;
import cn.evolvefield.mods.morechickens.init.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

import javax.annotation.Nullable;
import java.util.Objects;

public class RoostContainer extends Container {
    public final RoostTileEntity tileRoost;
    private int progress;
    public final IWorldPosCallable canInteractWithCallable;


    public RoostContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public RoostContainer(final int windowId, final PlayerInventory playerInventory, final RoostTileEntity tileEntity) {
        this(ModContainers.CONTAINER_ROOST, windowId, playerInventory, tileEntity);
    }


    public RoostContainer(@Nullable ContainerType<?> type, final int windowId, final PlayerInventory playerInventory, final RoostTileEntity tileEntity)
    {
        super(type, windowId);
        this.tileRoost = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos());

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


    private static RoostTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final TileEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof RoostTileEntity) {
            return (RoostTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }


    public TileEntity getTileEntity() {
        return tileRoost;
    }

    @Override//canInteractWith
    public boolean stillValid(PlayerEntity playerEntity) {
        return canInteractWithCallable.evaluate(
                (world, pos) -> world.getBlockState(pos).getBlock() instanceof RoostBlock
                        && playerEntity.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }




    @Override
    public void addSlotListener(IContainerListener listener) {
        super.addSlotListener(listener);

        //listener.refreshContainer(this, tileRoost);
    }


    @Override
    public void broadcastChanges() {
        //List<IContainerListener> containerListeners = ObfuscationReflectionHelper.getPrivateValue(Container.class,null,"field_75149_d");
        for (int i = 0; i < containerListeners.size(); ++i) {
            IContainerListener listener = containerListeners.get(i);
            if (progress != tileRoost.get(0)) {
                listener.setContainerData(this, 0, tileRoost.get(0));
            }
        }

        progress = tileRoost.get(0);
    }


    @Override
    public void setData(int id, int data) {
        tileRoost.set(id, data);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerEntity, int fromSlot) {
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

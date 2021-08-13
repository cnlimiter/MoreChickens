package cn.evolvefield.mods.morechickens.core.container;


import cn.evolvefield.mods.morechickens.core.container.slot.SlotChicken;
import cn.evolvefield.mods.morechickens.core.container.slot.SlotReadOnly;
import cn.evolvefield.mods.morechickens.core.container.slot.SlotSeeds;
import cn.evolvefield.mods.morechickens.core.tile.BreederTileEntity;
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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class BreederContainer extends Container {

    public final BreederTileEntity tileBreeder;
    private int progress;
    public final IWorldPosCallable canInteractWithCallable;

    public BreederContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public BreederContainer(final int windowId, final PlayerInventory playerInventory, final BreederTileEntity tileEntity) {
        this(ModContainers.CONTAINER_BREEDER, windowId, playerInventory, tileEntity);
    }


    public BreederContainer(@Nullable ContainerType<?> type, final int windowId, final PlayerInventory playerInventory, final BreederTileEntity tileEntity)
    {
        super(type, windowId);
        this.tileBreeder = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos());

        addSlot(new SlotChicken(tileEntity, 0, 44, 20));
        addSlot(new SlotChicken(tileEntity, 1, 62, 20));
        addSlot(new SlotSeeds(tileEntity, 2, 8, 20));

        for (int i = 0; i < 3; ++i) {
            addSlot(new SlotReadOnly(tileEntity, i + 3, 116 + i * 18, 20));
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

    private static BreederTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final TileEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof BreederTileEntity) {
            return (BreederTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }


    public TileEntity getTileEntity() {
        return tileBreeder;
    }


    @Override//canInteractWith
    public boolean stillValid(PlayerEntity player) {
        return tileBreeder.stillValid(player);
    }

    @Override
    public void addSlotListener(IContainerListener listener) {
        super.addSlotListener(listener);
        //listener.sendAllWindowProperties(this, this.breederInventory);
    }

    @Override//detectAndSendChanges
    public void broadcastChanges() {
        super.broadcastChanges();
        List<IContainerListener> containerListeners = ObfuscationReflectionHelper.getPrivateValue(Container.class,null,"field_75149_d");
        for (int i = 0; i < containerListeners.size(); ++i) {
            IContainerListener listener = containerListeners.get(i);

            if (progress != tileBreeder.get(0)) {
                listener.setContainerData(this, 0, tileBreeder.get(0));
            }
        }

        progress = tileBreeder.get(0);
    }

    @Override//updateProgressBar
    public void setData(int id, int data) {
        tileBreeder.set(id, data);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int fromSlot) {
        ItemStack previous = ItemStack.EMPTY;
        Slot slot = slots.get(fromSlot);

        if (slot != null && slot.hasItem()) {
            ItemStack current = slot.getItem();
            previous = current.copy();

            if (fromSlot < tileBreeder.getContainerSize()) {
                if (!moveItemStackTo(current, tileBreeder.getContainerSize(), slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(current, 0, 3, false)) {
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

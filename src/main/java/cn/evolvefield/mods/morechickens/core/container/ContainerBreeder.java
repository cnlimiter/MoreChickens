package cn.evolvefield.mods.morechickens.core.container;


import cn.evolvefield.mods.morechickens.core.container.slot.SlotChicken;
import cn.evolvefield.mods.morechickens.core.container.slot.SlotReadOnly;
import cn.evolvefield.mods.morechickens.core.container.slot.SlotSeeds;
import cn.evolvefield.mods.morechickens.core.tile.BreederTileEntity;
import cn.evolvefield.mods.morechickens.init.ModContainers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;


import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ContainerBreeder extends AbstractContainerMenu {

    public final BreederTileEntity tileBreeder;
    private int progress;
    public final ContainerLevelAccess canInteractWithCallable;

    public ContainerBreeder(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public ContainerBreeder(final int windowId, final Inventory playerInventory, final BreederTileEntity tileEntity) {
        this(ModContainers.CONTAINER_BREEDER, windowId, playerInventory, tileEntity);
    }


    public ContainerBreeder(@Nullable MenuType<?> type, final int windowId, final Inventory playerInventory, final BreederTileEntity tileEntity)
    {
        super(type, windowId);
        this.tileBreeder = tileEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());

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

    private static BreederTileEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof BreederTileEntity) {
            return (BreederTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }


    public BlockEntity getTileEntity() {
        return tileBreeder;
    }


    @Override//canInteractWith
    public boolean stillValid(Player player) {
        return tileBreeder.stillValid(player);
    }

    @Override
    public void addSlotListener(ContainerListener listener) {
        super.addSlotListener(listener);
        //listener.sendAllWindowProperties(this, this.breederInventory);
    }

    @Override//detectAndSendChanges
    public void broadcastChanges() {
        super.broadcastChanges();
        List<ContainerListener> containerListeners = ObfuscationReflectionHelper.getPrivateValue(AbstractContainerMenu.class,null,"f_38848_");
        for (int i = 0; i < containerListeners.size(); ++i) {
            ContainerListener listener = containerListeners.get(i);

            if (progress != tileBreeder.get(0)) {
                listener.dataChanged(this, 0, tileBreeder.get(0));
            }
        }

        progress = tileBreeder.get(0);
    }

    @Override//updateProgressBar
    public void setData(int id, int data) {
        tileBreeder.set(id, data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int fromSlot) {
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

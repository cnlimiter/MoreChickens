package cn.evolvefield.mods.morechickens.common.container;


import cn.evolvefield.mods.morechickens.common.container.base.ContainerBase;
import cn.evolvefield.mods.morechickens.common.container.base.LockedSlot;
import cn.evolvefield.mods.morechickens.common.container.base.SeedsSlot;
import cn.evolvefield.mods.morechickens.common.tile.BreederTileEntity;
import cn.evolvefield.mods.morechickens.init.ModContainers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;


import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class BreederContainer extends ContainerBase {

    private int progress;
    private final ContainerData data;
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0%");
    public BreederTileEntity breeder;

    public BreederContainer(int id, Container playerInventory,BreederTileEntity tileEntity) {
        this(ModContainers.CONTAINER_BREEDER, id, playerInventory, new SimpleContainer(1), new SimpleContainer(4),new SimpleContainerData(2),tileEntity);
    }

    public BreederContainer(int id, Inventory playerInventory, final FriendlyByteBuf data){
        this(id,playerInventory,getTileEntity(playerInventory,data));
    }


    public BreederContainer(@Nullable MenuType<?> type, final int windowId, Container playerInventory, Container inputInventory, Container outputInventory, ContainerData data, BreederTileEntity tileEntity)
    {
        super(type, windowId,playerInventory,null);
        this.breeder = tileEntity;

        checkContainerDataCount(data, 2);

        addSlot(getInputSlot(inputInventory, 0, 59 , 32));

        addSlot(new LockedSlot(outputInventory, 0 , 115 , 23 , true, false));
        addSlot(new LockedSlot(outputInventory, 1 , 115 + 18, 23 , true, false));
        addSlot(new LockedSlot(outputInventory, 2 , 115 , 23 + 18, true, false));
        addSlot(new LockedSlot(outputInventory, 3 , 115 + 18, 23 + 18, true, false));


        addPlayerInventorySlots();
        this.data = data;
        this.addDataSlots(data);
        this.breeder = tileEntity;
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

    public  Slot getInputSlot(Container inventory, int id, int x, int y){
        return new SeedsSlot(inventory, id, x, y);
    };

    @Override
    public int getInvOffset() {
        return -2;
    }

    @Override
    public int getInventorySize() {
        return 8;
    }



    @OnlyIn(Dist.CLIENT)
    public double getProgress() {
        int i = this.data.get(0);

        return i /1000.0;
    }

    public String getFormattedProgress() {
        return formatProgress(getProgress());
    }

    public String formatProgress(double progress) {
        return FORMATTER.format(progress);
    }


    @Override//detectAndSendChanges
    public void broadcastChanges() {
        super.broadcastChanges();
        for (int i = 0; i < containerListeners.size(); ++i) {
            ContainerListener listener = containerListeners.get(i);
            if (progress != data.get(0)) {
                listener.dataChanged(this, 0, data.get(0));
            }
        }
        progress = data.get(0);
    }


    @Override//updateProgressBar
    public void setData(int id, int data1) {
        data.set(id, data1);
    }




}

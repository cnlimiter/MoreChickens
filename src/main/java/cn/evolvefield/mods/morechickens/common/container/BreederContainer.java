package cn.evolvefield.mods.morechickens.common.container;

import cn.evolvefield.mods.morechickens.common.container.base.ContainerBase;
import cn.evolvefield.mods.morechickens.common.container.base.LockedSlot;
import cn.evolvefield.mods.morechickens.common.container.base.SeedsSlot;
import cn.evolvefield.mods.morechickens.common.tile.BreederTileEntity;
import cn.evolvefield.mods.morechickens.init.ModContainers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.Objects;

public  class BreederContainer extends ContainerBase {

    private int progress;
    private final IIntArray data;
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0%");
    public BreederTileEntity breeder;

    public BreederContainer(@Nullable ContainerType<?> containerType, int id, IInventory playerInventory, IInventory inputInventory, IInventory outputInventory, IIntArray data,BreederTileEntity tileEntity) {
        super(ModContainers.BREEDER_CONTAINER, id, playerInventory, null);
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

    public BreederContainer(int id, IInventory playerInventory,BreederTileEntity tileEntity) {
        this(ModContainers.BREEDER_CONTAINER, id, playerInventory, new Inventory(1), new Inventory(4),new IntArray(2),tileEntity);
    }

    public BreederContainer(int id, PlayerInventory playerInventory, final PacketBuffer data){
        this(id,playerInventory,getTileEntity(playerInventory,data));
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


    @Override
    public int getInvOffset() {
        return -2;
    }

    @Override
    public int getInventorySize() {
        return 8;
    }

    public  Slot getInputSlot(IInventory inventory, int id, int x, int y){
        return new SeedsSlot(inventory, id, x, y);
    };


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




    @Override//updateProgressBar
    public void setData(int id, int data1) {
        data.set(id, data1);
        broadcastChanges();
    }

}

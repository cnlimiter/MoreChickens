package cn.evolvefield.mods.morechickens.common.container;

import cn.evolvefield.mods.morechickens.common.container.base.ContainerBase;
import cn.evolvefield.mods.morechickens.common.container.base.LockedSlot;
import cn.evolvefield.mods.morechickens.common.container.base.SlotSeeds;
import cn.evolvefield.mods.morechickens.init.ModContainers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.text.DecimalFormat;

public  class BreederContainer extends ContainerBase {

    private int progress;
    private final IIntArray data;
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0%");

    public BreederContainer(ContainerType type, int id, PlayerInventory playerInventory, IInventory inputInventory, IInventory outputInventory, IIntArray data) {
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
    }

    public BreederContainer(int id, PlayerInventory playerInventory) {
        this(ModContainers.BREEDER_CONTAINER, id, playerInventory, new Inventory(1), new Inventory(4),new IntArray(2));
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
        return new SlotSeeds(inventory, id, x, y);
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


    @Override//detectAndSendChanges
    public void broadcastChanges() {
        super.broadcastChanges();
        for (int i = 0; i < containerListeners.size(); ++i) {
            IContainerListener listener = containerListeners.get(i);
            if (progress != data.get(0)) {
                listener.setContainerData(this, 0, data.get(0));
            }
        }

        progress = data.get(0);
    }


    @Override//updateProgressBar
    public void setData(int id, int data1) {
        data.set(id, data1);
    }

}

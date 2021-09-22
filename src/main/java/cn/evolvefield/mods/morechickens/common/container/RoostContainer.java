package cn.evolvefield.mods.morechickens.common.container;


import cn.evolvefield.mods.morechickens.common.block.RoostBlock;
import cn.evolvefield.mods.morechickens.common.container.base.ContainerBase;
import cn.evolvefield.mods.morechickens.common.container.base.LockedSlot;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;
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

public class RoostContainer extends ContainerBase {
    private int progress;
    private final ContainerData data;
    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0%");

    public final RoostTileEntity tileRoost;


    public RoostContainer(int id, Container playerInventory,final RoostTileEntity tileEntity) {
        this(ModContainers.CONTAINER_ROOST, id, playerInventory, new SimpleContainer(4),new SimpleContainerData(2),tileEntity);
    }

    public RoostContainer(int id, Inventory playerInventory, final FriendlyByteBuf data){
        this(id,playerInventory,getTileEntity(playerInventory,data));
    }


    public RoostContainer(@Nullable MenuType<?> type, int id, Container playerInventory, Container outputInventory, ContainerData data, RoostTileEntity tileEntity)
    {
        super(type, id, playerInventory, null);
        checkContainerDataCount(data, 2);

        addSlot(new LockedSlot(outputInventory, 0 , 115 , 23 , true, false));
        addSlot(new LockedSlot(outputInventory, 1 , 115 + 18, 23 , true, false));
        addSlot(new LockedSlot(outputInventory, 2 , 115 , 23 + 18, true, false));
        addSlot(new LockedSlot(outputInventory, 3 , 115 + 18, 23 + 18, true, false));


        addPlayerInventorySlots();
        this.data = data;
        this.addDataSlots(data);
        this.tileRoost = tileEntity;
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


//    @Override
//    public void broadcastChanges() {
//        for (int i = 0; i < containerListeners.size(); ++i) {
//            ContainerListener listener = containerListeners.get(i);
//            if (progress != data.get(0)) {
//                listener.dataChanged(this, 0, data.get(0));
//            }
//        }
//        progress = data.get(0);
//    }


    @Override
    public void setData(int id, int data1) {
        data.set(id, data1);
        broadcastChanges();
    }


}

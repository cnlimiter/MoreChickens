package cn.evolvefield.mods.morechickens.common.container.slot;


import cn.evolvefield.mods.morechickens.common.tile.ChickenContainerTileEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotSeeds extends Slot {
    public SlotSeeds(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.container instanceof ChickenContainerTileEntity) {
            ((ChickenContainerTileEntity) this.container).willNeedToUpdateChickenInfo();
        }
    }

    @Override //isItemValid
    public boolean mayPlace(ItemStack stack) {
        return ChickenContainerTileEntity.isSeed(stack);
    }

}

package cn.evolvefield.mods.morechickens.core.container.slot;


import cn.evolvefield.mods.morechickens.core.tile.ChickenContainerTileEntity;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotSeeds extends Slot {
    public SlotSeeds(Container inventoryIn, int index, int xPosition, int yPosition) {
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

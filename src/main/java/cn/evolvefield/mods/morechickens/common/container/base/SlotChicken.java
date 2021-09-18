package cn.evolvefield.mods.morechickens.common.container.base;


import cn.evolvefield.mods.morechickens.init.ModItems;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlotChicken extends Slot {
    public SlotChicken(IInventory inventory, int index, int x_pos, int y_pos) {
        super(inventory,index,x_pos,y_pos);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() == ModItems.ITEM_CHICKEN;
    }
}

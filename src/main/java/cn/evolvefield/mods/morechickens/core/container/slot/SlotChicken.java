package cn.evolvefield.mods.morechickens.core.container.slot;


import cn.evolvefield.mods.morechickens.core.data.DataChicken;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotChicken extends Slot {
    public SlotChicken(Container inventory, int index, int x_pos, int y_pos) {
        super(inventory,index,x_pos,y_pos);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return DataChicken.isChicken(stack);
    }
}

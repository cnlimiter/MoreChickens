package cn.evolvefield.mods.morechickens.common.container.base;


import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SeedsSlot extends Slot {
    public SeedsSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }



    @Override //isItemValid
    public boolean mayPlace(ItemStack stack) {
        return isSeed(stack);
    }
    public static boolean isSeed(ItemStack stack) {
        Item item = stack.getItem();
        return (item == Items.WHEAT_SEEDS || item == Items.MELON_SEEDS || item == Items.PUMPKIN_SEEDS || item == Items.BEETROOT_SEEDS);
    }
}

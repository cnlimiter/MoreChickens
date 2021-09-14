package cn.evolvefield.mods.morechickens.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.Comparator;

public class ItemUtil {
    public static final Comparator<ItemStack> ITEM_COMPARATOR = (item1, item2) -> {
        int cmp = item2.getItem().hashCode() - item1.getItem().hashCode();
        if (cmp != 0) {
            return cmp;
        } else {
            cmp = item2.getDamageValue() - item1.getDamageValue();
            if (cmp != 0) {
                return cmp;
            } else {
                CompoundNBT c1 = item1.getTag();
                CompoundNBT c2 = item2.getTag();
                if (c1 == null && c2 == null) {
                    return 0;
                } else if (c1 == null) {
                    return 1;
                } else {
                    return c2 == null ? -1 : c1.hashCode() - c2.hashCode();
                }
            }
        }
    };
}

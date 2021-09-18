package cn.evolvefield.mods.morechickens.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Comparator;
import java.util.Iterator;

public class ItemUtils {
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

    public ItemUtils() {
    }

    public static ItemStack itemStackAmount(int amount, ItemStack stack, PlayerEntity player) {
        if (stack != null && !stack.isEmpty()) {
            if (player != null && player.abilities.instabuild) {
                return stack;
            } else {
                stack.setCount(stack.getCount() + amount);
                if (stack.getCount() <= 0) {
                    stack.setCount(0);
                    return ItemStack.EMPTY;
                } else {
                    if (stack.getCount() > stack.getMaxStackSize()) {
                        stack.setCount(stack.getMaxStackSize());
                    }

                    return stack;
                }
            }
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static ItemStack decrItemStack(ItemStack stack, PlayerEntity player) {
        return itemStackAmount(-1, stack, player);
    }

    public static ItemStack incrItemStack(ItemStack stack, PlayerEntity player) {
        return itemStackAmount(1, stack, player);
    }

    public static boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
        if (stack1 != null && stack2 != null) {
            if (stack1.getItem() == stack2.getItem()) {
                return stack1.getDamageValue() == stack2.getDamageValue();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void saveInventory(CompoundNBT compound, String name, IInventory inv) {
        ListNBT tagList = new ListNBT();

        for(int i = 0; i < inv.getContainerSize(); ++i) {
            if (!inv.getItem(i).isEmpty()) {
                CompoundNBT slot = new CompoundNBT();
                slot.putInt("Slot", i);
                inv.getItem(i).save(slot);
                tagList.add(slot);
            }
        }

        compound.put(name, tagList);
    }

    public static void saveInventory(CompoundNBT compound, String name, NonNullList<ItemStack> inv) {
        ListNBT tagList = new ListNBT();

        for(int i = 0; i < inv.size(); ++i) {
            if (!((ItemStack)inv.get(i)).isEmpty()) {
                CompoundNBT slot = new CompoundNBT();
                slot.putInt("Slot", i);
                ((ItemStack)inv.get(i)).save(slot);
                tagList.add(slot);
            }
        }

        compound.put(name, tagList);
    }

    public static void saveItemList(CompoundNBT compound, String name, NonNullList<ItemStack> list) {
        saveItemList(compound, name, list, true);
    }

    public static void saveItemList(CompoundNBT compound, String name, NonNullList<ItemStack> list, boolean includeEmpty) {
        ListNBT itemList = new ListNBT();
        Iterator var5 = list.iterator();

        while(true) {
            ItemStack stack;
            do {
                if (!var5.hasNext()) {
                    compound.put(name, itemList);
                    return;
                }

                stack = (ItemStack)var5.next();
            } while(!includeEmpty && stack.isEmpty());

            itemList.add(stack.save(new CompoundNBT()));
        }
    }

    public static void readInventory(CompoundNBT compound, String name, IInventory inv) {
        if (compound.contains(name)) {
            ListNBT tagList = compound.getList(name, 10);

            for(int i = 0; i < tagList.size(); ++i) {
                CompoundNBT slot = tagList.getCompound(i);
                int j = slot.getInt("Slot");
                if (j >= 0 && j < inv.getContainerSize()) {
                    inv.setItem(j, ItemStack.of(slot));
                }
            }

        }
    }

    public static void readInventory(CompoundNBT compound, String name, NonNullList<ItemStack> inv) {
        if (compound.contains(name)) {
            ListNBT tagList = compound.getList(name, 10);

            for(int i = 0; i < tagList.size(); ++i) {
                CompoundNBT slot = tagList.getCompound(i);
                int j = slot.getInt("Slot");
                if (j >= 0 && j < inv.size()) {
                    inv.set(j, ItemStack.of(slot));
                }
            }

        }
    }

    public static NonNullList<ItemStack> readItemList(CompoundNBT compound, String name, boolean includeEmpty) {
        NonNullList<ItemStack> items = NonNullList.create();
        if (!compound.contains(name)) {
            return items;
        } else {
            ListNBT itemList = compound.getList(name, 10);

            for(int i = 0; i < itemList.size(); ++i) {
                ItemStack item = ItemStack.of(itemList.getCompound(i));
                if (!includeEmpty) {
                    if (!item.isEmpty()) {
                        items.add(item);
                    }
                } else {
                    items.add(item);
                }
            }

            return items;
        }
    }

    public static NonNullList<ItemStack> readItemList(CompoundNBT compound, String name) {
        return readItemList(compound, name, true);
    }

    public static void readItemList(CompoundNBT compound, String name, NonNullList<ItemStack> list) {
        if (compound.contains(name)) {
            ListNBT itemList = compound.getList(name, 10);

            for(int i = 0; i < itemList.size() && i < list.size(); ++i) {
                list.set(i, ItemStack.of(itemList.getCompound(i)));
            }

        }
    }

    public static void removeStackFromSlot(IInventory inventory, int index) {
        inventory.setItem(index, ItemStack.EMPTY);
    }

    public static boolean isStackable(ItemStack stack1, ItemStack stack2) {
        return ItemHandlerHelper.canItemStacksStack(stack1, stack2);
    }

    public static CompoundNBT writeOverstackedItem(CompoundNBT compound, ItemStack stack) {
        stack.save(compound);
        compound.remove("Count");
        compound.putInt("Count", stack.getCount());
        return compound;
    }

    public static ItemStack readOverstackedItem(CompoundNBT compound) {
        CompoundNBT data = compound.copy();
        int count = data.getInt("Count");
        data.remove("Count");
        data.putByte("Count", (byte)1);
        ItemStack stack = ItemStack.of(data);
        stack.setCount(count);
        return stack;
    }
}

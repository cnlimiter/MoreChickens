package cn.evolvefield.mods.morechickens.init.util;

import net.minecraft.core.Direction;

import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class InventoryUtil {
    private static boolean isEmpty(IItemHandler itemHandler)
    {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++)
        {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.getCount() > 0)
            {
                return false;
            }
        }
        return true;
    }
    private static boolean canInsertItemInSlot(Container inventoryIn, ItemStack stack, int index, Direction side)
    {
        if (!inventoryIn.canPlaceItem(index, stack))
        {
            return false;
        }
        else
        {
            return !(inventoryIn instanceof WorldlyContainer) || ((WorldlyContainer)inventoryIn).canPlaceItemThroughFace(index, stack, side);
        }
    }

    private static boolean canCombine(ItemStack stack1, ItemStack stack2)
    {
        if (stack1.getItem() != stack2.getItem())
        {
            return false;
        }
        else if (stack1.getCount() > stack1.getMaxStackSize())
        {
            return false;
        }
        else
        {
            return ItemStack.tagMatches(stack1, stack2);
        }
    }

    private static ItemStack insertStack(BlockEntity source, Container destination, ItemStack stack, int index, Direction direction)
    {
        ItemStack itemstack = destination.getItem(index);

        if (canInsertItemInSlot(destination, stack, index, direction))
        {
            boolean flag = false;
            boolean flag1 = destination.isEmpty();

            if (itemstack.isEmpty())
            {
                destination.setItem(index, stack);
                stack = ItemStack.EMPTY;
                flag = true;
            }
            else if (canCombine(itemstack, stack))
            {
                int i = stack.getMaxStackSize() - itemstack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.shrink(j);
                itemstack.grow(j);
                flag = j > 0;
            }

            if (flag)
            {
                if (flag1 && destination instanceof HopperBlockEntity)
                {
                    HopperBlockEntity tileentityhopper1 = (HopperBlockEntity)destination;
                    if (!tileentityhopper1.isOnCustomCooldown())
                    {
                        int k = 0;
                        if (source instanceof HopperBlockEntity)
                        {
                            HopperBlockEntity tileentityhopper = (HopperBlockEntity)source;
                            if (tileentityhopper1.getLastUpdateTime() >= tileentityhopper.getLastUpdateTime())
                            {
                                k = 1;
                            }
                        }
                        tileentityhopper1.setCooldown(8 - k);
                    }
                }
                destination.setChanged();
            }
        }

        return stack;
    }

    private static ItemStack insertStack(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot)
    {
        ItemStack itemstack = destInventory.getStackInSlot(slot);

        if (destInventory.insertItem(slot, stack, true).isEmpty())
        {
            boolean insertedItem = false;
            boolean inventoryWasEmpty = isEmpty(destInventory);

            if (itemstack.isEmpty())
            {
                destInventory.insertItem(slot, stack, false);
                stack = ItemStack.EMPTY;
                insertedItem = true;
            }
            else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack))
            {
                int originalSize = stack.getCount();
                stack = destInventory.insertItem(slot, stack, false);
                insertedItem = originalSize < stack.getCount();
            }

            if (insertedItem)
            {
                if (inventoryWasEmpty && destination instanceof HopperBlockEntity)
                {
                    HopperBlockEntity destinationHopper = (HopperBlockEntity)destination;

                    if (!destinationHopper.isOnCustomCooldown())
                    {
                        int k = 0;
                        if (source instanceof HopperBlockEntity)
                        {
                            if (destinationHopper.getLastUpdateTime() >= ((HopperBlockEntity) source).getLastUpdateTime())
                            {
                                k = 1;
                            }
                        }
                        destinationHopper.setCooldown(8 - k);
                    }
                }
            }
        }

        return stack;
    }

    public static ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack)
    {
        for (int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++)
        {
            stack = insertStack(source, destination, destInventory, stack, slot);
        }
        return stack;
    }

    public static ItemStack putStackInInventoryAllSlots(BlockEntity fromSource, Container destination, ItemStack stack, @Nullable Direction direction)
    {
        if (destination instanceof WorldlyContainer && direction != null)
        {
            WorldlyContainer isidedinventory = (WorldlyContainer)destination;
            int[] aint = isidedinventory.getSlotsForFace(direction);

            for (int k = 0; k < aint.length && !stack.isEmpty(); ++k)
            {
                stack = insertStack(fromSource, destination,stack, aint[k],direction);
            }
        }
        else
        {
            int i = destination.getContainerSize();

            for (int j = 0; j < i && !stack.isEmpty(); ++j)
            {
                stack = insertStack(fromSource, destination, stack, j, direction);
            }
        }

        return stack;
    }

}

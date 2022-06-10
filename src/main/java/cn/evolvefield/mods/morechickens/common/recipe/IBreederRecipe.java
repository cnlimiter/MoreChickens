package cn.evolvefield.mods.morechickens.common.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/6 18:54
 * Version: 1.0
 */
public interface IBreederRecipe extends Recipe<Container> {
    ItemStack getInput1();
    ItemStack getInput2();
    ItemStack getFood();
    List<ItemStack> getOutput();
}

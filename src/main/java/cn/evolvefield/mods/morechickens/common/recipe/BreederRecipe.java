package cn.evolvefield.mods.morechickens.common.recipe;

import cn.evolvefield.mods.atomlib.common.recipe.ISpecialRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/6 18:59
 * Version: 1.0
 */
public class BreederRecipe implements ISpecialRecipe, IBreederRecipe {
    @Override
    public ItemStack assemble(IItemHandler var1) {
        return null;
    }

    @Override
    public ItemStack getInput1() {
        return null;
    }

    @Override
    public ItemStack getInput2() {
        return null;
    }

    @Override
    public ItemStack getFood() {
        return null;
    }

    @Override
    public List<ItemStack> getOutput() {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }
}

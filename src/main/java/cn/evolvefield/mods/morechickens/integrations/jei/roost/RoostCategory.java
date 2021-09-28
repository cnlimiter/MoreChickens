package cn.evolvefield.mods.morechickens.integrations.jei.roost;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.recipe.RoostRecipe;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import cn.evolvefield.mods.morechickens.integrations.jei.JEIPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RoostCategory implements IRecipeCategory<RoostRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public RoostCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(MoreChickens.MODID, "textures/gui/jei/roost.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 90, 36);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.BLOCK_ROOST));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return JEIPlugin.CATEGORY_ROOST_UID;
    }

    @Nonnull
    @Override
    public Class<? extends RoostRecipe> getRecipeClass() {
        return RoostRecipe.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.get("jei.chickens.roost_recipe");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }



    @Override
    public void setIngredients(@Nonnull RoostRecipe recipe, @Nonnull IIngredients ingredients) {
        ingredients.setInput(JEIPlugin.CHICKEN_INGREDIENT, recipe.ingredient.get());

        List<List<ItemStack>> outputList = new ArrayList<>();
        recipe.getRecipeOutputs().forEach((stack, value) -> {
            List<ItemStack> innerList = new ArrayList<>();
            IntStream.range(value.get(0).getAsInt(), value.get(1).getAsInt() + 1).forEach((i) -> {
                ItemStack newStack = stack.copy();
                newStack.setCount(i);
                innerList.add(newStack);
            });
            outputList.add(innerList);
        });

        ingredients.setOutputLists(VanillaTypes.ITEM, outputList);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull RoostRecipe recipe, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiIngredientGroup<ChickenData> ingredientStacks = recipeLayout.getIngredientsGroup(JEIPlugin.CHICKEN_INGREDIENT);

        ingredientStacks.init(0, true, 5, 27);
        ingredientStacks.set(ingredients);

        int startX = 68;
        int startY = 10;
        if (ingredients.getOutputs(VanillaTypes.ITEM).size() > 0) {
            List<ItemStack> outputs = ingredients.getOutputs(VanillaTypes.ITEM).iterator().next();
            int offset = ingredients.getInputs(JEIPlugin.CHICKEN_INGREDIENT).size();
            IntStream.range(offset, outputs.size() + offset).forEach((i) -> {
                if (i > 3 + offset) {
                    return;
                }
                itemStacks.init(i, false, startX + ((i - offset) * 18), startY + ((int) Math.floor(((float) i - offset) / 3.0F) * 18));
            });
        }

        itemStacks.set(ingredients);
    }

}
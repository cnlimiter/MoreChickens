package cn.evolvefield.mods.morechickens.integrations.jei.roost;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import cn.evolvefield.mods.morechickens.integrations.jei.JEIAddon;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class RoostCategory implements IRecipeCategory<ItemStack> {

    private IGuiHelper helper;

    public RoostCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(new ResourceLocation(MoreChickens.MODID, "textures/gui/container/jei_input_output.png"), 0, 0, 72, 49);
    }

    @Override
    public IDrawable getIcon() {
        return helper.createDrawableIngredient(new ItemStack(ModBlocks.BLOCK_ROOST));
    }

    @Override
    public void setIngredients(ItemStack recipe, IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, recipe);
        //ingredients.setOutput(VanillaTypes.ITEM, VillagerItem.getBabyVillager());
    }

    @Override
    public String getTitle() {
        return new TranslationTextComponent("jei.easy_villagers.breeding").getString();
    }

    @Override
    public ResourceLocation getUid() {
        return JEIAddon.CATEGORY_ROOST;
    }

    @Override
    public Class<? extends ItemStack> getRecipeClass() {
        return ItemStack.class;
    }

    @Override
    public void setRecipe(IRecipeLayout layout, ItemStack wrapper, IIngredients ingredients) {
        IGuiItemStackGroup group = layout.getItemStacks();
        group.init(0, true, 0, 0);
        group.set(0, wrapper);
        group.init(1, true, 18, 0);
        group.init(2, true, 36, 0);
        group.init(3, true, 54, 0);
        group.init(4, true, 0, 31);
        //group.set(4, VillagerItem.getBabyVillager());
        group.init(5, true, 18, 31);
        group.init(6, true, 36, 31);
        group.init(7, true, 54, 31);
    }

}
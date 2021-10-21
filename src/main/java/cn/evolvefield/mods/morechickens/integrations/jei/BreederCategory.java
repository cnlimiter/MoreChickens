package cn.evolvefield.mods.morechickens.integrations.jei;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.common.data.ChickenRegistry;
import cn.evolvefield.mods.morechickens.common.data.ChickenUtils;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.EntityIngredient;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BreederCategory extends BaseCategory<ChickenData> {
    public static final ResourceLocation GUI_BACK = new ResourceLocation(MoreChickens.MODID, "textures/gui/jei/breeding.png");
    public static final ResourceLocation ID = new ResourceLocation(MoreChickens.MODID, "breeding");

    public BreederCategory(IGuiHelper guiHelper) {
        super(guiHelper, ID,
                I18n.get("jei.chickens.category.breeding"),
                guiHelper.drawableBuilder(GUI_BACK, 0, 0, 160, 60).addPadding(0, 0, 0, 0).build(),
                guiHelper.createDrawableIngredient(ModBlocks.BLOCK_BREEDER.asItem().getDefaultInstance()),
                ChickenData.class);
    }

    public static List<ChickenData> getBreedingRecipes() {
        List<ChickenData> recipes = new ArrayList<>();
        ChickenRegistry.FAMILY_TREE.values().forEach(children -> children.forEach(recipes::add));
        return recipes;
    }

    @Override
    public void setIngredients(@NotNull ChickenData data, @NotNull IIngredients ingredients) {
//        List<List<ItemStack>> list = new ArrayList<>();
//        List<ItemStack> food = new ArrayList<>();
//        food.add(new ItemStack(Items.WHEAT_SEEDS));
//        food.add(new ItemStack(Items.PUMPKIN_SEEDS));
//        food.add(new ItemStack(Items.MELON_SEEDS));
//        food.add(new ItemStack(Items.BEETROOT_SEEDS));
//        list.add(food);
//        ingredients.setInputLists(VanillaTypes.ITEM, list);

        List<EntityIngredient> entities = new ArrayList<>();
        entities.add(new EntityIngredient(ChickenUtils.getChickenDataByName(data.getParent1())));
        entities.add(new EntityIngredient(ChickenUtils.getChickenDataByName(data.getParent2())));

        ingredients.setInputs(JEIPlugin.ENTITY_INGREDIENT, entities);
        ingredients.setOutput(JEIPlugin.ENTITY_INGREDIENT, new EntityIngredient(data));
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout iRecipeLayout, @NotNull ChickenData data, @NotNull IIngredients ingredients) {
        IGuiIngredientGroup<EntityIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(JEIPlugin.ENTITY_INGREDIENT);

        ingredientStacks.init(0, true, 6, 6);
        ingredientStacks.init(1, true, 60, 6);
        ingredientStacks.init(2, false, 130, 18);
        ingredientStacks.set(0, ingredients.getInputs(JEIPlugin.ENTITY_INGREDIENT).get(0));
        ingredientStacks.set(1, ingredients.getInputs(JEIPlugin.ENTITY_INGREDIENT).get(1));
        ingredientStacks.set(2, ingredients.getOutputs(JEIPlugin.ENTITY_INGREDIENT).get(0));

    }

    @Override
    public void draw(@NotNull ChickenData data, @NotNull MatrixStack matrix, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontRenderer = minecraft.font;
        DecimalFormat decimalFormat = new DecimalFormat("##%");

        info.draw(matrix, 115, 40);
        //fontRenderer.draw(matrix, decimalFormat.format(data.getChance()), 130, 40, 0xff808080);

        fontRenderer.draw(matrix, decimalFormat.format(ChickenRegistry.getAdjustedWeightForChild(data)), 90, 35, 0xff808080);
    }

    @NotNull
    @Override
    public List<ITextComponent> getTooltipStrings(@NotNull ChickenData data, double mouseX, double mouseY) {
        double infoX = 115D;
        double infoY = 40D;
        if (mouseX >= infoX && mouseX <= infoX + 9D && mouseY >= infoY && mouseY <= infoY + 9D ) {
            return Collections.singletonList(new StringTextComponent(I18n.get("jei." + MoreChickens.MODID + ".breed_chance.info")));
        }
        return super.getTooltipStrings(data, mouseX, mouseY);
    }


}

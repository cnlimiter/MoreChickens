package cn.evolvefield.mods.morechickens.integrations.jei;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredient;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredientFactory;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredientHelper;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredientRenderer;
import cn.evolvefield.mods.morechickens.integrations.jei.roost.RoostCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import java.util.*;


@JeiPlugin
public class JEIAddon implements IModPlugin {
    public static final IIngredientType<ChickenIngredient> BEE_INGREDIENT = () -> ChickenIngredient.class;
    public static final ResourceLocation CATEGORY_ROOST = new ResourceLocation(MoreChickens.MODID, "roosting");


    public JEIAddon(){
        ChickenIngredientFactory.getOrCreateList();
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MoreChickens.MODID, "chickens");
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        //registration.addRecipes(foods, CATEGORY_ROOST);
        Map<String, ChickenIngredient> chickenList = ChickenIngredientFactory.getOrCreateList();
        for (Map.Entry<String, ChickenIngredient> entry : chickenList.entrySet()) {
            String id = entry.getKey();
            registration.addIngredientInfo(entry.getValue(), BEE_INGREDIENT, "chickens.ingredient.description." + (id));


        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new RoostCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        Collection<ChickenIngredient> ingredients = ChickenIngredientFactory.getOrCreateList(true).values();
        registration.register(BEE_INGREDIENT, new ArrayList<>(ingredients), new ChickenIngredientHelper(), new ChickenIngredientRenderer());
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_ROOST), CATEGORY_ROOST);
    }



}

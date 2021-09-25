package cn.evolvefield.mods.morechickens.integrations.jei;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.recipe.RoostRecipe;
import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import cn.evolvefield.mods.morechickens.init.ModItems;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredientHelper;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredientRenderer;
import cn.evolvefield.mods.morechickens.integrations.jei.roost.RoostCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final IIngredientType<ChickenType> CHICKEN_INGREDIENT = () -> ChickenType.class;
    public static final ResourceLocation CATEGORY_ROOST_UID = new ResourceLocation(MoreChickens.MODID, "roost_recipe");


    public JEIPlugin(){
        ChickenType.getTypes();
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MoreChickens.MODID, "chickens");
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        Map<ResourceLocation, IRecipe<IInventory>> advancedBeehiveRecipesMap = recipeManager.byType(RoostRecipe.ROOST);
        registration.addRecipes(advancedBeehiveRecipesMap.values(), CATEGORY_ROOST_UID);

        Map<String, ChickenType> chickenList = ChickenType.getTypes();
        for (Map.Entry<String, ChickenType> entry : chickenList.entrySet()) {
            String id = entry.getKey();
            registration.addIngredientInfo(entry.getValue(), CHICKEN_INGREDIENT, "chickens.ingredient.description." + (id));
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new RoostCategory(guiHelper));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        Collection<ChickenType> ingredients = ChickenType.Types.values();
        registration.register(CHICKEN_INGREDIENT, new ArrayList<>(ingredients), new ChickenIngredientHelper(), new ChickenIngredientRenderer());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(ModItems.ITEM_CHICKEN.getItem());

    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_ROOST), CATEGORY_ROOST_UID);
    }



}

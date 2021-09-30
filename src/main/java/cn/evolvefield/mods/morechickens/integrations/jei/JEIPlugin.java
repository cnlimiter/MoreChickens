package cn.evolvefield.mods.morechickens.integrations.jei;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import cn.evolvefield.mods.morechickens.init.ModItems;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredientHelper;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredientRenderer;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.EntityIngredient;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;


@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final IIngredientType<EntityIngredient> ENTITY_INGREDIENT = () -> EntityIngredient.class;

    public JEIPlugin(){
        EntityIngredient.getTypes();
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MoreChickens.MODID, "chickens");
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        World clientWorld = Minecraft.getInstance().level;
        if (clientWorld != null) {
            registration.addRecipes(BreederCategory.getBreedingRecipes(), BreederCategory.ID);
        }

        Map<String, EntityIngredient> chickenList = EntityIngredient.getTypes();
        for (Map.Entry<String, EntityIngredient> entry : chickenList.entrySet()) {
            String id = entry.getKey();
            registration.addIngredientInfo(entry.getValue(), ENTITY_INGREDIENT, "chickens.ingredient.description." + (id));
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new BreederCategory(guiHelper));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        Collection<EntityIngredient> ingredients = ChickenData.Types.values().stream().map(EntityIngredient::new).collect(Collectors.toList());
        registration.register(ENTITY_INGREDIENT, new ArrayList<>(ingredients), new ChickenIngredientHelper(), new ChickenIngredientRenderer());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(ModItems.ITEM_CHICKEN.getItem());

    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_BREEDER.asItem()), BreederCategory.ID);

    }



}

package cn.evolvefield.mods.morechickens.integrations.jei;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredient;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredientFactory;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredientHelper;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredientRenderer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.*;


@JeiPlugin
public class JEIAddon implements IModPlugin {
    public static final IIngredientType<ChickenIngredient> BEE_INGREDIENT = () -> ChickenIngredient.class;


    public JEIAddon(){
        ChickenIngredientFactory.getOrCreateList();
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        Map<String, ChickenIngredient> chickenList = ChickenIngredientFactory.getOrCreateList();
        for (Map.Entry<String, ChickenIngredient> entry : chickenList.entrySet()) {
            String id = entry.getKey();
            registration.addIngredientInfo(entry.getValue(), BEE_INGREDIENT, "chickens.ingredient.description." + (id));


        }
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        Collection<ChickenIngredient> ingredients = ChickenIngredientFactory.getOrCreateList(true).values();
        registration.register(BEE_INGREDIENT, new ArrayList<>(ingredients), new ChickenIngredientHelper(), new ChickenIngredientRenderer());
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        IModPlugin.super.registerRecipeCatalysts(registry);
    }



    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MoreChickens.MODID, "jei");
    }


}

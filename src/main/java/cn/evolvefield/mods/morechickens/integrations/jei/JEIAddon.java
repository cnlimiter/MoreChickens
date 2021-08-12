package cn.evolvefield.mods.morechickens.integrations.jei;

import cn.evolvefield.mods.morechickens.MoreChickens;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;


@JeiPlugin
public class JEIAddon implements IModPlugin {

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        IModPlugin.super.registerRecipes(registry);

        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        IModPlugin.super.registerRecipeCatalysts(registry);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories();
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MoreChickens.MODID, "jei");
    }


}

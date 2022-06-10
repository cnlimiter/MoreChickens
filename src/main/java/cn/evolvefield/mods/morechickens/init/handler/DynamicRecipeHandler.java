package cn.evolvefield.mods.morechickens.init.handler;

import cn.evolvefield.mods.atomlib.init.event.RegisterRecipesEvent;
import cn.evolvefield.mods.morechickens.common.item.ChickenIns;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.avaritia.Static;
import nova.committee.avaritia.common.item.singularity.Singularity;
import nova.committee.avaritia.common.recipe.CompressorRecipe;
import nova.committee.avaritia.common.recipe.ShapelessExtremeCraftingRecipe;
import nova.committee.avaritia.init.ModRecipes;
import nova.committee.avaritia.init.event.RegisterRecipesEvent;
import nova.committee.avaritia.util.SingularityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/5/15 20:34
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DynamicRecipeHandler {
    @SubscribeEvent
    public static void onRegisterRecipes(RegisterRecipesEvent event) {

        for (var singularity : ChickenRegistryHandler.getInstance().getChickens()) {
            var compressorRecipe = makeSingularityRecipe(singularity);

            if (compressorRecipe != null)
                event.register(compressorRecipe);
        }




    }


    private static CompressorRecipe makeSingularityRecipe(ChickenIns singularity) {
        var ingredient = singularity.getIngredient();
        if (ingredient == Ingredient.EMPTY)
            return null;

        var id = singularity.getId();
        var recipeId = new ResourceLocation(Static.MOD_ID, id.getPath() + "_singularity");
        var output = SingularityUtils.getItemForSingularity(singularity);
        int ingredientCount = singularity.getIngredientCount();
        int timeRequired = singularity.getTimeRequired();

        return new CompressorRecipe(recipeId, ingredient, output, ingredientCount, timeRequired);
    }

    public static ShapelessExtremeCraftingRecipe addExtremeShapelessRecipe(ItemStack result, List<ItemStack> ingredients) {
        List<ItemStack> arraylist = new ArrayList<>();

        for (ItemStack stack : ingredients) {
            if (stack != null) {
                arraylist.add(stack.copy());
            } else {
                throw new RuntimeException("Invalid shapeless recipes!");
            }
        }

        return new ShapelessExtremeCraftingRecipe(result.getItem().getRegistryName(), getList(arraylist), result);
    }

    private static NonNullList<Ingredient> getList(List<ItemStack> arrayList) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        for (ItemStack stack : arrayList) {
            ingredients.add(Ingredient.of(stack));
        }
        return ingredients;
    }
}

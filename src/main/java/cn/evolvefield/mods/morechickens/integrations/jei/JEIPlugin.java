package cn.evolvefield.mods.morechickens.integrations.jei;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.common.data.ChickenRegistry;
import cn.evolvefield.mods.morechickens.common.data.ChickenUtils;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;


@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final IIngredientType<EntityIngredient> ENTITY_INGREDIENT = () -> EntityIngredient.class;

    public JEIPlugin(){
        EntityIngredient.getTypes();
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MoreChickens.MODID, "jei");
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        World clientWorld = Minecraft.getInstance().level;
        if (clientWorld != null) {
            registration.addRecipes(BreederCategory.getBreedingRecipes(), BreederCategory.ID);
            registerInfoDesc(registration);
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
        registration.register(ENTITY_INGREDIENT,
                ChickenRegistry.Types.values().stream().map(EntityIngredient::new).collect(Collectors.toList()),
                new ChickenIngredientHelper(),
                new ChickenIngredientRenderer());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(ModItems.ITEM_CHICKEN.getItem());

    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_BREEDER.asItem()), BreederCategory.ID);

    }


    public void registerInfoDesc(IRecipeRegistration registration) {
        for (EntityIngredient ingredient : registration.getIngredientManager().getAllIngredients(ENTITY_INGREDIENT)) {
            if (ingredient.getChickenData() != null && ingredient.getEntity() instanceof BaseChickenEntity) {
                ChickenData customBee = ingredient.getChickenData();

                StringBuilder stats = new StringBuilder();
                String aqua = TextFormatting.DARK_AQUA.toString();
                String purple = TextFormatting.DARK_PURPLE.toString();


                stats.append(aqua).append(I18n.get("jei.chickens.info.base_health")).append(purple).append("4").append("\n");

                stats.append(aqua).append(I18n.get("jei.chickens.info.breedable")).append(purple).append(StringUtils.capitalize(String.valueOf(customBee.hasParents()))).append("\n");
                if (customBee.hasParents()) {
                    stats.append(aqua).append(I18n.get("jei.chickens.info.parents")).append(purple);
                        stats.append(StringUtils.capitalize("  "+ I18n.get("text.chickens.name." + customBee.getParent1()))).append(",\n")
                                .append(StringUtils.capitalize("  "+ I18n.get("text.chickens.name." + customBee.getParent2())));

                }

                registration.addIngredientInfo(ingredient, ENTITY_INGREDIENT, stats.toString());
            }
        }
    }



}

package cn.evolvefield.mods.morechickens.integrations.jei.ingredients;

import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.Objects;

public class ChickenIngredientHelper implements IIngredientHelper<EntityIngredient>
{


    @Nullable
    @Override
    public EntityIngredient getMatch(Iterable<EntityIngredient> iterable, EntityIngredient type) {
        for (EntityIngredient ingredient : iterable) {
            if (Objects.equals(ingredient.getChickenData().name, type.getChickenData().name)) {
                return ingredient;
            }
        }
        return null;
    }

    @Override
    public String getDisplayName(EntityIngredient type) {
        ChickenData chickenData = ChickenReloadListener.INSTANCE.getData(type.getChickenData().name);
        if (chickenData != null) {
            return new TranslationTextComponent("text.chickens.name."+ type.getChickenData().name).getString();
        }
        return "ChickenType:chicken:" + type.getChickenData().name;
    }

    @Override
    public String getUniqueId(EntityIngredient type) {
        return "ChickenType:" + type.getChickenData().name;
    }

    @Override
    public String getModId(EntityIngredient type) {
        return ModEntities.BASE_CHICKEN.get().getRegistryName().getNamespace();
    }

    @Override
    public String getResourceId(EntityIngredient type) {
        return ModEntities.BASE_CHICKEN.get().getRegistryName().getPath();
    }

    @Override
    public EntityIngredient copyIngredient(EntityIngredient type) {
            return type;
    }

    @Override
    public String getErrorInfo(@Nullable EntityIngredient type) {
        if (type == null) {
            return "ChickenType:null";
        }
        if (type.getChickenData().name == null) {
            return "ChickenType:chicken:null";
        }
        return "ChickenType:chicken:" + type.getChickenData().name;
    }
}

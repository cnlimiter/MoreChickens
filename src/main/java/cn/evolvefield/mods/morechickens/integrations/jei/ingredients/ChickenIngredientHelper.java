package cn.evolvefield.mods.morechickens.integrations.jei.ingredients;

import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.Objects;

public class ChickenIngredientHelper implements IIngredientHelper<ChickenData>
{


    @Nullable
    @Override
    public ChickenData getMatch(Iterable<ChickenData> iterable, ChickenData type) {
        for (ChickenData ingredient : iterable) {
            if (Objects.equals(ingredient.name, type.name)) {
                return ingredient;
            }
        }
        return null;
    }

    @Override
    public String getDisplayName(ChickenData type) {
        ChickenData chickenData = ChickenReloadListener.INSTANCE.getData(type.name);
        if (chickenData != null) {
            return new TranslationTextComponent("text.chickens.name."+ type.name).getString();
        }
        return "ChickenType:chicken:" + type.name;
    }

    @Override
    public String getUniqueId(ChickenData type) {
        return "ChickenType:" + type.name;
    }

    @Override
    public String getModId(ChickenData type) {
        return ModEntities.BASE_CHICKEN.get().getRegistryName().getNamespace();
    }

    @Override
    public String getResourceId(ChickenData type) {
        return ModEntities.BASE_CHICKEN.get().getRegistryName().getPath();
    }

    @Override
    public ChickenData copyIngredient(ChickenData type) {
            return ChickenData.Types.get(type.name);
    }

    @Override
    public String getErrorInfo(@Nullable ChickenData type) {
        if (type == null) {
            return "ChickenType:null";
        }
        if (type.name == null) {
            return "ChickenType:chicken:null";
        }
        return "ChickenType:chicken:" + type.name;
    }
}

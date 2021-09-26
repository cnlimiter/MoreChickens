package cn.evolvefield.mods.morechickens.integrations.jei.ingredients;

import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class ChickenIngredientHelper implements IIngredientHelper<ChickenType>
{


    @Nullable
    @Override
    public ChickenType getMatch(Iterable<ChickenType> iterable, ChickenType type) {
        for (ChickenType ingredient : iterable) {
            if (Objects.equals(ingredient.name, type.name)) {
                return ingredient;
            }
        }
        return null;
    }

    @Override
    public String getDisplayName(ChickenType type) {
        ChickenType chickenType = ChickenReloadListener.INSTANCE.getData(type.name);
        if (chickenType != null) {
            return new TranslationTextComponent("text.chickens.name."+ type.name).getString();
        }
        return "ChickenType:chicken:" + type.name;
    }

    @Override
    public String getUniqueId(ChickenType type) {
        return "ChickenType:" + type.name;
    }

    @Override
    public String getModId(ChickenType type) {
        return ModEntities.BASE_CHICKEN.get().getRegistryName().getNamespace();
    }

    @Override
    public String getResourceId(ChickenType type) {
        return ModEntities.BASE_CHICKEN.get().getRegistryName().getPath();
    }

    @Override
    public ChickenType copyIngredient(ChickenType type) {
            return ChickenType.Types.get(type.name);
    }

    @Override
    public String getErrorInfo(@Nullable ChickenType type) {
        if (type == null) {
            return "ChickenType:null";
        }
        if (type.name == null) {
            return "ChickenType:chicken:null";
        }
        return "ChickenType:chicken:" + type.name;
    }
}

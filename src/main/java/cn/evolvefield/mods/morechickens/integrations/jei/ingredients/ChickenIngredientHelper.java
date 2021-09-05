package cn.evolvefield.mods.morechickens.integrations.jei.ingredients;

import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChickenIngredientHelper implements IIngredientHelper<ChickenIngredient>
{
    @Nullable
    @Override
    public ChickenIngredient getMatch(Iterable<ChickenIngredient> iterable, ChickenIngredient ChickenIngredient) {
        for (ChickenIngredient ingredient : iterable) {
            if (ingredient.getChickenType() == ChickenIngredient.getChickenType()) {
                return ingredient;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public String getDisplayName(ChickenIngredient ChickenIngredient) {
        ChickenType chickenType = ChickenReloadListener.INSTANCE.getData(ChickenIngredient.getChickenType().toString());
        if (chickenType != null) {
            return new TranslationTextComponent("text.chickens.name"+ chickenType.name).getString();
        }
        return ChickenIngredient.getChickenEntity().getDescription().getString();
    }

    @Nonnull
    @Override
    public String getUniqueId(ChickenIngredient ChickenIngredient) {
        return "ChickenIngredient:" + ChickenIngredient.getChickenType();
    }

    @Nonnull
    @Override
    public String getWildcardId(@Nonnull ChickenIngredient ChickenIngredient) {
        return getUniqueId(ChickenIngredient);
    }

    @Nonnull
    @Override
    public String getModId(ChickenIngredient ChickenIngredient) {
        return ChickenIngredient.getChickenType().getNamespace();
    }

    @Nonnull
    @Override
    public String getResourceId(ChickenIngredient ChickenIngredient) {
        return ChickenIngredient.getChickenType().getPath();
    }

    @Nonnull
    @Override
    public ChickenIngredient copyIngredient(ChickenIngredient ChickenIngredient) {
        return new ChickenIngredient(ChickenIngredient.getChickenEntity(), ChickenIngredient.getChickenType());
    }

    @Nonnull
    @Override
    public String getErrorInfo(@Nullable ChickenIngredient ChickenIngredient) {
        if (ChickenIngredient == null) {
            return "ChickenIngredient:null";
        }
        if (ChickenIngredient.getChickenEntity() == null) {
            return "ChickenIngredient:chicken:null";
        }
        return "ChickenIngredient:" + ChickenIngredient.getChickenType();
    }
}

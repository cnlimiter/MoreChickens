package cn.evolvefield.mods.morechickens.integrations.jei.ingredients;

import cn.evolvefield.mods.morechickens.client.render.ingredient.ChickenRenderer;
import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChickenIngredientRenderer implements IIngredientRenderer<ChickenIngredient>
{
    @Override
    public void render(@Nonnull MatrixStack matrixStack, int xPosition, int yPosition, @Nullable ChickenIngredient ChickenIngredient) {
        if (ChickenIngredient == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            ChickenRenderer.render(matrixStack, xPosition, yPosition, ChickenIngredient, minecraft);
        }
    }

    @Nonnull
    @Override
    public List<ITextComponent> getTooltip(ChickenIngredient ChickenIngredient, ITooltipFlag iTooltipFlag) {
        List<ITextComponent> list = new ArrayList<>();
        ChickenType chickenType = ChickenReloadListener.INSTANCE.getData(ChickenIngredient.getChickenType().toString());
        if (chickenType != null) {
            list.add(new TranslationTextComponent("text.chickens.name", chickenType.name));
        }
        else {
            list.add(ChickenIngredient.getChickenEntity().getDescription());
        }
        list.add(new StringTextComponent(ChickenIngredient.getChickenType().toString()).withStyle(TextFormatting.DARK_GRAY));
        return list;
    }
}

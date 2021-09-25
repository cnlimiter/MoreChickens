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

public class ChickenIngredientRenderer implements IIngredientRenderer<ChickenType>
{

    @Override
    public void render(MatrixStack matrixStack, int xPosition, int yPosition, @Nullable ChickenType type) {
        if (type == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            ChickenRenderer.render(matrixStack, xPosition, yPosition, type, minecraft);
        }
    }

    @Override
    public List<ITextComponent> getTooltip(ChickenType type, ITooltipFlag iTooltipFlag) {
        List<ITextComponent> list = new ArrayList<>();
        ChickenType chickenType = ChickenReloadListener.INSTANCE.getData(type.name);
        if (chickenType != null) {
            list.add(new TranslationTextComponent("text.chickens.name." + type.name));
        }
        list.add(new TranslationTextComponent("text.chickens.name." + type.name).withStyle(TextFormatting.DARK_GRAY));
        return list;
    }
}

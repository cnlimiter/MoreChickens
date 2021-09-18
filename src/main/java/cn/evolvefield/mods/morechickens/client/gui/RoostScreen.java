package cn.evolvefield.mods.morechickens.client.gui;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.client.gui.base.ScreenBase;
import cn.evolvefield.mods.morechickens.common.container.BreederContainer;
import cn.evolvefield.mods.morechickens.common.container.RoostContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class RoostScreen extends ScreenBase<RoostContainer> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MoreChickens.MODID, "textures/gui/container/roost.png");

    private PlayerInventory playerInventory;

    public RoostScreen(RoostContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        imageWidth = 176;
        imageHeight = 164;
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        drawString(matrixStack,font,new TranslationTextComponent("container.chickens.roost"),4,4,FONT_COLOR);
        int x = getGuiLeft();
        int y = (height - getYSize()) / 2;

        if (mouseX > x + 69 && mouseX < x + 95 && mouseY > y + 31 && mouseY < y + 46) {
            List<IReorderingProcessor> tooltip = new ArrayList<IReorderingProcessor>();;
            tooltip.add(new StringTextComponent(this.menu.getFormattedProgress()).getVisualOrderText());
            renderTooltip(matrixStack, tooltip, mouseX - x, mouseY - y);
        }

    }


    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        int x = getGuiLeft();
        int y = (height - getYSize()) / 2;




        GuiUtils.drawTexturedModalRect(matrixStack,x, y, 0, 0, getXSize(), getYSize(),100);
        GuiUtils.drawTexturedModalRect(matrixStack,x + 69, y + 31, 176, 0, getProgressWidth(), 12,100);
    }

    private int getProgressWidth() {
        double progress = this.menu.getProgress();
        return progress == 0.0D ? 0 : 1 + (int) (progress * 25);
    }
}

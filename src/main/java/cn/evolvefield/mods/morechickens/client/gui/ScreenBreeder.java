package cn.evolvefield.mods.morechickens.client.gui;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.container.BreederContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class ScreenBreeder extends ContainerScreen<BreederContainer> {

    private static final ResourceLocation BREEDER_GUI_TEXTURES = new ResourceLocation(MoreChickens.MODID, "textures/gui/breeder.png");


    public ScreenBreeder(BreederContainer container, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(container,playerInventory,textComponent);
        this.imageWidth = 176;
        this.imageHeight = 133;
    }


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }


    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack,this.menu.tileBreeder.getDisplayName().getString(), 8, 6, 4210752);
        font.draw(matrixStack, new TranslationTextComponent("container.inventory"), 8, this.getYSize() - 96 + 2, 4210752);
        int x = getGuiLeft();
        int y = (height - getYSize()) / 2;

        if (mouseX > x + 84 && mouseX < x + 110 && mouseY > y + 22 && mouseY < y + 34) {
            List<IReorderingProcessor> tooltip = new ArrayList<IReorderingProcessor>();;
            tooltip.add(new StringTextComponent(this.menu.tileBreeder.getFormattedProgress()).getVisualOrderText());
            renderTooltip(matrixStack, tooltip, mouseX - x, mouseY - y);
        }

    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(BREEDER_GUI_TEXTURES);
        int x = getGuiLeft();
        int y = (height - getYSize()) / 2;

        GuiUtils.drawTexturedModalRect(matrixStack,x, y, 0, 0, getXSize(), getYSize(),100);
        GuiUtils.drawTexturedModalRect(matrixStack,x + 84, y + 22, 176, 0, getProgressWidth(), 12,100);

    }

    private int getProgressWidth() {
        double progress = this.menu.tileBreeder.getProgress();
        return progress == 0.0D ? 0 : 1 + (int) (progress * 25);
    }
}

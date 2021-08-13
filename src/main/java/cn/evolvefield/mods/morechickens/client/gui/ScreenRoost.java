package cn.evolvefield.mods.morechickens.client.gui;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.container.RoostContainer;
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

public class ScreenRoost extends ContainerScreen<RoostContainer> {

    public ScreenRoost(RoostContainer container, PlayerInventory inventory, ITextComponent textComponent) {
        super(container,inventory,textComponent);
        this.imageWidth = 176;
        this.imageHeight = 133;
    }




    private static final ResourceLocation ROOST_GUI_TEXTURES = new ResourceLocation(MoreChickens.MODID, "textures/gui/roost.png");



    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }


    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack,this.menu.tileRoost.getDisplayName().getString(), 8, 6, 4210752);
        font.draw(matrixStack, new TranslationTextComponent("container.inventory"), 8, this.getYSize() - 96 + 2, 4210752);
        int x = getGuiLeft();
        int y = (height - getYSize()) / 2;

        if (mouseX > x + 48 && mouseX < x + 74 && mouseY > y + 20 && mouseY < y + 36) {
            List<IReorderingProcessor> tooltip = new ArrayList<IReorderingProcessor>();;
            tooltip.add(new StringTextComponent(this.menu.tileRoost.getFormattedProgress()).getVisualOrderText());
            renderTooltip(matrixStack, tooltip, mouseX - x, mouseY - y);
        }

    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(ROOST_GUI_TEXTURES);
        int x = getGuiLeft();
        int y = (height - getYSize()) / 2;

        GuiUtils.drawTexturedModalRect(matrixStack,x, y, 0, 0, getXSize(), getYSize(),100);
        GuiUtils.drawTexturedModalRect(matrixStack,x + 48, y + 20, 176, 0, getProgressWidth(), 16,100);

    }

    private int getProgressWidth() {
        double progress = this.menu.tileRoost.getProgress();
        return progress == 0.0D ? 0 : 1 + (int) (progress * 25);
    }
}

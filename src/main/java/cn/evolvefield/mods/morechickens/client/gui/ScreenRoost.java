package cn.evolvefield.mods.morechickens.client.gui;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.container.ContainerRoost;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fmlclient.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class ScreenRoost extends AbstractContainerScreen<ContainerRoost> {

    public ScreenRoost(ContainerRoost container, Inventory inventory, Component textComponent) {
        super(container,inventory,textComponent);
        this.imageWidth = 176;
        this.imageHeight = 133;

    }




    private static final ResourceLocation ROOST_GUI_TEXTURES = new ResourceLocation(MoreChickens.MODID, "textures/gui/roost.png");



    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack,mouseX,mouseY);
    }


    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack,this.menu.tileRoost.getDisplayName().getString(), 8, 6, 4210752);
        font.draw(matrixStack, new TranslatableComponent("container.inventory"), 8, this.getYSize() - 96 + 2, 4210752);
        int x = getGuiLeft();
        int y = (height - getYSize()) / 2;

        if (mouseX > x + 48 && mouseX < x + 74 && mouseY > y + 20 && mouseY < y + 36) {
            List<FormattedCharSequence> tooltip = new ArrayList<>();;
            tooltip.add(new TextComponent(this.menu.tileRoost.getFormattedProgress()).getVisualOrderText());
            renderTooltip(matrixStack, tooltip, mouseX - x, mouseY - y);
        }

    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindForSetup(ROOST_GUI_TEXTURES);
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

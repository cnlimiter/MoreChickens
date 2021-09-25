package cn.evolvefield.mods.morechickens.client.gui;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.client.gui.base.ScreenBase;
import cn.evolvefield.mods.morechickens.common.container.BreederContainer;

import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fmlclient.gui.GuiUtils;


import java.util.ArrayList;
import java.util.List;

public class BreederScreen extends ScreenBase<BreederContainer> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MoreChickens.MODID, "textures/gui/container/breeder.png");

    private Inventory playerInventory;

    public BreederScreen(BreederContainer container, Inventory playerInventory, Component name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        imageWidth = 176;
        imageHeight = 164;
    }

    @Override
    protected void renderLabels(PoseStack PoseStack, int mouseX, int mouseY) {
        drawString(PoseStack,font,new TranslatableComponent("container.chickens.breeder"),4,4,FONT_COLOR);
        int x = getGuiLeft();
        int y = (height - getYSize()) / 2;

        if (mouseX > x + 81 && mouseX < x + 107 && mouseY > y + 34 && mouseY < y + 46) {
            List<FormattedCharSequence> tooltip = new ArrayList<FormattedCharSequence>();;
            tooltip.add(new TextComponent(this.menu.getFormattedProgress()).getVisualOrderText());
            renderTooltip(PoseStack, tooltip, mouseX - x, mouseY - y);
        }
        if ((mouseX > x + 19 && mouseX < x + 37 && mouseY > y + 25 && mouseY < y + 43) && this.menu.breeder.hasChicken1()) {
            List<FormattedCharSequence> tooltip = new ArrayList<FormattedCharSequence>();
            tooltip.add(new TranslatableComponent("text.chickens.name."+((BaseChickenEntity)this.menu.breeder.getChickenEntity1()).getChickenName()).getVisualOrderText());
            renderTooltip(PoseStack, tooltip, mouseX - x, mouseY - y);
        }

        if ((mouseX > x + 19 && mouseX < x + 37 && mouseY > y + 46 && mouseY < y + 64) && this.menu.breeder.hasChicken2()) {
            List<FormattedCharSequence> tooltip = new ArrayList<FormattedCharSequence>();
            tooltip.add(new TranslatableComponent("text.chickens.name."+((BaseChickenEntity)this.menu.breeder.getChickenEntity2()).getChickenName()).getVisualOrderText());
            renderTooltip(PoseStack, tooltip, mouseX - x, mouseY - y);
        }
    }


    @Override
    protected void renderBg(PoseStack PoseStack, float partialTicks, int mouseX, int mouseY) {
        int x = getGuiLeft();
        int y = (height - getYSize()) / 2;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blit(PoseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        GuiUtils.drawTexturedModalRect(PoseStack,x, y, 0, 0, getXSize(), getYSize(),100);
        GuiUtils.drawTexturedModalRect(PoseStack,x + 81, y + 34, 176, 0, getProgressWidth(), 12,100);

        if (this.menu.breeder.hasChicken1()){
            this.itemRenderer.renderAndDecorateItem(this.menu.breeder.getChicken1(),x + 19 , y + 25 ,18, 18);
        }
        if (this.menu.breeder.hasChicken2()){
            this.itemRenderer.renderAndDecorateItem(this.menu.breeder.getChicken2(),x + 19 , y + 46 ,18, 18);
        }
    }

    private int getProgressWidth() {
        double progress = this.menu.getProgress();
        return progress == 0.0D ? 0 : 1 + (int) (progress * 25);
    }
}

package cn.evolvefield.mods.morechickens.client.gui;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.client.gui.base.ScreenBase;
import cn.evolvefield.mods.morechickens.common.container.RoostContainer;

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


public class RoostScreen extends ScreenBase<RoostContainer> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MoreChickens.MODID, "textures/gui/container/roost.png");

    private Inventory playerInventory;

    public RoostScreen(RoostContainer container, Inventory playerInventory, Component name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        imageWidth = 176;
        imageHeight = 164;
    }

    @Override
    protected void renderLabels(PoseStack PoseStack, int mouseX, int mouseY) {
        drawString(PoseStack,font,new TranslatableComponent("container.chickens.roost"),4,4,FONT_COLOR);
        int x = getGuiLeft();
        int y = (height - getYSize()) / 2;

//        if (this.menu.tileRoost.hasChickenItem()){
//            ChickenRenderer.render(PoseStack, this.menu.tileRoost.getChickenEntity(),26,28,minecraft);
//        }

        if (mouseX > x + 69 && mouseX < x + 95 && mouseY > y + 31 && mouseY < y + 46) {
            List<FormattedCharSequence> tooltip = new ArrayList<FormattedCharSequence>();;
            tooltip.add(new TextComponent(this.menu.getFormattedProgress()).getVisualOrderText());
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
        GuiUtils.drawTexturedModalRect(PoseStack,x + 69, y + 31, 176, 0, getProgressWidth(), 12,100);
    }

    private int getProgressWidth() {
        double progress = this.menu.getProgress();
        return progress == 0.0D ? 0 : 1 + (int) (progress * 25);
    }
}

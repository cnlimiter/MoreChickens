package cn.evolvefield.mods.morechickens.client.gui.base;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class ScreenBase<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    public static final int FONT_COLOR = 4210752;
    protected ResourceLocation texture;
    protected List<HoverArea> hoverAreas;

    public ScreenBase(ResourceLocation texture, T container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.texture = texture;
        this.hoverAreas = new ArrayList<>();
    }

    @Override
    public void render(PoseStack matrixStack, int x, int y, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);
        renderTooltip(matrixStack, x, y);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, texture);

        blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {

    }

    public void drawHoverAreas(PoseStack matrixStack, int mouseX, int mouseY) {
        for (HoverArea hoverArea : hoverAreas) {
            if (hoverArea.tooltip != null && hoverArea.isHovered(leftPos, topPos, mouseX, mouseY)) {
                renderTooltip(matrixStack, hoverArea.tooltip.get(), mouseX - leftPos, mouseY - topPos);
            }
        }
    }

    public int getBlitSize(int amount, int max, int size) {
        return size - (int) (((float) amount / (float) max) * (float) size);
    }

    public void drawCentered(PoseStack matrixStack, Component text, int y, int color) {
        drawCentered(matrixStack, text, imageWidth / 2, y, color);
    }

    public void drawCentered(PoseStack matrixStack, Component text, int x, int y, int color) {
        drawCentered(font, matrixStack, text, x, y, color);
    }

    public static void drawCentered(Font font, PoseStack matrixStack, Component text, int x, int y, int color) {
        int width = font.width(text);
        font.draw(matrixStack, text, x - width / 2, y, color);
    }

    public static class HoverArea {
        private final int posX, posY;
        private final int width, height;
        @Nullable
        private final Supplier<List<FormattedCharSequence>> tooltip;

        public HoverArea(int posX, int posY, int width, int height) {
            this(posX, posY, width, height, null);
        }

        public HoverArea(int posX, int posY, int width, int height, Supplier<List<FormattedCharSequence>> tooltip) {
            this.posX = posX;
            this.posY = posY;
            this.width = width;
            this.height = height;
            this.tooltip = tooltip;
        }

        public int getPosX() {
            return posX;
        }

        public int getPosY() {
            return posY;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @Nullable
        public Supplier<List<FormattedCharSequence>> getTooltip() {
            return tooltip;
        }

        public boolean isHovered(int guiLeft, int guiTop, int mouseX, int mouseY) {
            if (mouseX >= guiLeft + posX && mouseX < guiLeft + posX + width) {
                if (mouseY >= guiTop + posY && mouseY < guiTop + posY + height) {
                    return true;
                }
            }
            return false;
        }
    }

}

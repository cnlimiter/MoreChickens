package cn.evolvefield.mods.morechickens.client.gui.base;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
        this.hoverAreas = new ArrayList();
    }

    public void render(PoseStack PoseStack, int x, int y, float partialTicks) {
        this.renderBackground(PoseStack);
        super.render(PoseStack, x, y, partialTicks);
        this.renderTooltip(PoseStack, x, y);
    }

    protected void renderBg(PoseStack PoseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindForSetup(this.texture);
        this.blit(PoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    protected void renderLabels(PoseStack PoseStack, int mouseX, int mouseY) {
    }

    public void drawHoverAreas(PoseStack PoseStack, int mouseX, int mouseY) {
        Iterator var4 = this.hoverAreas.iterator();

        while(var4.hasNext()) {
            HoverArea hoverArea = (HoverArea)var4.next();
            if (hoverArea.tooltip != null && hoverArea.isHovered(this.leftPos, this.topPos, mouseX, mouseY)) {
                this.renderTooltip(PoseStack, (List)hoverArea.tooltip.get(), mouseX - this.leftPos, mouseY - this.topPos);
            }
        }

    }

    public int getBlitSize(int amount, int max, int size) {
        return size - (int)((float)amount / (float)max * (float)size);
    }

    public void drawCentered(PoseStack PoseStack, Component text, int y, int color) {
        this.drawCentered(PoseStack, text, this.imageWidth / 2, y, color);
    }

    public void drawCentered(PoseStack PoseStack, Component text, int x, int y, int color) {
        drawCentered(this.font, PoseStack, text, x, y, color);
    }

    public static void drawCentered(Font font, PoseStack PoseStack, Component text, int x, int y, int color) {
        int width = font.width(text);
        font.draw(PoseStack, text, (float)(x - width / 2), (float)y, color);
    }

    public static class HoverArea {
        private final int posX;
        private final int posY;
        private final int width;
        private final int height;
        @Nullable
        private final Supplier<List<FormattedCharSequence>> tooltip;

        public HoverArea(int posX, int posY, int width, int height) {
            this(posX, posY, width, height, (Supplier)null);
        }

        public HoverArea(int posX, int posY, int width, int height, Supplier<List<FormattedCharSequence>> tooltip) {
            this.posX = posX;
            this.posY = posY;
            this.width = width;
            this.height = height;
            this.tooltip = tooltip;
        }

        public int getPosX() {
            return this.posX;
        }

        public int getPosY() {
            return this.posY;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        @Nullable
        public Supplier<List<FormattedCharSequence>> getTooltip() {
            return this.tooltip;
        }

        public boolean isHovered(int guiLeft, int guiTop, int mouseX, int mouseY) {
            return mouseX >= guiLeft + this.posX && mouseX < guiLeft + this.posX + this.width && mouseY >= guiTop + this.posY && mouseY < guiTop + this.posY + this.height;
        }
    }
}

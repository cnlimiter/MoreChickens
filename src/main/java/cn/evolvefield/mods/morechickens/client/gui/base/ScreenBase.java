package cn.evolvefield.mods.morechickens.client.gui.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class ScreenBase<T extends Container> extends ContainerScreen<T> {
    public static final int FONT_COLOR = 4210752;
    protected ResourceLocation texture;
    protected List<HoverArea> hoverAreas;

    public ScreenBase(ResourceLocation texture, T container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.texture = texture;
        this.hoverAreas = new ArrayList();
    }

    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);
        this.renderTooltip(matrixStack, x, y);
    }

    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(this.texture);
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
    }

    public void drawHoverAreas(MatrixStack matrixStack, int mouseX, int mouseY) {
        Iterator var4 = this.hoverAreas.iterator();

        while(var4.hasNext()) {
            ScreenBase.HoverArea hoverArea = (ScreenBase.HoverArea)var4.next();
            if (hoverArea.tooltip != null && hoverArea.isHovered(this.leftPos, this.topPos, mouseX, mouseY)) {
                this.renderTooltip(matrixStack, (List)hoverArea.tooltip.get(), mouseX - this.leftPos, mouseY - this.topPos);
            }
        }

    }

    public int getBlitSize(int amount, int max, int size) {
        return size - (int)((float)amount / (float)max * (float)size);
    }

    public void drawCentered(MatrixStack matrixStack, ITextComponent text, int y, int color) {
        this.drawCentered(matrixStack, text, this.imageWidth / 2, y, color);
    }

    public void drawCentered(MatrixStack matrixStack, ITextComponent text, int x, int y, int color) {
        drawCentered(this.font, matrixStack, text, x, y, color);
    }

    public static void drawCentered(FontRenderer font, MatrixStack matrixStack, ITextComponent text, int x, int y, int color) {
        int width = font.width(text);
        font.draw(matrixStack, text, (float)(x - width / 2), (float)y, color);
    }

    public static class HoverArea {
        private final int posX;
        private final int posY;
        private final int width;
        private final int height;
        @Nullable
        private final Supplier<List<IReorderingProcessor>> tooltip;

        public HoverArea(int posX, int posY, int width, int height) {
            this(posX, posY, width, height, (Supplier)null);
        }

        public HoverArea(int posX, int posY, int width, int height, Supplier<List<IReorderingProcessor>> tooltip) {
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
        public Supplier<List<IReorderingProcessor>> getTooltip() {
            return this.tooltip;
        }

        public boolean isHovered(int guiLeft, int guiTop, int mouseX, int mouseY) {
            return mouseX >= guiLeft + this.posX && mouseX < guiLeft + this.posX + this.width && mouseY >= guiTop + this.posY && mouseY < guiTop + this.posY + this.height;
        }
    }
}

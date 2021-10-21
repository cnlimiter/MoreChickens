package cn.evolvefield.mods.morechickens.client.gui;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.client.gui.base.ScreenBase;
import cn.evolvefield.mods.morechickens.client.render.ingredient.ChickenRenderer;
import cn.evolvefield.mods.morechickens.common.container.BreederContainer;
import cn.evolvefield.mods.morechickens.common.data.ChickenRegistry;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
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

public class BreederScreen extends ScreenBase<BreederContainer> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MoreChickens.MODID, "textures/gui/container/breeder.png");

    private PlayerInventory playerInventory;

    public BreederScreen(BreederContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        imageWidth = 176;
        imageHeight = 164;
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        drawString(matrixStack,font,new TranslationTextComponent("container.chickens.breeder"),4,4, FONT_COLOR);
        int x = getGuiLeft();
        int y = (height - getYSize()) / 2;

        if (mouseX > x + 81 && mouseX < x + 107 && mouseY > y + 34 && mouseY < y + 46) {
            List<IReorderingProcessor> tooltip = new ArrayList<IReorderingProcessor>();;
            tooltip.add(new StringTextComponent(this.menu.getFormattedProgress()).getVisualOrderText());
            renderTooltip(matrixStack, tooltip, mouseX - x, mouseY - y);
        }
        if ((mouseX > x + 19 && mouseX < x + 37 && mouseY > y + 25 && mouseY < y + 43) && this.menu.breeder.hasChicken1()) {
            List<IReorderingProcessor> tooltip = new ArrayList<IReorderingProcessor>();
            tooltip.add(new TranslationTextComponent("text.chickens.name."+((BaseChickenEntity)this.menu.breeder.getChickenEntity1()).getChickenName()).getVisualOrderText());
            renderTooltip(matrixStack, tooltip, mouseX - x, mouseY - y);
        }

        if ((mouseX > x + 19 && mouseX < x + 37 && mouseY > y + 46 && mouseY < y + 64) && this.menu.breeder.hasChicken2()) {
            List<IReorderingProcessor> tooltip = new ArrayList<IReorderingProcessor>();
            tooltip.add(new TranslationTextComponent("text.chickens.name."+((BaseChickenEntity)this.menu.breeder.getChickenEntity2()).getChickenName()).getVisualOrderText());
            renderTooltip(matrixStack, tooltip, mouseX - x, mouseY - y);
        }

    }


    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        int x = getGuiLeft();
        int y = (height - getYSize()) / 2;

        GuiUtils.drawTexturedModalRect(matrixStack,x, y, 0, 0, getXSize(), getYSize(),100);
        GuiUtils.drawTexturedModalRect(matrixStack,x + 81, y + 34, 176, 0, getProgressWidth(), 12,100);
        if (this.menu.breeder.hasChicken1()){
            ChickenRenderer.render(matrixStack,x + 19,y + 25, ChickenRegistry.Types.get(this.menu.breeder.getChicken1Name()),minecraft);
            //this.itemRenderer.renderAndDecorateItem(this.menu.breeder.getChicken1(),x + 19 , y + 25 );
        }
        if (this.menu.breeder.hasChicken2()){
            ChickenRenderer.render(matrixStack,x + 19,y + 46, ChickenRegistry.Types.get(this.menu.breeder.getChicken2Name()),minecraft);
            //this.itemRenderer.renderAndDecorateItem(this.menu.breeder.getChicken2(),x + 19 , y + 46 );
        }
    }

    private int getProgressWidth() {
        double progress = this.menu.getProgress();
        return progress == 0.0D ? 0 : 1 + (int) (progress * 25);
    }
}

package cn.evolvefield.mods.morechickens.client.render.tile;

import cn.evolvefield.mods.morechickens.common.tile.base.FakeWorldTileEntity;
import cn.evolvefield.mods.morechickens.common.util.render.RenderUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.client.model.data.EmptyModelData;

public class BlockRendererBase<T extends FakeWorldTileEntity> extends TileEntityRenderer<T> {

    protected Minecraft minecraft;

    public BlockRendererBase(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void render(T tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        renderBlock(tileEntity, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
    }

    protected void renderBlock(T tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        BlockState state = tileEntity.getBlockState();
        int color = minecraft.getBlockColors().getColor(state, null, null, 0);
        BlockRendererDispatcher dispatcher = minecraft.getBlockRenderer();
        dispatcher.getModelRenderer().renderModel(matrixStack.last(), buffer.getBuffer(RenderType.cutoutMipped()), state, dispatcher.getBlockModel(state), RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
    }

}

package cn.evolvefield.mods.morechickens.client.render.tile;

import cn.evolvefield.mods.morechickens.client.render.entity.BaseChickenEntityRender;
import cn.evolvefield.mods.morechickens.common.tile.base.FakeWorldTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class RendererBase<T extends FakeWorldTileEntity> extends BlockRendererBase<T> {

    protected BaseChickenEntityRender baseChickenEntityRender;

    public RendererBase(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(T tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if (baseChickenEntityRender == null) {
            baseChickenEntityRender = new BaseChickenEntityRender(minecraft.getEntityRenderDispatcher());
        }
        super.render(tileEntity, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
    }

}

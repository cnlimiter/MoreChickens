package cn.evolvefield.mods.morechickens.client.render.tile;

import cn.evolvefield.mods.morechickens.client.render.entity.BaseChickenEntityRender;
import cn.evolvefield.mods.morechickens.common.tile.base.FakeWorldTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RendererBase<T extends FakeWorldTileEntity> extends BlockRendererBase<T> {

    protected BaseChickenEntityRender baseChickenEntityRender;

    public RendererBase(BlockEntityRendererProvider.Context renderer) {
        super(renderer);
    }

    @Override
    public void render(T tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (baseChickenEntityRender == null) {
            baseChickenEntityRender = new BaseChickenEntityRender(getEntityRenderer());
        }
        super.render(tileEntity, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
    }

}

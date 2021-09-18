package cn.evolvefield.mods.morechickens.client.render.tile;

import cn.evolvefield.mods.morechickens.common.block.base.HorizontalRotatableBlock;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class RoostRenderer extends RendererBase<RoostTileEntity> {

    public RoostRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(RoostTileEntity roost, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        super.render(roost, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
        matrixStack.pushPose();
        BaseChickenEntity chicken = (BaseChickenEntity) roost.getChickenEntity();

        Direction direction = Direction.SOUTH;
        if (!roost.isFakeWorld()) {
            direction = roost.getBlockState().getValue(HorizontalRotatableBlock.FACING);
        }

        if (roost.getChickenEntity() != null) {
            matrixStack.pushPose();
            matrixStack.translate(0.5D, 1D / 16D, 0.5D);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(direction.toYRot()));
            matrixStack.translate(-5D / 16D, 0D, 0D);
            matrixStack.scale(0.45F, 0.45F, 0.45F);
            baseChickenEntityRender.render(chicken, 0F, 1F, matrixStack, buffer, combinedLight);
            matrixStack.popPose();
        }




        matrixStack.popPose();
    }
}

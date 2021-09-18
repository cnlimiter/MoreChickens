package cn.evolvefield.mods.morechickens.client.render.tile;

import cn.evolvefield.mods.morechickens.common.block.base.HorizontalRotatableBlock;
import cn.evolvefield.mods.morechickens.common.tile.BreederTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.BedTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class BreederRenderer extends RendererBase<BreederTileEntity> {


    public BreederRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(BreederTileEntity breeder, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        super.render(breeder, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
        matrixStack.pushPose();



        Direction direction = Direction.SOUTH;
        if (!breeder.isFakeWorld()) {
            direction = breeder.getBlockState().getValue(HorizontalRotatableBlock.FACING);
        }

        if (breeder.getChickenEntity1() != null) {
            matrixStack.pushPose();
            matrixStack.translate(0.5D, 1D / 16D, 0.5D);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-direction.toYRot()));
            matrixStack.translate(-5D / 16D, 0D, 0D);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
            matrixStack.scale(0.45F, 0.45F, 0.45F);
            baseChickenEntityRender.render(breeder.getChickenEntity1(), 0F, 1F, matrixStack, buffer, combinedLight);
            matrixStack.popPose();
        }

        if (breeder.getChickenEntity2() != null) {
            matrixStack.pushPose();

            matrixStack.translate(0.5D, 1D / 16D, 0.5D);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-direction.toYRot()));
            matrixStack.translate(5D / 16D, 0D, 0D);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90));
            matrixStack.scale(0.45F, 0.45F, 0.45F);
            baseChickenEntityRender.render(breeder.getChickenEntity2(), 0F, 1F, matrixStack, buffer, combinedLight);
            matrixStack.popPose();
        }

//        matrixStack.pushPose();
//        matrixStack.translate(0.5D, 1D / 16D, 0.5D);
//        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-direction.toYRot()));
//        matrixStack.translate(0D, 0D, 3D / 16D);
//        matrixStack.translate(-0.5D, 0D, -0.5D);
//        matrixStack.scale(0.4F, 0.4F, 0.4F);
//        matrixStack.translate(0.5D / 0.4D - 0.5D, 0D, 0.5D / 0.4D - 0.5D);
//        matrixStack.popPose();

        matrixStack.popPose();
    }

}

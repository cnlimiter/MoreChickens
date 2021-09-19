package cn.evolvefield.mods.morechickens.client.render.tile;

import cn.evolvefield.mods.morechickens.common.block.base.HorizontalRotatableBlock;
import cn.evolvefield.mods.morechickens.common.tile.BreederTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;


public class BreederRenderer extends RendererBase<BreederTileEntity> {


    public BreederRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(BreederTileEntity breeder, float partialTicks, PoseStack PoseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        super.render(breeder, partialTicks, PoseStack, buffer, combinedLight, combinedOverlay);
        PoseStack.pushPose();



        Direction direction = Direction.SOUTH;
        if (!breeder.isFakeWorld()) {
            direction = breeder.getBlockState().getValue(HorizontalRotatableBlock.FACING);
        }

        if (breeder.getChickenEntity1() != null) {
            PoseStack.pushPose();
            PoseStack.translate(0.5D, 1D / 16D, 0.5D);
            PoseStack.mulPose(Vector3f.YP.rotationDegrees(-direction.toYRot()));
            PoseStack.translate(-5D / 16D, 0D, 0D);
            PoseStack.mulPose(Vector3f.YP.rotationDegrees(90));
            PoseStack.scale(0.45F, 0.45F, 0.45F);
            baseChickenEntityRender.render(breeder.getChickenEntity1(), 0F, 1F, PoseStack, buffer, combinedLight);
            PoseStack.popPose();
        }

        if (breeder.getChickenEntity2() != null) {
            PoseStack.pushPose();

            PoseStack.translate(0.5D, 1D / 16D, 0.5D);
            PoseStack.mulPose(Vector3f.YP.rotationDegrees(-direction.toYRot()));
            PoseStack.translate(5D / 16D, 0D, 0D);
            PoseStack.mulPose(Vector3f.YP.rotationDegrees(-90));
            PoseStack.scale(0.45F, 0.45F, 0.45F);
            baseChickenEntityRender.render(breeder.getChickenEntity2(), 0F, 1F, PoseStack, buffer, combinedLight);
            PoseStack.popPose();
        }


        PoseStack.popPose();
    }

}

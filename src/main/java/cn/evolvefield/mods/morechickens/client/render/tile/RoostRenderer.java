package cn.evolvefield.mods.morechickens.client.render.tile;

import cn.evolvefield.mods.morechickens.common.block.base.HorizontalRotatableBlock;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;


public class RoostRenderer extends RendererBase<RoostTileEntity> {

    public RoostRenderer(BlockEntityRendererProvider.Context renderer) {
        super(renderer);
    }

    @Override
    public void render(RoostTileEntity roost, float partialTicks, PoseStack PoseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        super.render(roost, partialTicks, PoseStack, buffer, combinedLight, combinedOverlay);
        PoseStack.pushPose();
        BaseChickenEntity chicken = (BaseChickenEntity) roost.getChickenEntity();

        Direction direction = Direction.SOUTH;
        if (!roost.isFakeWorld()) {
            direction = roost.getBlockState().getValue(HorizontalRotatableBlock.FACING);
        }

        if (roost.getChickenEntity() != null) {
            PoseStack.pushPose();
            PoseStack.translate(0.4D, 1D / 16D, 0D);
            PoseStack.mulPose(Vector3f.YP.rotationDegrees(direction.toYRot()));
            PoseStack.translate(-5D / 16D, 0D, 0D);
            PoseStack.mulPose(Vector3f.YP.rotationDegrees(0));
            PoseStack.scale(0.8F, 0.8F, 0.8F);
            baseChickenEntityRender.render(chicken, 0F, 1F, PoseStack, buffer, combinedLight);
            PoseStack.popPose();
        }
        PoseStack.popPose();
    }
}

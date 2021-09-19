package cn.evolvefield.mods.morechickens.client.render.tile;

import cn.evolvefield.mods.morechickens.common.block.utils.BaitType;
import cn.evolvefield.mods.morechickens.common.tile.BaitTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;


public class BaitRenderer implements BlockEntityRenderer<BaitTileEntity> {
    public BaitRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BaitTileEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BaitType baitType = tileEntity.getBaitType();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        matrixStack.pushPose();
        matrixStack.translate(0.45, 0.05f, 0.45);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(new Quaternion(90f, 0f, 0f, true));
        if (!baitType.getDisplayItemFirst().isEmpty()) {
            itemRenderer.renderStatic(baitType.getDisplayItemFirst(), ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStack, bufferIn,1);
        }
        matrixStack.translate(0.1f, 0f, -0.05f);
        matrixStack.mulPose(new Quaternion(5f, 0f, 0f, true));
        if (!baitType.getDisplayItemSecond().isEmpty()) {
            itemRenderer.renderStatic(baitType.getDisplayItemSecond(), ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStack, bufferIn,1);
        }
        matrixStack.popPose();
    }

}

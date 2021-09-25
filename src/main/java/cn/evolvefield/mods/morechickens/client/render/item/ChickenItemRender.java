package cn.evolvefield.mods.morechickens.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class ChickenItemRender extends BlockEntityWithoutLevelRenderer {
    public ChickenItemRender(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType type, PoseStack poseStack, MultiBufferSource source, int p_108834_, int p_108835_) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel ibakedmodel = itemRenderer.getModel(stack, null, null,0);
//        poseStack.push();
//        poseStack.translate(0.5F, 0.5F, 0.5F);
//        float xOffset = -1 / 32f;
//        float zOffset = 0;
//        poseStack.translate(-xOffset, 0, -zOffset);
//        poseStack.rotate(Vector3f.YP.rotationDegrees(degree));
//        poseStack.translate(xOffset, 0, zOffset);
//
//        itemRenderer.render(stack, ItemTransforms.TransformType.NONE, false, matrixStack, buffer, combinedLight, combinedOverlay, ibakedmodel.getBakedModel());
//        poseStack.pop();
    }
}

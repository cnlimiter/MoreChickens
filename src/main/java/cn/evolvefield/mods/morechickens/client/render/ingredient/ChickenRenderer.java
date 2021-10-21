package cn.evolvefield.mods.morechickens.client.render.ingredient;

import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class ChickenRenderer {
    private ChickenRenderer() {
    }

    public static void render(MatrixStack matrixStack, int xPosition, int yPosition, ChickenData chickenData, Minecraft minecraft) {
        BaseChickenEntity chicken = ModEntities.BASE_CHICKEN.get().create(minecraft.level);

            if (minecraft.player != null && chicken != null) {
                chicken.setType(chickenData);
                chicken.tickCount = minecraft.player.tickCount;
                chicken.yBodyRot = -20;

                float scaledSize = 18;

                matrixStack.pushPose();
                matrixStack.translate(7D + xPosition, 12D + yPosition, 1.5);
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(190.0F));
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(20.0F));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(20.0F));
                matrixStack.translate(0.0F, -0.2F, 1);
                matrixStack.scale(scaledSize, scaledSize, scaledSize);

                EntityRendererManager entityrenderermanager = minecraft.getEntityRenderDispatcher();
                IRenderTypeBuffer.Impl buffer = minecraft.renderBuffers().bufferSource();
                entityrenderermanager.render(chicken, 0, 0, 0.0D, minecraft.getFrameTime(), 1, matrixStack, buffer, 15728880);
                buffer.endBatch();
                matrixStack.popPose();
        }

    }

    public static void renderEntity(MatrixStack matrixStack, Entity entity, World world, float x, float y, float rotation, float renderScale) {
        if (world == null) return;
        float scaledSize;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) entity.tickCount = mc.player.tickCount;
        scaledSize = 20 / (Math.max(entity.getBbWidth(), entity.getBbHeight()));
        if (mc.player != null) {
            matrixStack.pushPose();
            matrixStack.translate(10, 20 * renderScale, 0.5);
            matrixStack.translate(x, y, 1);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            matrixStack.translate(0, 0, 100);
            matrixStack.scale(-(scaledSize * renderScale), (scaledSize * renderScale), 30);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(rotation));
            EntityRendererManager entityrenderermanager = mc.getEntityRenderDispatcher();
            IRenderTypeBuffer.Impl renderTypeBuffer = mc.renderBuffers().bufferSource();
            entityrenderermanager.render(entity, 0, 0, 0.0D, mc.getFrameTime(), 1, matrixStack, renderTypeBuffer, 15728880);
            renderTypeBuffer.endBatch();
        }
        matrixStack.popPose();
    }

}

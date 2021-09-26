package cn.evolvefield.mods.morechickens.client.render.ingredient;

import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.math.vector.Vector3f;

public class ChickenRenderer {
    private ChickenRenderer() {
    }

    public static void render(MatrixStack matrixStack, int xPosition, int yPosition, ChickenType chickenType, Minecraft minecraft) {
        BaseChickenEntity chicken = ModEntities.BASE_CHICKEN.get().create(minecraft.level);

            if (minecraft.player != null && chicken != null) {
                chicken.setType(chickenType);
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

}

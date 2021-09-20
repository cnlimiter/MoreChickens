package cn.evolvefield.mods.morechickens.client.render.ingredient;

import cn.evolvefield.mods.morechickens.client.render.entity.BaseChickenEntityRender;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredient;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.vector.Vector3f;

public class ChickenRenderer {
    private ChickenRenderer() {
    }

    public static void render(MatrixStack matrixStack, int xPosition, int yPosition, ChickenIngredient ChickenIngredient, Minecraft minecraft) {
            BaseChickenEntity chicken = (BaseChickenEntity)ChickenIngredient.getCachedEntity(minecraft.level);

            if (minecraft.player != null && chicken != null) {
                chicken.setChickenName(ChickenIngredient.getChickenType().toString());

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
    public static void render(MatrixStack matrixStack, AnimalEntity chicken, int xPosition, int yPosition, Minecraft minecraft) {

        if (minecraft.player != null && chicken != null) {

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

            //BaseChickenEntityRender baseChickenEntityRender = new BaseChickenEntityRender(minecraft.getEntityRenderDispatcher());
            IRenderTypeBuffer.Impl buffer = minecraft.renderBuffers().bufferSource();
            //baseChickenEntityRender.render((BaseChickenEntity) chicken,0,1,matrixStack,buffer,15728880);
            EntityRendererManager entityrenderermanager = minecraft.getEntityRenderDispatcher();

            entityrenderermanager.render(chicken, 0, 0, 0.0D, minecraft.getFrameTime(), 1, matrixStack, buffer, 15728880);
            buffer.endBatch();
            matrixStack.popPose();
        }

    }
}

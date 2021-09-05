package cn.evolvefield.mods.morechickens.client.render.ingredient;

import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import cn.evolvefield.mods.morechickens.integrations.jei.ingredients.ChickenIngredient;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class ChickenRenderer {
    private ChickenRenderer() {
    }

    public static void render(MatrixStack matrixStack, int xPosition, int yPosition, ChickenIngredient ChickenIngredient, Minecraft minecraft) {
//        if (ModConfig.COMMON.renderChickenIngredientAsEntity.get()) {
            BaseChickenEntity chicken = ChickenIngredient.getCachedEntity(minecraft.level);

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
//            }
        }
//        else {
//            renderBeeFace(xPosition, yPosition, ChickenIngredient, minecraft.level);
//        }
    }
//    private static void renderBeeFace(int xPosition, int yPosition, ChickenIngredient ChickenIngredient, World world) {
//        RenderSystem.enableBlend();
//        RenderSystem.enableAlphaTest();
//        ResourceLocation resLocation = getBeeTexture(ChickenIngredient, world);
//        Minecraft.getInstance().getTextureManager().getTexture(resLocation);
//
//        float[] color = colorCache.get(ChickenIngredient.getChickenType().toString());
//
//        float scale = 1F / 128F;
//        float iconX = 14F;
//        float iconY = 14F;
//        float iconU = 20F;
//        float iconV = 20F;
//
//        if (color == null) {
//            color = new float[]{1.0f, 1.0f, 1.0f};
//        }
//        RenderSystem.color4f(color[0], color[1], color[2], 1.0f);
//        BufferBuilder renderBuffer = Tessellator.getInstance().getBuilder();
//
//        renderBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
//        renderBuffer.vertex(xPosition, yPosition + iconY, 0D).uv((iconU) * scale, (iconV + iconY) * scale).color(color[0], color[1], color[2], 1.0f).endVertex();
//        renderBuffer.vertex(xPosition + iconX, yPosition + iconY, 0D).uv((iconU + iconX) * scale, (iconV + iconY) * scale).color(color[0], color[1], color[2], 1.0f).endVertex();
//        renderBuffer.vertex(xPosition + iconX, yPosition, 0D).uv((iconU + iconX) * scale, (iconV) * scale).color(color[0], color[1], color[2], 1.0f).endVertex();
//        renderBuffer.vertex(xPosition, yPosition, 0D).uv((iconU) * scale, (iconV) * scale).color(color[0], color[1], color[2], 1.0f).endVertex();
//
//        Tessellator.getInstance().end();
//
//        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
//        RenderSystem.disableAlphaTest();
//        RenderSystem.disableBlend();
//    }
//
//    private static HashMap<String, ResourceLocation> chickenTextureLocations = new HashMap<>();
//    private static HashMap<String, float[]> colorCache = new HashMap<>();
//
//    public static ResourceLocation getBeeTexture(@Nonnull ChickenIngredient ingredient, World world) {
//        String chickenId = ingredient.getChickenType().toString();
//        if (chickenTextureLocations.get(chickenId) != null) {
//            return chickenTextureLocations.get(chickenId);
//        }
//
//        BaseChickenEntity chicken = ingredient.getCachedEntity(world);
//        if (chicken != null) {
//
//            chicken.setChickenName(ingredient.getChickenType().toString());
//            colorCache.put(chickenId, chicken.getColor(0).getComponents(null));
//
//
//            EntityRendererManager manager = Minecraft.getInstance().getEntityRenderDispatcher();
//            EntityRenderer<? super BaseChickenEntity> renderer = manager.getRenderer(chicken);
//
//            ResourceLocation resource = renderer.getTextureLocation(chicken);
//            chickenTextureLocations.put(chickenId, resource);
//
//            return chickenTextureLocations.get(chickenId);
//        }
//        return new ResourceLocation("textures/entity/bee/bee.png");
//    }
}

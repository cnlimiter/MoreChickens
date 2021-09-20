package cn.evolvefield.mods.morechickens.common.util.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;

public class RenderUtils {
    public RenderUtils() {
    }

    public static int getArgb(int a, int red, int green, int blue) {
        return a << 24 | red << 16 | green << 8 | blue;
    }

    public static int getAlpha(int argb) {
        return argb >> 24 & 255;
    }

    public static int getRed(int argb) {
        return argb >> 16 & 255;
    }

    public static int getGreen(int argb) {
        return argb >> 8 & 255;
    }

    public static int getBlue(int argb) {
        return argb & 255;
    }

    public static void vertex(IVertexBuilder builder, MatrixStack matrixStack, Vector3f position, Vector2f texCoord, Vector3f normal, int light, int overlay) {
        vertex(builder, matrixStack, position.x(), position.y(), position.z(), texCoord.x, texCoord.y, normal.x(), normal.y(), normal.z(), 255, 255, 255, light, overlay);
    }

    public static void vertex(IVertexBuilder builder, MatrixStack matrixStack, float posX, float posY, float posZ, float texX, float texY, int red, int green, int blue, int light, int overlay) {
        vertex(builder, matrixStack, posX, posY, posZ, texX, texY, 0.0F, 0.0F, -1.0F, red, green, blue, light, overlay);
    }

    public static void vertex(IVertexBuilder builder, MatrixStack matrixStack, float posX, float posY, float posZ, float texX, float texY, int light, int overlay) {
        vertex(builder, matrixStack, posX, posY, posZ, texX, texY, 0.0F, 0.0F, -1.0F, 255, 255, 255, light, overlay);
    }

    public static void vertex(IVertexBuilder builder, MatrixStack matrixStack, float posX, float posY, float posZ, float texX, float texY, float norX, float norY, float norZ, int red, int green, int blue, int light, int overlay) {
        MatrixStack.Entry entry = matrixStack.last();
        builder.vertex(entry.pose(), posX, posY, posZ).color(red, green, blue, 255).uv(texX, texY).overlayCoords(overlay).uv2(light).normal(entry.normal(), norX, norY, norZ).endVertex();
    }
}

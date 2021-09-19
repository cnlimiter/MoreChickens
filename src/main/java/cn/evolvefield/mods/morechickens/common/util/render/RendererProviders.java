package cn.evolvefield.mods.morechickens.common.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RendererProviders {
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static BlockEntityRendererProvider.Context createBlockEntityRendererContext() {
        return new BlockEntityRendererProvider.Context(minecraft.getBlockEntityRenderDispatcher(), minecraft.getBlockRenderer(), minecraft.getEntityModels(), minecraft.font);
    }

    public static EntityRendererProvider.Context createEntityRendererContext() {
        return new EntityRendererProvider.Context(minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getResourceManager(), minecraft.getEntityModels(), minecraft.font);
    }
}

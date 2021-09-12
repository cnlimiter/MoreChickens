package cn.evolvefield.mods.morechickens.client.render.item;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class ChickenItemRender extends ItemRenderer {

    public ChickenItemRender(TextureManager p_i46552_1_, ModelManager p_i46552_2_, ItemColors p_i46552_3_) {
        super(p_i46552_1_, p_i46552_2_, p_i46552_3_);
    }

    public static ResourceLocation getTextureLocation(String tex) {
        return new ResourceLocation("roost", "items/chicken/" + tex);
    }
}

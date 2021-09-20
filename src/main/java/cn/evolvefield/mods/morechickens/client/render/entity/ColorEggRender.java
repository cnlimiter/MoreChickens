package cn.evolvefield.mods.morechickens.client.render.entity;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.entity.ColorEggEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ColorEggRender extends ThrownItemRenderer<ColorEggEntity> {

    public ColorEggRender(EntityRendererProvider.Context context, float p_174417_, boolean p_174418_) {
        super(context, p_174417_, p_174418_);
    }



    @Override
    public void render(ColorEggEntity p_116085_, float p_116086_, float p_116087_, PoseStack p_116088_, MultiBufferSource p_116089_, int p_116090_) {
        super.render(p_116085_, p_116086_, p_116087_, p_116088_, p_116089_, p_116090_);
    }

}




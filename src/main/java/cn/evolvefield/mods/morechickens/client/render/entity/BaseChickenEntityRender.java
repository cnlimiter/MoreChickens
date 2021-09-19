package cn.evolvefield.mods.morechickens.client.render.entity;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;


import javax.annotation.Nullable;

public class BaseChickenEntityRender extends MobRenderer<BaseChickenEntity, ChickenModel<BaseChickenEntity>> {
    protected static final String TEXTURE_TEMPLATE = "textures/entity/chicken/%s.png";


    public BaseChickenEntityRender(EntityRendererProvider.Context manager) {
        super(manager,new ChickenModel<>(manager.bakeLayer(ModelLayers.CHICKEN)),0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(BaseChickenEntity entity) {
        return new ResourceLocation(MoreChickens.MODID, String.format(TEXTURE_TEMPLATE, entity.getChickenName()));
    }

    @Nullable
    @Override
    protected RenderType getRenderType(BaseChickenEntity chickenEntity, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
        return RenderType.entityTranslucent(this.getTextureLocation(chickenEntity));
    }

    @Override
    protected float getBob(BaseChickenEntity livingBase, float partialTicks) {
        float f = Mth.lerp(partialTicks, livingBase.oFlap, livingBase.wingRotation);
        float f1 = Mth.lerp(partialTicks, livingBase.oFlapSpeed, livingBase.destPos);
        return (Mth.sin(f) + 1.0F) * f1;
    }
}

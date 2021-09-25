package cn.evolvefield.mods.morechickens.client.model.item;

import cn.evolvefield.mods.morechickens.MoreChickens;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.BakedModel;

public abstract class ChickensCustomRenderedItemModel extends CustomRenderedItemModel{
    public ChickensCustomRenderedItemModel(BakedModel template, String basePath) {
        super(template, MoreChickens.MODID, basePath);
    }


}

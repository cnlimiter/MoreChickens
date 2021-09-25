package cn.evolvefield.mods.morechickens.client.model.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.BakedModel;

public class ChickenItemModel extends ChickensCustomRenderedItemModel {


    public ChickenItemModel(BakedModel template) {
        super(template, "chicken/vanilla");
        addPartials();
    }

    @Override
    public BlockEntityWithoutLevelRenderer createRenderer() {
        return null;
    }
}

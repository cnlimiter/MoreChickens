package cn.evolvefield.mods.morechickens.client.render.item;

import cn.evolvefield.mods.morechickens.client.render.tile.BreederRenderer;
import cn.evolvefield.mods.morechickens.common.tile.BreederTileEntity;

public class BreederItemRenderer extends BlockItemRendererBase<BreederRenderer, BreederTileEntity> {

    public BreederItemRenderer() {
        super(BreederRenderer::new, BreederTileEntity::new);
    }

}

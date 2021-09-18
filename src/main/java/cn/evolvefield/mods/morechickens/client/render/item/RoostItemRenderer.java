package cn.evolvefield.mods.morechickens.client.render.item;

import cn.evolvefield.mods.morechickens.client.render.tile.RoostRenderer;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;

public class RoostItemRenderer extends BlockItemRendererBase<RoostRenderer, RoostTileEntity>{
    public RoostItemRenderer() {
        super(RoostRenderer::new, RoostTileEntity::new);
    }
}

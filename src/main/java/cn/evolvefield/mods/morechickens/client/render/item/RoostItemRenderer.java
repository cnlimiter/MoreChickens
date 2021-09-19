package cn.evolvefield.mods.morechickens.client.render.item;

import cn.evolvefield.mods.morechickens.client.render.tile.BreederRenderer;
import cn.evolvefield.mods.morechickens.client.render.tile.RoostRenderer;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import net.minecraft.core.BlockPos;

public class RoostItemRenderer extends BlockItemRendererBase<RoostRenderer, RoostTileEntity>{
    public RoostItemRenderer() {
        super(RoostRenderer::new, () -> new RoostTileEntity(BlockPos.ZERO, ModBlocks.BLOCK_BREEDER.defaultBlockState()));
    }
}

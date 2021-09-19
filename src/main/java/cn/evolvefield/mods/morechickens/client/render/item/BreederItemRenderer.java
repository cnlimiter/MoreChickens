package cn.evolvefield.mods.morechickens.client.render.item;

import cn.evolvefield.mods.morechickens.client.render.tile.BreederRenderer;
import cn.evolvefield.mods.morechickens.client.render.tile.RoostRenderer;
import cn.evolvefield.mods.morechickens.common.tile.BreederTileEntity;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import net.minecraft.core.BlockPos;

public class BreederItemRenderer extends BlockItemRendererBase<BreederRenderer, BreederTileEntity> {

    public BreederItemRenderer() {
        super(BreederRenderer::new, () -> new BreederTileEntity(BlockPos.ZERO, ModBlocks.BLOCK_BREEDER.defaultBlockState()));    }

}

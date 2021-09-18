package cn.evolvefield.mods.morechickens.common.tile.base;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;

public class FakeWorldTileEntity extends SyncableTileEntity {
    private boolean fakeWorld;
    private BlockState defaultState;

    public FakeWorldTileEntity(TileEntityType<?> tileEntityType, BlockState defaultState) {
        super(tileEntityType);
        this.defaultState = defaultState;
    }

    public void setFakeWorld(World w) {
        level = w;
        fakeWorld = true;
    }

    public boolean isFakeWorld() {
        return fakeWorld;
    }

    @Override
    public BlockState getBlockState() {
        if (fakeWorld) {
            return defaultState;
        }
        return super.getBlockState();
    }
}

package cn.evolvefield.mods.morechickens.core.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class NestContainer extends NestAbstractContainer{
    protected NestContainer(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

    @Override
    protected TileEntity getTileEntity() {
        return null;
    }

    @Override
    public boolean stillValid(PlayerEntity p_75145_1_) {
        return false;
    }
}

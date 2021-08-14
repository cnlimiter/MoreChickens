package cn.evolvefield.mods.morechickens.core.container;



import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class NestContainer extends NestAbstractContainer{
    protected NestContainer(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    @Override
    protected BlockEntity getTileEntity() {
        return null;
    }

    @Override
    public boolean stillValid(Player p_75145_1_) {
        return false;
    }
}

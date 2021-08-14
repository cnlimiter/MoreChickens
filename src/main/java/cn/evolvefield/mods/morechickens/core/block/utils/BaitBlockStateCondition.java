package cn.evolvefield.mods.morechickens.core.block.utils;


import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BaitBlockStateCondition implements BaitEnvironmentCondition{
    private final BlockState state;

    public BaitBlockStateCondition(BlockState state) {
        this.state = state;
    }

    @Override
    public boolean test(BlockState blockState, FluidState fluidState) {
        return state == blockState;
    }
}

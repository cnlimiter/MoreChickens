package cn.evolvefield.mods.morechickens.core.block.utils;


import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public interface BaitEnvironmentCondition {
    boolean test(BlockState blockState, FluidState fluidState);
}

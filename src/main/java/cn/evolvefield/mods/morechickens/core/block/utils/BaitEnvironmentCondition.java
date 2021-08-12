package cn.evolvefield.mods.morechickens.core.block.utils;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;

public interface BaitEnvironmentCondition {
    boolean test(BlockState blockState, FluidState fluidState);
}

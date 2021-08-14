package cn.evolvefield.mods.morechickens.core.block.utils;


import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class BaitFluidCondition implements BaitEnvironmentCondition{
    private final Fluid fluid;

    public BaitFluidCondition(Fluid fluid) {
        this.fluid = fluid;
    }

    @Override
    public boolean test(BlockState blockState, FluidState fluidState) {
        return fluid == fluidState.getType();
    }
}

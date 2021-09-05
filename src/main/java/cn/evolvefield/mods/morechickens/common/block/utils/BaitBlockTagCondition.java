package cn.evolvefield.mods.morechickens.common.block.utils;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.ResourceLocation;

public class BaitBlockTagCondition implements BaitEnvironmentCondition{
    private final ResourceLocation tag;

    public BaitBlockTagCondition(ResourceLocation tag) {
        this.tag = tag;
    }

    @Override
    public boolean test(BlockState blockState, FluidState fluidState) {
        return blockState.getBlock().getTags().contains(tag);
    }
}

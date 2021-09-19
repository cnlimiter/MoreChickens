package cn.evolvefield.mods.morechickens.common.item;

import net.minecraft.advancements.CriteriaTriggers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class BallItem extends Item {

    private final Fluid fluid;

    public BallItem(Properties properties, Fluid fluid) {
        super(properties);
        this.fluid = fluid;
    }




    /**
     * Place the fluid
     * @return
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        BlockHitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.NONE);
        InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, itemstack, raytraceresult);
        if (ret != null) return ret;
        if (raytraceresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else if (raytraceresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            BlockPos blockpos = raytraceresult.getBlockPos();
            Direction direction = raytraceresult.getDirection();
            BlockPos blockpos1 = blockpos.relative(direction);
            if (worldIn.mayInteract(playerIn, blockpos) && playerIn.mayUseItemAt(blockpos1, direction, itemstack)) {
                BlockState blockstate = worldIn.getBlockState(blockpos);
                BlockPos blockpos2 = canBlockContainFluid(worldIn, blockpos, blockstate) ? blockpos : blockpos1;
                if (this.tryPlaceContainedLiquid(playerIn, worldIn, blockpos2, raytraceresult)) {
                    if (playerIn instanceof ServerPlayer) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)playerIn, blockpos2, itemstack);
                    }

                    playerIn.awardStat(Stats.ITEM_USED.get(this));
                    if(!playerIn.isCreative())
                        itemstack.shrink(1);
                    return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide);
                } else {
                    return InteractionResultHolder.fail(itemstack);
                }
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        }
    }
    public int getBurnTime(ItemStack stack){
        return fluid == Fluids.LAVA ? 20_000 : 0;
    }

    public boolean tryPlaceContainedLiquid(@Nullable Player player, Level worldIn, BlockPos posIn, @Nullable BlockHitResult rayTrace) {
        if (!(this.fluid instanceof FlowingFluid)) {
            return false;
        } else {
            BlockState blockstate = worldIn.getBlockState(posIn);
            Block block = blockstate.getBlock();
            Material material = blockstate.getMaterial();
            boolean flag = blockstate.canBeReplaced(this.fluid);
            boolean flag1 = blockstate.isAir() || flag || block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(worldIn, posIn, blockstate, this.fluid);
            if (!flag1) {
                return rayTrace != null && this.tryPlaceContainedLiquid(player, worldIn, rayTrace.getBlockPos().relative(rayTrace.getDirection()), null);
            } else if (worldIn.dimensionType().ultraWarm() && this.fluid.is(FluidTags.WATER)) {
                int i = posIn.getX();
                int j = posIn.getY();
                int k = posIn.getZ();
                worldIn.playSound(player, posIn, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.8F);

                for(int l = 0; l < 8; ++l) {
                    worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                return true;
            } else if (block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(worldIn,posIn,blockstate,fluid)) {
                ((LiquidBlockContainer)block).placeLiquid(worldIn, posIn, blockstate, ((FlowingFluid)this.fluid).getSource(false));
                this.playEmptySound(player, worldIn, posIn);
                return true;
            } else {
                if (!worldIn.isClientSide && flag && !material.isLiquid()) {
                    worldIn.destroyBlock(posIn, true);
                }

                if (!worldIn.setBlock(posIn, ((FlowingFluid) this.fluid).defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
                    return false;
                } else {
                    this.playEmptySound(player, worldIn, posIn);
                    return true;
                }
            }
        }
    }

    protected void playEmptySound(@Nullable Player player, LevelAccessor worldIn, BlockPos pos) {
        SoundEvent soundevent = this.fluid.getAttributes().getEmptySound();
        if(soundevent == null) soundevent = this.fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        worldIn.playSound(player, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private boolean canBlockContainFluid(Level worldIn, BlockPos posIn, BlockState blockstate)
    {
        return blockstate.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer)blockstate.getBlock()).canPlaceLiquid(worldIn, posIn, blockstate, this.fluid);
    }
}

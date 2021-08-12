package cn.evolvefield.mods.morechickens.core.tile;

import cn.evolvefield.mods.morechickens.core.block.BaitBlock;
import cn.evolvefield.mods.morechickens.core.block.utils.BaitEnvironmentCondition;
import cn.evolvefield.mods.morechickens.core.block.utils.BaitType;
import cn.evolvefield.mods.morechickens.core.block.utils.EnvironmentalCondition;
import cn.evolvefield.mods.morechickens.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;

public class BaitTileEntity extends TileEntity implements ITickableTileEntity {

    private static final int ENVIRONMENTAL_CHECK_INTERVAL = 20 * 10;
    private static final int MAX_BAITS_IN_AREA = 2;
    private static final int MIN_ENV_IN_AREA = 10;
    private static final int MAX_ANIMALS_IN_AREA = 2;
    private static final int SPAWN_CHECK_INTERVAL = 20;
    private static final int MIN_DISTANCE_NO_PLAYERS = 6;


    private EnvironmentalCondition environmentStatus;
    private int ticksSinceEnvironmentalCheck;
    private int ticksSinceSpawnCheck;

    public BaitTileEntity() {
        super(ModTileEntities.BAIT);
    }

    @Override
    public void tick() {
        final BaitType baitType = getBaitType();

        ticksSinceEnvironmentalCheck++;

        ticksSinceSpawnCheck++;
        if (ticksSinceSpawnCheck >= SPAWN_CHECK_INTERVAL) {
            if (!level.isClientSide && level.random.nextFloat() <= baitType.getChance()) {
                if (checkSpawnConditions(true) == EnvironmentalCondition.CanSpawn) {
                    final float range = MIN_DISTANCE_NO_PLAYERS;
                    if (level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(getBlockPos().getX() - range, getBlockPos().getY() - range, getBlockPos().getZ() - range, getBlockPos().getX() + range, getBlockPos().getY() + range, getBlockPos().getZ() + range)).isEmpty()) {
                        baitType.createEntity(level,getBlockPos().getX()+ 0.5,getBlockPos().getY()+ 0.5,getBlockPos().getZ()+ 0.5);
                        //entityLiving.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                        //level.addEntity(entityLiving);
                        level.addParticle(ParticleTypes.EXPLOSION, getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5,  0, 0, 0);
                        level.playSound(null, getBlockPos(), SoundEvents.CHICKEN_EGG, SoundCategory.BLOCKS, 1f, 1f);

                        level.removeBlock(getBlockPos(), false);
                    }
                }
            }
            ticksSinceSpawnCheck = 0;
        }
    }


    public EnvironmentalCondition checkSpawnConditions(boolean checkNow) {
        if (checkNow || ticksSinceEnvironmentalCheck > ENVIRONMENTAL_CHECK_INTERVAL) {
            BaitType baitType = getBaitType();
            Collection<BaitEnvironmentCondition> envBlocks = baitType.getEnvironmentConditions();
            final int range = 5;
            final int rangeVertical = 3;
            int countBait = 0;
            int countEnvBlocks = 0;
            boolean foundWater = false;
            for (int x = getBlockPos().getX() - range; x < getBlockPos().getX() + range; x++) {
                for (int y = getBlockPos().getY() - rangeVertical; y < getBlockPos().getY() + rangeVertical; y++) {
                    for (int z = getBlockPos().getZ() - range; z < getBlockPos().getZ() + range; z++) {
                        BlockPos testPos = new BlockPos(x, y, z);
                        BlockState blockState = level.getBlockState(testPos);
                        FluidState fluidState = level.getFluidState(testPos);
                        if (blockState.getBlock() instanceof BaitBlock) {
                            countBait++;
                        } else if (fluidState.getType() == Fluids.WATER || fluidState.getType() == Fluids.FLOWING_WATER) {
                            foundWater = true;
                        }

                        for (BaitEnvironmentCondition envBlock : envBlocks) {
                            if (envBlock.test(blockState, fluidState)) {
                                countEnvBlocks++;
                            }
                        }
                    }
                }
            }
            if (!foundWater) {
                environmentStatus = EnvironmentalCondition.NoWater;
            } else if (countBait > MAX_BAITS_IN_AREA) {
                environmentStatus = EnvironmentalCondition.NearbyBait;
            } else if (countEnvBlocks < MIN_ENV_IN_AREA) {
                environmentStatus = EnvironmentalCondition.WrongEnv;
            } else if (level.getEntitiesOfClass(AnimalEntity.class, new AxisAlignedBB(getBlockPos().getX() - range * 2, getBlockPos().getY() - rangeVertical, getBlockPos().getZ() - range * 2, getBlockPos().getX() + range * 2, getBlockPos().getY() + rangeVertical, getBlockPos().getZ() + range * 2)).size() > MAX_ANIMALS_IN_AREA) {
                environmentStatus = EnvironmentalCondition.NearbyAnimal;
            } else {
                environmentStatus = EnvironmentalCondition.CanSpawn;
            }
            ticksSinceEnvironmentalCheck = 0;
        }

        return environmentStatus;
    }

    public BaitType getBaitType() {
        return ((BaitBlock) getBlockState().getBlock()).getBaitType();
    }
}

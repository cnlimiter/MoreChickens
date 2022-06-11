package cn.evolvefield.mods.morechickens.common.block;

import cn.evolvefield.mods.morechickens.common.block.utils.BaitType;
import cn.evolvefield.mods.morechickens.common.block.utils.EnvironmentalCondition;
import cn.evolvefield.mods.morechickens.common.tile.BaitTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Random;

public class BaitBlock extends BaseEntityBlock {
    public static final String nameSuffix = "_bait";

    private static final VoxelShape BOUNDING_BOX = Block.box(0.1, 0, 0.1, 0.9, 0.1, 0.9);

    private final BaitType baitType;

    public BaitBlock(BaitType baitType) {
        super(Properties.of(Material.CAKE).strength(0.1f));
        this.baitType = baitType;
    }




    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return BOUNDING_BOX;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext p_220071_4_) {
        return Shapes.empty();
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BaitTileEntity(pos, state);
    }



    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        BaitTileEntity tileEntity = (BaitTileEntity) world.getBlockEntity(pos);
        if (tileEntity != null) {
            EnvironmentalCondition environmentStatus = tileEntity.checkSpawnConditions(true);
            if (!world.isClientSide) {
                TranslatableComponent chatComponent = new TranslatableComponent(environmentStatus.langKey);
                chatComponent.getStyle().withColor(environmentStatus != EnvironmentalCondition.CanSpawn ? ChatFormatting.RED : ChatFormatting.GREEN);
                player.sendMessage(chatComponent, Util.NIL_UUID);
            }
        }

        return InteractionResult.SUCCESS;
    }


    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer instanceof Player) {
            BaitTileEntity tileEntity = (BaitTileEntity) world.getBlockEntity(pos);
            if (tileEntity != null) {
                EnvironmentalCondition environmentStatus = tileEntity.checkSpawnConditions(true);
                if (!world.isClientSide) {
                    TranslatableComponent chatComponent = new TranslatableComponent(environmentStatus.langKey);
                    chatComponent.getStyle().withColor(environmentStatus != EnvironmentalCondition.CanSpawn ? ChatFormatting.RED : ChatFormatting.GREEN);
                    placer.sendMessage(chatComponent, Util.NIL_UUID);
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState stateIn, Level world, BlockPos pos, Random rand) {
            BaitTileEntity tileEntity = (BaitTileEntity) world.getBlockEntity(pos);
            if (tileEntity != null && tileEntity.checkSpawnConditions(false) == EnvironmentalCondition.CanSpawn) {
                if (rand.nextFloat() <= 0.2f) {
                    world.addParticle(ParticleTypes.SMOKE, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat() * 0.5f, pos.getZ() + rand.nextFloat(), 0.0, 0.0, 0.0);
                }
            }
    }





    public BaitType getBaitType() {
        return baitType;
    }


}

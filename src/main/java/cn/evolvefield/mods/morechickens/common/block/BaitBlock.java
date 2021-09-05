package cn.evolvefield.mods.morechickens.common.block;

import cn.evolvefield.mods.morechickens.common.block.utils.BaitType;
import cn.evolvefield.mods.morechickens.common.block.utils.EnvironmentalCondition;
import cn.evolvefield.mods.morechickens.common.tile.BaitTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BaitBlock extends ContainerBlock {
    public static final String nameSuffix = "_bait";

    private static final VoxelShape BOUNDING_BOX = VoxelShapes.box(0.1, 0, 0.1, 0.9, 0.1, 0.9);

    private final BaitType baitType;

    public BaitBlock(BaitType baitType) {
        super(Properties.of(Material.CAKE).strength(0.1f));
        this.baitType = baitType;
    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return BOUNDING_BOX;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext p_220071_4_) {
        return VoxelShapes.empty();
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
        return new BaitTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        BaitTileEntity tileEntity = (BaitTileEntity) world.getBlockEntity(pos);
        if (tileEntity != null) {
            EnvironmentalCondition environmentStatus = tileEntity.checkSpawnConditions(true);
            if (!world.isClientSide) {
                TextComponent chatComponent = new TranslationTextComponent(environmentStatus.langKey);
                chatComponent.getStyle().withColor(environmentStatus != EnvironmentalCondition.CanSpawn ? TextFormatting.RED : TextFormatting.GREEN);
                player.sendMessage(chatComponent, Util.NIL_UUID);
            }
        }

        return ActionResultType.SUCCESS;
    }


    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer instanceof PlayerEntity) {
            BaitTileEntity tileEntity = (BaitTileEntity) world.getBlockEntity(pos);
            if (tileEntity != null) {
                EnvironmentalCondition environmentStatus = tileEntity.checkSpawnConditions(true);
                if (!world.isClientSide) {
                    TextComponent chatComponent = new TranslationTextComponent(environmentStatus.langKey);
                    chatComponent.getStyle().withColor(environmentStatus != EnvironmentalCondition.CanSpawn ? TextFormatting.RED : TextFormatting.GREEN);
                    placer.sendMessage(chatComponent, Util.NIL_UUID);
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState stateIn, World world, BlockPos pos, Random rand) {
        //if (!ExCompressumConfig.CLIENT.disableParticles.get()) {
            BaitTileEntity tileEntity = (BaitTileEntity) world.getBlockEntity(pos);
            if (tileEntity != null && tileEntity.checkSpawnConditions(false) == EnvironmentalCondition.CanSpawn) {
                if (rand.nextFloat() <= 0.2f) {
                    world.addParticle(ParticleTypes.SMOKE, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat() * 0.5f, pos.getZ() + rand.nextFloat(), 0.0, 0.0, 0.0);
                }
            }
        //}
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

    }



    public BaitType getBaitType() {
        return baitType;
    }


}

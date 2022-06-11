package cn.evolvefield.mods.morechickens.common.block;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.item.ChickenItem;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;
import cn.evolvefield.mods.morechickens.common.util.ItemUtils;
import cn.evolvefield.mods.morechickens.common.util.blockentity.SimpleBlockEntityTicker;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class RoostBlock extends BaseEntityBlock{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public RoostBlock() {
        super(Properties.of(Material.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.0f,5.0f)
                .noCollission()

        );
        setRegistryName(new ResourceLocation(MoreChickens.MODID, "roost"));

    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(handIn);
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        if (!(tileEntity instanceof RoostTileEntity)) {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }
        RoostTileEntity breeder = (RoostTileEntity) tileEntity;

        if (!breeder.hasChickenItem() && heldItem.getItem() instanceof ChickenItem) {
            breeder.setChickenItem(heldItem.copy());
            ItemUtils.decrItemStack(heldItem, player);
            worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;

        } else if (player.isShiftKeyDown() && breeder.hasChickenItem()) {
            ItemStack stack = breeder.removeChickenItem();
            if (heldItem.isEmpty()) {
                player.setItemInHand(handIn, stack);
            } else {
                if (!player.getInventory().add(stack)) {
                    Direction direction = state.getValue(FACING);
                    Containers.dropItemStack(worldIn, direction.getStepX() + pos.getX() + 0.5D, pos.getY() + 0.5D, direction.getStepZ() + pos.getZ() + 0.5D, stack);
                }
            }
            worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        } else {
            if (!worldIn.isClientSide())
            {
                openGui((ServerPlayer) player, (RoostTileEntity) tileEntity);

            }
            return InteractionResult.SUCCESS;
        }
    }

    public void openGui(ServerPlayer player, RoostTileEntity tileEntity) {
        NetworkHooks.openGui(player, tileEntity, packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getBlockPos()));
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
        BlockEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof RoostTileEntity) {
            Containers.dropItemStack((Level) world, pos.getX(),pos.getY(),pos.getZ(), ModBlocks.BLOCK_ROOST.asItem().getDefaultInstance());
        }

        super.destroy(world, pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level1, BlockState state, BlockEntityType<T> type) {
        return new SimpleBlockEntityTicker<>();
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RoostTileEntity(blockPos, blockState);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1F;
    }

}

package cn.evolvefield.mods.morechickens.core.block;



import cn.evolvefield.mods.morechickens.core.tile.BreederTileEntity;
import cn.evolvefield.mods.morechickens.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;

public class BlockBreeder extends BaseEntityBlock {

    public static final BooleanProperty IS_BREEDING = BooleanProperty.create("is_breeding");
    public static final BooleanProperty HAS_SEEDS = BooleanProperty.create("has_seeds");

    public BlockBreeder() {
        super(Properties.of(Material.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.0f,5.0f)

        );
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(IS_BREEDING, false)
                .setValue(HAS_SEEDS,false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_BREEDING,HAS_SEEDS);
    }



    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        BlockEntity tileEntity = world instanceof LevelChunk ? ((LevelChunk) world).getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK)
                : world.getBlockEntity(pos);

        if (tileEntity instanceof BreederTileEntity) {
            BreederTileEntity breederTileEntity = (BreederTileEntity) tileEntity;
            return state.setValue(IS_BREEDING, breederTileEntity.isFullOfChickens()).setValue(HAS_SEEDS, breederTileEntity.isFullOfSeeds());
        }
        return state;
    }




    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BreederTileEntity tileEntity = (BreederTileEntity) world.getBlockEntity(pos);

        if (tileEntity == null) {
            return InteractionResult.FAIL;
        }

        NetworkHooks.openGui((ServerPlayer) player, tileEntity
                ,
                (FriendlyByteBuf packerBuffer) -> {
                    packerBuffer.writeBlockPos(tileEntity.getBlockPos());
                });

        return InteractionResult.SUCCESS;
    }


    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
        BlockEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof BreederTileEntity) {
            Containers.dropItemStack((Level) world, pos.getX(),pos.getY(),pos.getZ(), ModBlocks.BLOCK_BREEDER.asItem().getDefaultInstance());
        }

        super.destroy(world, pos, state);
    }



    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_196283_1_,BlockState state) {
        return new BreederTileEntity(p_196283_1_,state);
    }



}

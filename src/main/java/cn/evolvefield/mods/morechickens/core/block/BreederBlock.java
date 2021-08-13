package cn.evolvefield.mods.morechickens.core.block;



import cn.evolvefield.mods.morechickens.core.tile.BreederTileEntity;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class BreederBlock extends ContainerBlock {

    public static final BooleanProperty IS_BREEDING = BooleanProperty.create("is_breeding");
    public static final BooleanProperty HAS_SEEDS = BooleanProperty.create("has_seeds");

    public BreederBlock() {
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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(IS_BREEDING,HAS_SEEDS);
    }



    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IWorldReader world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        TileEntity tileEntity = world instanceof Chunk ? ((Chunk) world).getBlockEntity(pos, Chunk.CreateEntityType.CHECK)
                : world.getBlockEntity(pos);

        if (tileEntity instanceof BreederTileEntity) {
            BreederTileEntity breederTileEntity = (BreederTileEntity) tileEntity;
            return state.setValue(IS_BREEDING, breederTileEntity.isFullOfChickens()).setValue(HAS_SEEDS, breederTileEntity.isFullOfSeeds());
        }
        return state;
    }




    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }

        BreederTileEntity tileEntity = (BreederTileEntity) world.getBlockEntity(pos);

        if (tileEntity == null) {
            return ActionResultType.FAIL;
        }

        NetworkHooks.openGui((ServerPlayerEntity) player, tileEntity
                ,
                (PacketBuffer packerBuffer) -> {
                    packerBuffer.writeBlockPos(tileEntity.getBlockPos());
                });

        return ActionResultType.SUCCESS;
    }

    @Override
    public void destroy(IWorld world, BlockPos pos, BlockState state) {
        TileEntity tileEntity = world.getBlockEntity(pos);

        if (tileEntity instanceof BreederTileEntity) {
            InventoryHelper.dropItemStack((World) world, pos.getX(),pos.getY(),pos.getZ(), ModBlocks.BLOCK_BREEDER.asItem().getDefaultInstance());
        }

        super.destroy(world, pos, state);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
        return new BreederTileEntity();
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }




}

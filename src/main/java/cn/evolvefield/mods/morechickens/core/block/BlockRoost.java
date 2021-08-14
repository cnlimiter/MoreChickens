package cn.evolvefield.mods.morechickens.core.block;


import cn.evolvefield.mods.morechickens.core.tile.RoostTileEntity;
import cn.evolvefield.mods.morechickens.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;


import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public class BlockRoost extends BaseEntityBlock {
    public static class ChickenTypeProperty extends Property<String> {
        private final String name;

        public ChickenTypeProperty(String name) {
            super(name,String.class);
            this.name = name;
        }
        @Override
        public Collection<String> getPossibleValues() {
            return null;
        }
        @Override
        public String getName(String name1) {
            return name;
        }
        @Override
        public Optional<String> getValue(String value) {
            return Optional.of(value);
        }
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final ChickenTypeProperty CHICKEN = new ChickenTypeProperty("chicken");

    public BlockRoost() {
        super(Properties.of(Material.WOOD)
            .sound(SoundType.WOOD)
                .strength(2.0f,5.0f)

        );
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }



    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState();
        LevelAccessor iworldreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Direction[] adirection = context.getNearestLookingDirections();

        for(Direction direction : adirection) {
            if (direction.getAxis().isHorizontal()) {
                Direction direction1 = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, direction1);

                    return blockstate;

            }
        }
        return  null;
        //return this.defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite());
    }


    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }


    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    //    @Nullable
//    @Override
//    public BlockState getStateForPlacement(BlockItemUseContext context) {
//        IBlockReader world = context.getLevel();
//        BlockPos blockpos = context.getClickedPos();
//        BlockState blockstate = context.getLevel().getBlockState(blockpos);
//
//        String chickenType = "chickens:empty";
//        TileEntity tileEntity = world instanceof Chunk
//                ? ((Chunk) world).getBlockEntity(blockpos, Chunk.CreateEntityType.CHECK)
//                : world.getBlockEntity(blockpos);
//
//        if (tileEntity instanceof TileEntityRoost) {
//            DataChicken chickenData = ((TileEntityRoost) tileEntity).createChickenData();
//            if (chickenData != null) chickenType = chickenData.getChickenType();
//        }
//
//        if (chickenType == null) chickenType = "minecraft:vanilla";
//
//        return blockstate.setValue(CHICKEN, chickenType);
//
//    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player playerEntity, InteractionHand hand, BlockHitResult result) {
        if (world.isClientSide) {
            return InteractionResult.FAIL;
        }
        RoostTileEntity tileEntity = (RoostTileEntity) world.getBlockEntity(pos);
        if (tileEntity == null) {
            return InteractionResult.FAIL;
        }
        if (playerEntity.isCrouching() && tileEntity.pullChickenOut(playerEntity)) {
            return InteractionResult.SUCCESS;
        }

        if (tileEntity.putChickenIn(playerEntity.getItemInHand(hand))) {
            return InteractionResult.SUCCESS;
        }

        NetworkHooks.openGui((ServerPlayer) playerEntity, tileEntity
                ,
                (FriendlyByteBuf packerBuffer) -> {
            packerBuffer.writeBlockPos(tileEntity.getBlockPos());
        }
        );


        return InteractionResult.SUCCESS;
    }



    @Override
    public void destroy(LevelAccessor world, BlockPos blockPos, BlockState state) {
        BlockEntity tileEntity =world.getBlockEntity(blockPos);
        if (tileEntity instanceof RoostTileEntity) {
            Containers.dropItemStack((Level) world, blockPos.getX(),blockPos.getY(),blockPos.getZ(), ModBlocks.BLOCK_ROOST.asItem().getDefaultInstance());
        }
        super.destroy(world, blockPos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }



    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {
        return new RoostTileEntity(pos, state);
    }




}

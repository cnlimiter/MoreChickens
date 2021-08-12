package cn.evolvefield.mods.morechickens.core.block;


import cn.evolvefield.mods.morechickens.core.tile.TileEntityRoost;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public class BlockRoost extends ContainerBlock {
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

    public static final DirectionProperty FACING = HorizontalBlock.FACING;
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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = this.defaultBlockState();
        IWorldReader iworldreader = context.getLevel();
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
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result) {
        if (world.isClientSide) {
            return ActionResultType.FAIL;
        }
        TileEntityRoost tileEntity = (TileEntityRoost) world.getBlockEntity(pos);
        if (tileEntity == null) {
            return ActionResultType.FAIL;
        }
        if (playerEntity.isCrouching() && tileEntity.pullChickenOut(playerEntity)) {
            return ActionResultType.SUCCESS;
        }

        if (tileEntity.putChickenIn(playerEntity.getItemInHand(hand))) {
            return ActionResultType.SUCCESS;
        }

        NetworkHooks.openGui((ServerPlayerEntity) playerEntity, tileEntity
                ,
                (PacketBuffer packerBuffer) -> {
            packerBuffer.writeBlockPos(tileEntity.getBlockPos());
        }
        );


        return ActionResultType.SUCCESS;
    }



    @Override
    public void destroy(IWorld world, BlockPos blockPos, BlockState state) {
        TileEntity tileEntity =world.getBlockEntity(blockPos);
        if (tileEntity instanceof TileEntityRoost) {
            InventoryHelper.dropItemStack((World) world, blockPos.getX(),blockPos.getY(),blockPos.getZ(), ModBlocks.BLOCK_ROOST.asItem().getDefaultInstance());
        }
        super.destroy(world, blockPos, state);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }



    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader blockReader) {
        return new TileEntityRoost();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }




}

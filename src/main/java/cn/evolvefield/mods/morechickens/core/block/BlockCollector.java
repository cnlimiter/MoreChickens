package cn.evolvefield.mods.morechickens.core.block;


import cn.evolvefield.mods.morechickens.core.tile.TileEntityCollector;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class BlockCollector extends ContainerBlock {
    public BlockCollector() {
        super(Properties.of(Material.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.0f,5.0f)

        );
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result) {
        TileEntityCollector tileEntity = (TileEntityCollector) world.getBlockEntity(pos);


        if (!world.isClientSide) {
            NetworkHooks.openGui((ServerPlayerEntity) playerEntity, tileEntity
                    ,
                    (PacketBuffer packerBuffer) -> {
                        packerBuffer.writeBlockPos(tileEntity.getBlockPos());
                    }
            );
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void destroy(IWorld world, BlockPos blockPos, BlockState state) {
        TileEntity tileEntity = world.getBlockEntity(blockPos);

        if (tileEntity instanceof IInventory) {
            InventoryHelper.dropItemStack((World) world, blockPos.getX(),blockPos.getY(),blockPos.getZ(), ModBlocks.BLOCK_COLLECTOR.asItem().getDefaultInstance());
        }

        super.destroy(world, blockPos, state);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
        return new TileEntityCollector();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}

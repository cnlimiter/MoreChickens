package cn.evolvefield.mods.morechickens.common.block;


import cn.evolvefield.mods.morechickens.common.tile.CollectorTileEntity;
import cn.evolvefield.mods.morechickens.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;


import javax.annotation.Nullable;

public class CollectorBlock extends BaseEntityBlock  {
    public CollectorBlock() {
        super(Properties.of(Material.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.0f,5.0f)

        );
        setRegistryName("collector");
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player playerEntity, InteractionHand hand, BlockHitResult result) {
        CollectorTileEntity tileEntity = (CollectorTileEntity) world.getBlockEntity(pos);


        if (!world.isClientSide) {
            NetworkHooks.openGui((ServerPlayer) playerEntity, tileEntity
                    ,
                    (FriendlyByteBuf packerBuffer) -> {
                        packerBuffer.writeBlockPos(tileEntity.getBlockPos());
                    }
            );
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos blockPos, BlockState state) {
        BlockEntity tileEntity = world.getBlockEntity(blockPos);

        if (tileEntity instanceof Container) {
            Containers.dropItemStack((Level) world, blockPos.getX(),blockPos.getY(),blockPos.getZ(), ModBlocks.BLOCK_COLLECTOR.asItem().getDefaultInstance());
        }

        super.destroy(world, blockPos, state);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {
        return new CollectorTileEntity(pos, state);
    }

}

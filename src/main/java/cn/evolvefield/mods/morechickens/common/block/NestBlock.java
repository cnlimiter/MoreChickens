package cn.evolvefield.mods.morechickens.common.block;

import cn.evolvefield.mods.morechickens.common.tile.NestTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class NestBlock extends ContainerBlock {
    public NestBlock() {
        super(Properties.of(Material.WOOD)
                .strength(3.0f,4.0f)
                .sound(SoundType.WOOD)
                );
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
        return new NestTileEntity();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof NestTileEntity){
            NestTileEntity nest = (NestTileEntity)tileEntity;
            return Math.min(15, nest.numChickens());
        }
        return 0;
    }

    @Override
    public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        if (!worldIn.isClientSide && te instanceof NestTileEntity) {
            NestTileEntity nestTileEntity = (NestTileEntity) te;
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player) == 0) {
                nestTileEntity.spawnChickens(worldIn);
                worldIn.updateNeighborsAt(pos, this);
            }
        }
    }

    @Override
    public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!worldIn.isClientSide && player.isCreative() && worldIn.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof NestTileEntity) {
                NestTileEntity nestTileEntity = (NestTileEntity) tileentity;
                ItemStack itemstack = new ItemStack(this);
                boolean flag = nestTileEntity.numChickens() > 0;
                if (!flag) {
                    return;
                }

                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.put("Chickens", nestTileEntity.getChickens());
                itemstack.addTagElement("BlockEntityTag", compoundnbt);

                ItemEntity itementity = new ItemEntity(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemstack);
                itementity.setDefaultPickUpDelay();
                worldIn.addFreshEntity(itementity);
            }
        }
        super.playerWillDestroy(worldIn, pos, state, player);
    }


    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Entity entity = builder.getOptionalParameter(LootParameters.THIS_ENTITY);
        if (entity instanceof TNTEntity || entity instanceof CreeperEntity || entity instanceof WitherSkullEntity || entity instanceof WitherEntity || entity instanceof TNTMinecartEntity) {
            TileEntity tileentity = builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
            if (tileentity instanceof NestTileEntity) {
                NestTileEntity nestTileEntity = (NestTileEntity)tileentity;
                nestTileEntity.spawnChickens(builder.getLevel());
            }
        }
        return super.getDrops(state, builder);
    }
}

package cn.evolvefield.mods.morechickens.core.block;

import cn.evolvefield.mods.morechickens.core.tile.NestTileEntity;

import net.minecraft.core.BlockPos;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import javax.annotation.Nullable;
import java.util.List;

public class NestBlock extends BaseEntityBlock {
    public NestBlock() {
        super(Properties.of(Material.WOOD)
                .strength(3.0f,4.0f)
                .sound(SoundType.WOOD)
                );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NestTileEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_149645_1_) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof NestTileEntity){
            NestTileEntity nest = (NestTileEntity)tileEntity;
            return Math.min(15, nest.numChickens());
        }
        return 0;
    }


    @Override
    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
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
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        if (!worldIn.isClientSide && player.isCreative() && worldIn.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof NestTileEntity) {
                NestTileEntity nestTileEntity = (NestTileEntity) tileentity;
                ItemStack itemstack = new ItemStack(this);
                boolean flag = nestTileEntity.numChickens() > 0;
                if (!flag) {
                    return;
                }

                CompoundTag compoundnbt = new CompoundTag();
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
        Entity entity = builder.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (entity instanceof MinecartTNT || entity instanceof Creeper || entity instanceof WitherSkull || entity instanceof WitherBoss ) {
            BlockEntity tileentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (tileentity instanceof NestTileEntity) {
                NestTileEntity nestTileEntity = (NestTileEntity)tileentity;
                nestTileEntity.spawnChickens(builder.getLevel());
            }
        }
        return super.getDrops(state, builder);
    }
}

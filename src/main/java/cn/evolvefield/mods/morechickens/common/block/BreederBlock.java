package cn.evolvefield.mods.morechickens.common.block;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.block.base.HorizontalRotatableBlock;
import cn.evolvefield.mods.morechickens.common.container.BreederContainer;
import cn.evolvefield.mods.morechickens.common.item.ChickenItem;
import cn.evolvefield.mods.morechickens.common.tile.BreederTileEntity;
import cn.evolvefield.mods.morechickens.common.util.ItemUtils;
import cn.evolvefield.mods.morechickens.init.ModBlocks;
import cn.evolvefield.mods.morechickens.init.ModContainers;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class BreederBlock extends HorizontalRotatableBlock  {


    public BreederBlock() {
        super(Properties.of(Material.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.0f,5.0f)
                .noCollission()

        );
        setRegistryName(new ResourceLocation(MoreChickens.MODID, "breeder"));

    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack heldItem = player.getItemInHand(handIn);
        TileEntity tileEntity = worldIn.getBlockEntity(pos);
        if (!(tileEntity instanceof BreederTileEntity)) {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }
        BreederTileEntity breeder = (BreederTileEntity) tileEntity;

        if (!breeder.hasChicken1() && heldItem.getItem() instanceof ChickenItem) {
            breeder.setChicken1(heldItem.copy());
            ItemUtils.decrItemStack(heldItem, player);
            worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        } else if (!breeder.hasChicken2() && heldItem.getItem() instanceof ChickenItem) {
            breeder.setChicken2(heldItem.copy());
            ItemUtils.decrItemStack(heldItem, player);
            worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        } else if (player.isShiftKeyDown() && breeder.hasChicken2()) {
            ItemStack stack = breeder.removeChicken2();
            if (heldItem.isEmpty()) {
                player.setItemInHand(handIn, stack);
            } else {
                if (!player.inventory.add(stack)) {
                    Direction direction = state.getValue(FACING);
                    InventoryHelper.dropItemStack(worldIn, direction.getStepX() + pos.getX() + 0.5D, pos.getY() + 0.5D, direction.getStepZ() + pos.getZ() + 0.5D, stack);
                }
            }
            worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        } else if (player.isShiftKeyDown() && breeder.hasChicken1()) {
            ItemStack stack = breeder.removeChicken1();
            if (heldItem.isEmpty()) {
                player.setItemInHand(handIn, stack);
            } else {
                if (!player.inventory.add(stack)) {
                    Direction direction = state.getValue(FACING);
                    InventoryHelper.dropItemStack(worldIn, direction.getStepX() + pos.getX() + 0.5D, pos.getY() + 0.5D, direction.getStepZ() + pos.getZ() + 0.5D, stack);
                }
            }
            worldIn.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        } else {
            player.openMenu(new INamedContainerProvider() {
                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent(state.getBlock().getDescriptionId());
                }

                @Nullable
                @Override
                public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
                    return new BreederContainer(ModContainers.BREEDER_CONTAINER,id, playerInventory, breeder.getFoodInventory(), breeder.getOutputInventory(),breeder.dataAccess);
                }
            });
            return ActionResultType.SUCCESS;
        }
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
        return BlockRenderType.INVISIBLE;
    }


    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BreederTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getShadeBrightness(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1F;
    }


}

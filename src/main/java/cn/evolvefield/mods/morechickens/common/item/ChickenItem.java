package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.morechickens.common.data.DataChicken;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ChickenItem extends Item {

    private static String I18N_NAME = "entity.Chicken.name";

    public ChickenItem(Properties properties) {
        super(properties
            .stacksTo(16)
        );
    }


    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        DataChicken data = DataChicken.getDataFromStack(itemStack);
        if (data != null) {
            data.addInfoToTooltip(tooltip);
        } else {
            CompoundNBT tag = itemStack.getTag();
            if (tag != null) {
                String chicken = tag.getString(DataChicken.CHICKEN_ID_KEY);
                if (chicken.length() > 0) {
                    tooltip.add(ITextComponent.nullToEmpty("Broken chicken, id = \"" + chicken + "\""));
                }
            }
        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getName(ItemStack stack) {
        DataChicken data = DataChicken.getDataFromStack(stack);
        if (data == null) return ITextComponent.nullToEmpty(I18n.get(I18N_NAME));
        return ITextComponent.nullToEmpty(data.getDisplayName());
    }


    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        Hand hand = context.getHand();
        BlockPos pos = context.getClickedPos().above();
        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);

            if (tileEntity != null && tileEntity instanceof RoostTileEntity) {
                putChickenIn(player.getItemInHand(hand), (RoostTileEntity) tileEntity);
            } else {
                spawnChicken(player.getItemInHand(hand), player, world, pos);
            }
        }

        return ActionResultType.SUCCESS;
    }




    private void putChickenIn(ItemStack stack, RoostTileEntity tileEntity) {
        tileEntity.putChickenIn(stack);
    }

    private void spawnChicken(ItemStack stack, PlayerEntity player, World worldIn, BlockPos pos) {
        DataChicken chickenData = DataChicken.getDataFromStack(stack);
        if (chickenData == null) return;
        chickenData.spawnEntity(worldIn, pos);
        stack.shrink(1);
    }


}

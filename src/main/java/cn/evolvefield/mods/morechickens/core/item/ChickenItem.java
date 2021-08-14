package cn.evolvefield.mods.morechickens.core.item;


import cn.evolvefield.mods.morechickens.core.data.DataChicken;
import cn.evolvefield.mods.morechickens.core.tile.RoostTileEntity;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        DataChicken data = DataChicken.getDataFromStack(itemStack);
        if (data != null) {
            data.addInfoToTooltip(tooltip);
        } else {
            CompoundTag tag = itemStack.getTag();
            if (tag != null) {
                String chicken = tag.getString(DataChicken.CHICKEN_ID_KEY);
                if (chicken.length() > 0) {
                    tooltip.add(Component.nullToEmpty("Broken chicken, id = \"" + chicken + "\""));
                }
            }
        }
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getName(ItemStack stack) {
        DataChicken data = DataChicken.getDataFromStack(stack);
        if (data == null) return Component.nullToEmpty(I18n.get(I18N_NAME));
        return Component.nullToEmpty(data.getDisplayName());
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        InteractionHand hand = context.getHand();
        BlockPos pos = context.getClickedPos().above();
        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);

            if (tileEntity != null && tileEntity instanceof RoostTileEntity) {
                putChickenIn(player.getItemInHand(hand), (RoostTileEntity) tileEntity);
            } else {
                spawnChicken(player.getItemInHand(hand), player, world, pos);
            }
        }

        return InteractionResult.SUCCESS;
    }




    private void putChickenIn(ItemStack stack, RoostTileEntity tileEntity) {
        tileEntity.putChickenIn(stack);
    }

    private void spawnChicken(ItemStack stack, Player player, Level worldIn, BlockPos pos) {
        DataChicken chickenData = DataChicken.getDataFromStack(stack);
        if (chickenData == null) return;
        chickenData.spawnEntity(worldIn, pos);
        stack.shrink(1);
    }


}

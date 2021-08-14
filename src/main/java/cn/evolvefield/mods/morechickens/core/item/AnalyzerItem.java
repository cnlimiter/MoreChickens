package cn.evolvefield.mods.morechickens.core.item;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.core.tile.NestTileEntity;
import cn.evolvefield.mods.morechickens.init.ModItemGroups;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class AnalyzerItem extends Item {
    public AnalyzerItem() {
        super(new Properties()
                        .durability(238)
                        .tab(ModItemGroups.INSTANCE)

                );
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslatableComponent("item.chickens.analyzer.tooltip1"));
        tooltip.add(new TranslatableComponent("item.chickens.analyzer.tooltip2"));
    }


    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        if(playerIn.level.isClientSide)
            return InteractionResult.FAIL;
        if(!(target instanceof BaseChickenEntity))
            return InteractionResult.FAIL;
        BaseChickenEntity quail = (BaseChickenEntity) target;
        BaseChickenEntity.Gene gene = quail.getGene();
        TranslatableComponent name=new TranslatableComponent("text." + MoreChickens.MODID + ".breed." + quail.getBreedName());
        name.getStyle().withColor(ChatFormatting.GOLD);
        playerIn.sendMessage(
                name, Util.NIL_UUID
        );
        playerIn.sendMessage(
                new TranslatableComponent("text." + MoreChickens.MODID + ".stat.amount", gene.layAmount),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslatableComponent("text." + MoreChickens.MODID + ".stat.amountRandom", gene.layRandomAmount),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslatableComponent("text." + MoreChickens.MODID + ".stat.time", gene.layTime),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslatableComponent("text." + MoreChickens.MODID + ".stat.timeRandom", gene.layRandomTime),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslatableComponent("text." + MoreChickens.MODID + ".stat.eggTimer", quail.getLayTimer() / 1200f),
                Util.NIL_UUID);
        return InteractionResult.PASS;

    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        if(world.isClientSide)
            return InteractionResult.FAIL;
        BlockEntity entity = world.getBlockEntity(pos);
        if(!(entity instanceof NestTileEntity))
            return InteractionResult.FAIL;
        NestTileEntity nest = (NestTileEntity)entity;
        nest.printChickens(context.getPlayer());
        return InteractionResult.FAIL;
    }
}

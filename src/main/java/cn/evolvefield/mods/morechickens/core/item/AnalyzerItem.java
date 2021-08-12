package cn.evolvefield.mods.morechickens.core.item;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.core.tile.NestTileEntity;
import cn.evolvefield.mods.morechickens.init.ModItemGroups;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslationTextComponent("item.chickens.analyzer.tooltip1"));
        tooltip.add(new TranslationTextComponent("item.chickens.analyzer.tooltip2"));
    }


    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if(playerIn.level.isClientSide)
            return ActionResultType.FAIL;
        if(!(target instanceof BaseChickenEntity))
            return ActionResultType.FAIL;
        BaseChickenEntity quail = (BaseChickenEntity) target;
        BaseChickenEntity.Gene gene = quail.getGene();
        TranslationTextComponent name=new TranslationTextComponent("text." + MoreChickens.MODID + ".breed." + quail.getBreedName());
        name.getStyle().withColor(TextFormatting.GOLD);
        playerIn.sendMessage(
                name, Util.NIL_UUID
        );
        playerIn.sendMessage(
                new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.amount", gene.layAmount),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.amountRandom", gene.layRandomAmount),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.time", gene.layTime),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.timeRandom", gene.layRandomTime),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.eggTimer", quail.getLayTimer() / 1200f),
                Util.NIL_UUID);
        return ActionResultType.PASS;

    }


    @Override
    public ActionResultType useOn(ItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        if(world.isClientSide)
            return ActionResultType.FAIL;
        TileEntity entity = world.getBlockEntity(pos);
        if(!(entity instanceof NestTileEntity))
            return ActionResultType.FAIL;
        NestTileEntity nest = (NestTileEntity)entity;
        nest.printChickens(context.getPlayer());
        return ActionResultType.FAIL;
    }
}

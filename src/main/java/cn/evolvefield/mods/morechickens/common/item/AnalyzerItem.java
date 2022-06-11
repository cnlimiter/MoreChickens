package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.entity.core.Gene;
import cn.evolvefield.mods.morechickens.init.ModTab;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class AnalyzerItem extends Item {
    public AnalyzerItem() {
        super(new Properties()
                        .durability(238)
                        .tab(ModTab.INSTANCE)

                );
        setRegistryName("analyzer");
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslatableComponent("item.chickens.analyzer.tooltip1"));
        tooltip.add(new TranslatableComponent("item.chickens.analyzer.tooltip2"));
    }


    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player playerIn, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if(playerIn.level.isClientSide)
            return InteractionResult.FAIL;
        if(!(target instanceof BaseChickenEntity chicken))
            return InteractionResult.FAIL;
        Gene gene = chicken.getGene();
        TranslatableComponent name=new TranslatableComponent(chicken.getChickenName());
        name.getStyle().withColor(ChatFormatting.GOLD);
        playerIn.sendMessage(
                name, Util.NIL_UUID
        );
        playerIn.sendMessage(
                new TranslatableComponent("text." + MoreChickens.MODID + ".stat.amount", gene.layCount),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslatableComponent("text." + MoreChickens.MODID + ".stat.amountRandom", gene.layRandomCount),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslatableComponent("text." + MoreChickens.MODID + ".stat.time", gene.layTime),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslatableComponent("text." + MoreChickens.MODID + ".stat.timeRandom", gene.layRandomTime),
                Util.NIL_UUID);
        playerIn.sendMessage(
                new TranslatableComponent("text." + MoreChickens.MODID + ".stat.eggTimer", chicken.getLayTimer() / 1200f),
                Util.NIL_UUID);
        return InteractionResult.PASS;

    }


}

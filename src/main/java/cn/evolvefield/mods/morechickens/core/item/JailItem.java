package cn.evolvefield.mods.morechickens.core.item;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.core.tile.NestTileEntity;
import cn.evolvefield.mods.morechickens.init.ModDefaultEntities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class JailItem extends Item {

    private boolean reusable;

    public JailItem(Properties properties, boolean reusable) {
        super(properties);
        this.reusable = reusable;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        CompoundTag jailTag = stack.getTagElement("Jailed");
        if(jailTag != null || !(target instanceof BaseChickenEntity)){
            return InteractionResult.FAIL;
        }
        BaseChickenEntity chickenEntity = (BaseChickenEntity)target;
        jailTag = new CompoundTag();
        chickenEntity.addAdditionalSaveData(jailTag);
        chickenEntity.remove(Entity.RemovalReason.KILLED);
        ItemStack jailed = new ItemStack(stack.getItem());
        jailed.addTagElement("Jailed", jailTag);
        stack.shrink(1);
        if (!playerIn.addItem(jailed))
            playerIn.drop(jailed, false);
        return stack.isEmpty() ? InteractionResult.PASS : InteractionResult.sidedSuccess(playerIn.level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if(!(world instanceof ServerLevel))
            return InteractionResult.SUCCESS;
        ItemStack itemStack = context.getItemInHand();
        Player player = context.getPlayer();
        if(player == null)
            return InteractionResult.PASS;
        CompoundTag jailTag = itemStack.getTagElement("Jailed");
        BlockEntity tileEntity = world.getBlockEntity(context.getClickedPos());
        if(!(tileEntity instanceof NestTileEntity)) { // When using to release a quail
            if (jailTag == null)
                return InteractionResult.PASS;
            BaseChickenEntity released = (BaseChickenEntity) ModDefaultEntities.BASE_CHICKEN.get().spawn((ServerLevel) world, itemStack, player, context.getClickedPos().relative(context.getClickedFace()), MobSpawnType.SPAWN_EGG, true, false);
            if (released != null)
                released.readAdditionalSaveData(jailTag);
            ItemStack emptied = new ItemStack(itemStack.getItem());
            itemStack.shrink(1);
            if (reusable && !player.isCreative()) {
                emptied.removeTagKey("Jailed");
                if (!player.addItem(emptied))
                    player.drop(emptied, false);
            }
            return itemStack.isEmpty() ? InteractionResult.PASS : InteractionResult.sidedSuccess(player.level.isClientSide);
        }
        else{ // When using on a nest
            NestTileEntity nestEntity = (NestTileEntity)tileEntity;
            if(jailTag == null){ // Withdraw quail
                CompoundTag quail = nestEntity.getChicken();
                if(quail == null) // Empty so just quit
                    return InteractionResult.PASS;
                ItemStack jailed = new ItemStack(itemStack.getItem());
                jailed.addTagElement("Jailed", quail);
                if(!player.isCreative())
                    itemStack.shrink(1);
                if (!player.addItem(jailed))
                    player.drop(jailed, false);
                return itemStack.isEmpty() ? InteractionResult.PASS : InteractionResult.sidedSuccess(player.level.isClientSide);
            }
            else{ // Deposit a quail
                if(jailTag.getInt("Age") < 0) // If a baby
                    return InteractionResult.PASS;
                nestEntity.putChicken(jailTag);
                ItemStack emptied = new ItemStack(itemStack.getItem());
                itemStack.shrink(1);
                if (reusable && !player.isCreative()) {
                    emptied.removeTagKey("Jailed");
                    if (!player.addItem(emptied))
                        player.drop(emptied, false);
                }
                return itemStack.isEmpty() ? InteractionResult.PASS : InteractionResult.sidedSuccess(player.level.isClientSide);
            }
        }
        
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag jailTag = stack.getTagElement("Jailed");
        if(jailTag == null){
            tooltip.add(new TranslatableComponent("text." + MoreChickens.MODID + ".empty"));
        }
        else{
            String breed = jailTag.getString("Breed");
            tooltip.add(new TranslatableComponent("text." + MoreChickens.MODID + ".breed." + breed));
        }
        if(!reusable)
            tooltip.add(new TranslatableComponent("text." + MoreChickens.MODID + ".onetime"));
        else if(jailTag != null){
            CompoundTag a = jailTag.getCompound("AlleleA"), b = jailTag.getCompound("AlleleB");
            CompoundTag gene = a.getFloat("Dominance") >= b.getFloat("Dominance") ? a : b;
            tooltip.add(
                    new TranslatableComponent("text." + MoreChickens.MODID + ".stat.amount", gene.getFloat("LayAmount")));
            tooltip.add(
                    new TranslatableComponent("text." + MoreChickens.MODID + ".stat.amountRandom", gene.getFloat("LayRandomAmount")));
            tooltip.add(
                    new TranslatableComponent("text." + MoreChickens.MODID + ".stat.time", gene.getFloat("LayTime")));
            tooltip.add(
                    new TranslatableComponent("text." + MoreChickens.MODID + ".stat.timeRandom", gene.getFloat("LayRandomTime")));
            tooltip.add(
                    new TranslatableComponent("text." + MoreChickens.MODID + ".stat.eggTimer", jailTag.getInt("EggLayTime") / 1200f));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }


    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getTagElement("Jailed") != null;
    }
}

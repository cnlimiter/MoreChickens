package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.tile.NestTileEntity;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class JailItem extends Item {

    private boolean reusable;

    public JailItem(Properties properties, boolean reusable) {
        super(properties);
        this.reusable = reusable;
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        CompoundNBT jailTag = stack.getTagElement("Jailed");
        if(jailTag != null || !(target instanceof BaseChickenEntity)){
            return ActionResultType.FAIL;
        }
        BaseChickenEntity chickenEntity = (BaseChickenEntity)target;
        jailTag = new CompoundNBT();
        chickenEntity.addAdditionalSaveData(jailTag);
        chickenEntity.remove(false);
        ItemStack jailed = new ItemStack(stack.getItem());
        jailed.addTagElement("Jailed", jailTag);
        stack.shrink(1);
        if (!playerIn.addItem(jailed))
            playerIn.drop(jailed, false);
        return stack.isEmpty() ? ActionResultType.PASS : ActionResultType.sidedSuccess(playerIn.level.isClientSide);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if(!(world instanceof ServerWorld))
            return ActionResultType.SUCCESS;
        ItemStack itemStack = context.getItemInHand();
        PlayerEntity player = context.getPlayer();
        if(player == null)
            return ActionResultType.PASS;
        CompoundNBT jailTag = itemStack.getTagElement("Jailed");
        TileEntity tileEntity = world.getBlockEntity(context.getClickedPos());
        if(!(tileEntity instanceof NestTileEntity)) { // When using to release a quail
            if (jailTag == null)
                return ActionResultType.PASS;
            BaseChickenEntity released = (BaseChickenEntity) ModEntities.BASE_CHICKEN.get().spawn((ServerWorld) world, itemStack, player, context.getClickedPos().relative(context.getClickedFace()), SpawnReason.SPAWN_EGG, true, false);
            if (released != null)
                released.readAdditionalSaveData(jailTag);
            ItemStack emptied = new ItemStack(itemStack.getItem());
            itemStack.shrink(1);
            if (reusable && !player.isCreative()) {
                emptied.removeTagKey("Jailed");
                if (!player.addItem(emptied))
                    player.drop(emptied, false);
            }
            return itemStack.isEmpty() ? ActionResultType.PASS : ActionResultType.sidedSuccess(player.level.isClientSide);
        }
        else{ // When using on a nest
            NestTileEntity nestEntity = (NestTileEntity)tileEntity;
            if(jailTag == null){ // Withdraw quail
                CompoundNBT quail = nestEntity.getChicken();
                if(quail == null) // Empty so just quit
                    return ActionResultType.PASS;
                ItemStack jailed = new ItemStack(itemStack.getItem());
                jailed.addTagElement("Jailed", quail);
                if(!player.isCreative())
                    itemStack.shrink(1);
                if (!player.addItem(jailed))
                    player.drop(jailed, false);
                return itemStack.isEmpty() ? ActionResultType.PASS : ActionResultType.sidedSuccess(player.level.isClientSide);
            }
            else{ // Deposit a quail
                if(jailTag.getInt("Age") < 0) // If a baby
                    return ActionResultType.PASS;
                nestEntity.putChicken(jailTag);
                ItemStack emptied = new ItemStack(itemStack.getItem());
                itemStack.shrink(1);
                if (reusable && !player.isCreative()) {
                    emptied.removeTagKey("Jailed");
                    if (!player.addItem(emptied))
                        player.drop(emptied, false);
                }
                return itemStack.isEmpty() ? ActionResultType.PASS : ActionResultType.sidedSuccess(player.level.isClientSide);
            }
        }
        
    }

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT jailTag = stack.getTagElement("Jailed");
        if(jailTag == null){
            tooltip.add(new TranslationTextComponent("text." + MoreChickens.MODID + ".empty"));
        }
        else{
            String breed = jailTag.getString("Breed");
            tooltip.add(new TranslationTextComponent("text." + MoreChickens.MODID + ".breed." + breed));
        }
        if(!reusable)
            tooltip.add(new TranslationTextComponent("text." + MoreChickens.MODID + ".onetime"));
        else if(jailTag != null){
            CompoundNBT a = jailTag.getCompound("AlleleA"), b = jailTag.getCompound("AlleleB");
            CompoundNBT gene = a.getFloat("Dominance") >= b.getFloat("Dominance") ? a : b;
            tooltip.add(
                    new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.amount", gene.getFloat("LayAmount")));
            tooltip.add(
                    new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.amountRandom", gene.getFloat("LayRandomAmount")));
            tooltip.add(
                    new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.time", gene.getFloat("LayTime")));
            tooltip.add(
                    new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.timeRandom", gene.getFloat("LayRandomTime")));
            tooltip.add(
                    new TranslationTextComponent("text." + MoreChickens.MODID + ".stat.eggTimer", jailTag.getInt("EggLayTime") / 1200f));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }


    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getTagElement("Jailed") != null;
    }
}

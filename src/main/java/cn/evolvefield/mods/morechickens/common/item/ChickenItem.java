package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;

import cn.evolvefield.mods.morechickens.common.util.main.Gene;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import cn.evolvefield.mods.morechickens.init.ModItemGroups;
import cn.evolvefield.mods.morechickens.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ChickenItem extends Item {
    public ChickenItem() {
        super(new Properties().craftRemainder(Items.BUCKET)
                .stacksTo(1)
                .tab(ModItemGroups.INSTANCE)
        );
        setRegistryName("chicken");
    }


    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getName(ItemStack stack) {
        World world = Minecraft.getInstance().level;
        if (world == null) {
            return super.getName(stack);
        } else {

            String name = stack.getOrCreateTag().getString("Name");
            return  new TranslationTextComponent("text.chickens.name."+ name);
        }
    }


    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        Hand hand = context.getHand();
        BlockPos pos = context.getClickedPos().above();
        ItemStack chickenItem = player.getItemInHand(hand);
        String typeTag = chickenItem.getOrCreateTag().getString("Type");
        CompoundNBT chickenTag = chickenItem.getTagElement("ChickenData");

        if (!world.isClientSide) {
                if(typeTag.equals("vanilla")){
                    ChickenEntity chicken =  new ChickenEntity(EntityType.CHICKEN,world);
                    chicken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    chicken.finalizeSpawn(world.getServer().overworld(),world.getCurrentDifficultyAt(pos), SpawnReason.SPAWN_EGG,null,null);
                    world.getServer().overworld().addFreshEntity(chicken);
                    chickenItem.shrink(1);
                }
                else if(typeTag.equals("modded")){
                    CompoundNBT nbt = chickenItem.getTag();
                    if (nbt == null)
                        return ActionResultType.PASS;
                    BaseChickenEntity chicken = new BaseChickenEntity(ModEntities.BASE_CHICKEN.get(),world);
                    chicken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    chicken.finalizeSpawn(world.getServer().overworld(),world.getCurrentDifficultyAt(pos), SpawnReason.SPAWN_EGG,null,null);
                    chicken.readAdditionalSaveData(nbt);
                    world.getServer().overworld().addFreshEntity(chicken);

                    chickenItem.shrink(1);
                }
        }

        return ActionResultType.SUCCESS;
    }




}

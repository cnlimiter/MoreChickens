package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;

import cn.evolvefield.mods.morechickens.init.ModEntities;
import cn.evolvefield.mods.morechickens.init.ModItemGroups;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ChickenItem extends Item {

    public ChickenItem() {
        super(new Properties().craftRemainder(Items.BUCKET)
                .stacksTo(1)
                .tab(ModItemGroups.INSTANCE)
        );
        setRegistryName("chicken");
    }


    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {

    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getName(ItemStack stack) {
        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return super.getName(stack);
        } else {

            String name = stack.getOrCreateTag().getString("Name");
            return  new TranslatableComponent("text.chickens.name."+ name);
        }
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        InteractionHand hand = context.getHand();
        BlockPos pos = context.getClickedPos().above();
        ItemStack chickenItem = player.getItemInHand(hand);
        String typeTag = chickenItem.getOrCreateTag().getString("Type");
        CompoundTag chickenTag = chickenItem.getTagElement("ChickenData");

        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);

            if(typeTag.equals("vanilla")){
                Chicken chicken =  new Chicken(EntityType.CHICKEN,world);
                chicken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                chicken.finalizeSpawn(world.getServer().overworld(),world.getCurrentDifficultyAt(pos), MobSpawnType.SPAWN_EGG,null,null);
                world.getServer().overworld().addFreshEntity(chicken);
                chickenItem.shrink(1);
            }
            else if(typeTag.equals("modded")){
                CompoundTag nbt = chickenItem.getTag();
                if (nbt == null)
                    return InteractionResult.PASS;
                BaseChickenEntity chicken = new BaseChickenEntity(ModEntities.BASE_CHICKEN.get(),world);
                chicken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                chicken.finalizeSpawn(world.getServer().overworld(),world.getCurrentDifficultyAt(pos), MobSpawnType.SPAWN_EGG,null,null);
                chicken.readAdditionalSaveData(nbt);
                world.getServer().overworld().addFreshEntity(chicken);

                chickenItem.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }




}

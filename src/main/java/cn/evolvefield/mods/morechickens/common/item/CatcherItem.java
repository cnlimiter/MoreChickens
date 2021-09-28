package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.init.ModItemGroups;
import cn.evolvefield.mods.morechickens.init.ModItems;
import com.sun.javafx.geom.Vec3d;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CatcherItem extends Item {

    public CatcherItem() {
        super(new Properties()
                .stacksTo(1)
                .durability(238)
                .craftRemainder(Items.BUCKET)
                .tab(ModItemGroups.INSTANCE)

        );
        setRegistryName("catcher");

    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslationTextComponent("item.chickens.catcher.tooltip1"));

    }


    @Override
    public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity player, LivingEntity entity, Hand hand) {
        World world = entity.level;
        Vec3d pos = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
        if(entity instanceof ChickenEntity) {
            if (world.isClientSide) {
                spawnParticles(pos, world, ParticleTypes.EXPLOSION);
            } else {
                ItemStack chickenItem = new ItemStack(ModItems.ITEM_CHICKEN);
                CompoundNBT tagCompound = chickenItem.getOrCreateTagElement("ChickenData");
                tagCompound.putString("Type", "vanilla");
                chickenItem.setTag(tagCompound);
                ItemEntity item = entity.spawnAtLocation(chickenItem, 1.0F);
                item.setDeltaMovement(0, 0.2D, 0);
                entity.getServer().overworld().removeEntity(entity,false);
            }
            world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.CHICKEN_EGG, entity.getSoundSource(), 1.0F, 1.0F);
            return ActionResultType.SUCCESS;
        }
        else if (entity instanceof BaseChickenEntity) {

                BaseChickenEntity chickenEntity = (BaseChickenEntity)entity;

                if (entity.isBaby()) {
                    if (world.isClientSide) {
                        spawnParticles(pos, world, ParticleTypes.SMOKE);
                    }
                    world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.CHICKEN_HURT, entity.getSoundSource(), 1.0F, 1.0F);
                    itemStack.hurtAndBreak(1, player, LivingEntity::animateHurt);
                } else {
                    if (world.isClientSide) {
                        spawnParticles(pos, world, ParticleTypes.EXPLOSION);
                    } else {
                        ItemStack chickenItem = new ItemStack(ModItems.ITEM_CHICKEN);
                        CompoundNBT tagCompound = chickenItem.getOrCreateTagElement("ChickenData");
                        chickenEntity.addAdditionalSaveData(tagCompound);
                        chickenEntity.remove(false);
                        tagCompound.putString("Type", "modded");
                        chickenItem.setTag(tagCompound);
                        ItemEntity item = entity.spawnAtLocation(chickenItem, 1.0F);
                        item.setDeltaMovement(0,0.2d,0);
                    }
                    world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.CHICKEN_EGG, entity.getSoundSource(), 1.0F, 1.0F);
                }
                return ActionResultType.SUCCESS;
            }

        player.sendMessage(new TranslationTextComponent("item.chickens.catcher.fail"), Util.NIL_UUID);
        return ActionResultType.FAIL;

    }


    private void spawnParticles(Vec3d pos, World world, IParticleData data) {
        Random rand = new Random();
        for (int k = 0; k < 20; ++k) {
            double xCoord = pos.x + (rand.nextDouble() * 0.6D) - 0.3D;
            double yCoord = pos.y + (rand.nextDouble() * 0.6D);
            double zCoord = pos.z + (rand.nextDouble() * 0.6D) - 0.3D;
            double xSpeed = rand.nextGaussian() * 0.02D;
            double ySpeed = rand.nextGaussian() * 0.2D;
            double zSpeed = rand.nextGaussian() * 0.02D;
            world.addParticle(data, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
        }
    }


}

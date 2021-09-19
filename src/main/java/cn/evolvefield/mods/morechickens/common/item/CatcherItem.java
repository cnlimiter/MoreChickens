package cn.evolvefield.mods.morechickens.common.item;



import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.init.ModItemGroups;
import cn.evolvefield.mods.morechickens.init.ModItems;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslatableComponent("item.chickens.catcher.tooltip1"));

    }


    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity entity, InteractionHand hand) {
        Level world = entity.level;
        Vector3f pos = new Vector3f((float) entity.getX(),(float) entity.getY(), (float)entity.getZ());
        if(entity instanceof Chicken) {
            if (world.isClientSide) {
                spawnParticles(pos, world, ParticleTypes.EXPLOSION);
            } else {
                ItemStack chickenItem = new ItemStack(ModItems.ITEM_CHICKEN);
                CompoundTag tagCompound = chickenItem.getOrCreateTagElement("ChickenData");
                tagCompound.putString("Type", "vanilla");
                chickenItem.setTag(tagCompound);
                ItemEntity item = entity.spawnAtLocation(chickenItem, 1.0F);
                item.lerpMotion(0, 0.2D, 0);
                entity.getServer().overworld().removeEntity(entity,false);
            }
            world.playSound(player, pos.x(), pos.y(), pos.z(), SoundEvents.CHICKEN_EGG, entity.getSoundSource(), 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        else if (entity instanceof BaseChickenEntity) {

            BaseChickenEntity chickenEntity = (BaseChickenEntity)entity;

            if (entity.isBaby()) {
                if (world.isClientSide) {
                    spawnParticles(pos, world, ParticleTypes.SMOKE);
                }
                world.playSound(player, pos.x(), pos.y(), pos.z(), SoundEvents.CHICKEN_HURT, entity.getSoundSource(), 1.0F, 1.0F);
                itemStack.hurtAndBreak(1, player, (playerIn) -> {
                    playerIn.animateHurt();
                });
            } else {
                if (world.isClientSide) {
                    spawnParticles(pos, world, ParticleTypes.EXPLOSION);
                } else {
                    ItemStack chickenItem = new ItemStack(ModItems.ITEM_CHICKEN);
                    CompoundTag tagCompound = chickenItem.getOrCreateTagElement("ChickenData");
                    chickenEntity.addAdditionalSaveData(tagCompound);
                    chickenEntity.remove(null);
                    tagCompound.putString("Type", "modded");
                    chickenItem.setTag(tagCompound);
                    ItemEntity item = entity.spawnAtLocation(chickenItem, 1.0F);
                    item.lerpMotion(0, 0.2D, 0);
                }
                world.playSound(player, pos.x(), pos.y(), pos.z(), SoundEvents.CHICKEN_EGG, entity.getSoundSource(), 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        player.sendMessage(new TranslatableComponent("item.chickens.catcher.fail"), Util.NIL_UUID);
        return InteractionResult.FAIL;
    }


    private void spawnParticles(Vector3f pos, Level world, ParticleOptions data) {
        Random rand = new Random();

        for (int k = 0; k < 20; ++k) {
            double xCoord = pos.x() + (rand.nextDouble() * 0.6D) - 0.3D;
            double yCoord = pos.y() + (rand.nextDouble() * 0.6D);
            double zCoord = pos.z() + (rand.nextDouble() * 0.6D) - 0.3D;
            double xSpeed = rand.nextGaussian() * 0.02D;
            double ySpeed = rand.nextGaussian() * 0.2D;
            double zSpeed = rand.nextGaussian() * 0.02D;
            world.addParticle(data, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
        }
    }


}

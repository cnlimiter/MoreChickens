package cn.evolvefield.mods.morechickens.core.item;



import cn.evolvefield.mods.morechickens.core.data.DataChicken;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CatcherItem extends Item {
    public CatcherItem(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(238)

        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslatableComponent("item.chickens.catcher.tooltip1"));

    }


    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity entity, InteractionHand hand) {
        DataChicken chickenData = DataChicken.getDataFromEntity(entity);
        Vec3 pos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        Level world = entity.level;

        if (chickenData == null) {
            return InteractionResult.FAIL;
        } else if (entity.isBaby()) {
            if (world.isClientSide) {
                spawnParticles(pos, world, ParticleTypes.SMOKE);
            }
            world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.CHICKEN_HURT, entity.getSoundSource(), 1.0F, 1.0F);
        } else {
            if (world.isClientSide) {
                spawnParticles(pos, world, ParticleTypes.EXPLOSION);
            } else {
                ItemEntity item = entity.spawnAtLocation(chickenData.buildChickenStack(), 1.0F);
                item.lerpMotion(0,0.2D,0);
                entity.getServer().overworld().removeEntity(entity);
            }
            world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.CHICKEN_EGG, entity.getSoundSource(), 1.0F, 1.0F);
            itemStack.hurtAndBreak(1, player,(playerin) -> {
                        playerin.animateHurt();
            });
        }


        return InteractionResult.SUCCESS;
    }


    private void spawnParticles(Vec3 pos, Level world, ParticleOptions data) {
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

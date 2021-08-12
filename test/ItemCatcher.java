package cn.evolvefield.mods.morechickens.core.item;



import cn.evolvefield.mods.morechickens.core.data.DataChicken;
import com.sun.javafx.geom.Vec3d;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ItemCatcher extends Item {
    public ItemCatcher(Properties properties) {
        super(properties
                .stacksTo(1)
                .durability(238)

        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslationTextComponent("item.chickens.catcher.tooltip1"));

    }


    @Override
    public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity player, LivingEntity entity, Hand hand) {
        DataChicken chickenData = DataChicken.getDataFromEntity(entity);
        Vec3d pos = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
        World world = entity.level;

        if (chickenData == null) {
            return ActionResultType.FAIL;
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
                entity.getServer().overworld().onEntityRemoved(entity);
            }
            world.playSound(player, pos.x, pos.y, pos.z, SoundEvents.CHICKEN_EGG, entity.getSoundSource(), 1.0F, 1.0F);
            itemStack.hurtAndBreak(1, player,(playerin) -> {
                        playerin.animateHurt();
            });
        }


        return ActionResultType.SUCCESS;
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

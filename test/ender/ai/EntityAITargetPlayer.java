package cn.evolvefield.mods.morechickens.core.entity.ai;

import cn.evolvefield.mods.morechickens.core.entity.EnderChickenEntity;
import com.google.common.base.Function;
import net.minecraft.entity.ai.goal.Goal;

public class EntityAITargetPlayer extends Goal {
    public EnderChickenEntity chicken;
    public int targetCooldown;

    public EntityAITargetPlayer(EnderChickenEntity chicken) {
        this.chicken = chicken;
        this.setMutexBits(1);
    }

    @Override
    public boolean canUse() {
        EntityPlayer player = this.chicken.world.getNearestAttackablePlayer(this.chicken.posX, this.chicken.posY, this.chicken.posZ, (double)(15.0F * this.chicken.getScale()), (double)(15.0F * this.chicken.getScale()), (Function)null, (input) -> {
            return input != null && input.ticksExisted > 100;
        });
        if (player != null && (this.chicken.getRevengeTarget() == null || !this.chicken.getRevengeTarget().isEntityAlive())) {
            this.chicken.setAttackTarget(player);
        }

        return player != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetCooldown > 0;
    }

    @Override
    public void start() {
        this.targetCooldown = 20;
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        --this.targetCooldown;
    }
}

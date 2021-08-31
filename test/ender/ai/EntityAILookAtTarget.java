package cn.evolvefield.mods.morechickens.core.entity.ai;

import cn.evolvefield.mods.morechickens.core.entity.EnderChickenEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;

public class EntityAILookAtTarget extends Goal {

    public EnderChickenEntity chicken;

    public EntityAILookAtTarget(EnderChickenEntity chicken) {
        this.chicken = chicken;
        this.setMutexBits(2);
    }
    @Override
    public boolean canUse() {
        return this.chicken.isHeadAvailable() && this.chicken.getAttackTarget() != null && !this.chicken.getFiring() && !this.chicken.getCharging() && !this.chicken.getSpinning() && this.chicken.isEntityAlive();

    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        Entity ent = this.chicken.getAttackTarget();
        if (ent != null) {
            this.chicken.getLookHelper().setLookPosition(ent.posX, ent.posY + (double)ent.getEyeHeight(), ent.posZ, (float)this.chicken.getHorizontalFaceSpeed(), (float)this.chicken.getVerticalFaceSpeed());
        }
    }
}

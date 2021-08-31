package cn.evolvefield.mods.morechickens.core.entity.ai;

import cn.evolvefield.mods.morechickens.core.entity.EnderChickenEntity;
import net.minecraft.entity.ai.goal.Goal;

public class EntityAIClearArea extends Goal {
    public EnderChickenEntity chicken;
    public float chance;
    public int clearingAreaTime;

    public EntityAIClearArea(EnderChickenEntity chicken, float chance) {
        this.chicken = chicken;
        this.chance = chance;
        this.setMutexBits(3);
    }
    @Override
    public boolean canUse() {
        return this.chicken.canUseAbility() && this.chicken.getEggState() < 0 && this.chicken.getRNG().nextFloat() < this.chance && this.chicken.shouldClearArea();

    }

    @Override
    public boolean canContinueToUse() {
        return this.chicken.getClearingArea() && this.chicken.isEntityAlive();
    }

    @Override
    public void start() {
        this.chicken.useAbility();
        this.chicken.setClearingArea(true);
        this.clearingAreaTime = 0;
    }

    @Override
    public void stop() {
        this.chicken.endAbility();
        this.clearingAreaTime = 0;
    }

    @Override
    public void tick() {
        if (!this.chicken.getFiring()) {
            this.chicken.rotationPitch = 20.0F;
        }

        ++this.clearingAreaTime;
        if (this.clearingAreaTime == 1) {
            this.chicken.playSound(SoundIndex.clear_warn, 1.0F + 0.2F * this.chicken.getScale(), 0.8F + this.chicken.getRNG().nextFloat() * 0.4F);
        } else if (this.clearingAreaTime == 40) {
            this.chicken.explode(this.chicken.partHead.posX, this.chicken.partHead.posY, this.chicken.partHead.posZ, 3.75F * this.chicken.getScale(), false, true);
            this.chicken.explode(this.chicken.posX, this.chicken.posY, this.chicken.posZ, 8.5F * this.chicken.getScale(), false, true);
        } else if (this.clearingAreaTime >= 55) {
            this.chicken.clearArea = false;
            this.chicken.setClearingArea(false);
        }
    }
}

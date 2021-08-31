package cn.evolvefield.mods.morechickens.core.entity.ai;

import cn.evolvefield.mods.morechickens.core.entity.EnderChickenEntity;
import net.minecraft.entity.ai.goal.Goal;

public class EntityAIBreakEgg extends Goal {

    public EnderChickenEntity chicken;
    public float chance;
    public int breakCooldown;
    public EntityAIBreakEgg(EnderChickenEntity chicken, float chance) {
        this.chicken = chicken;
        this.chance = chance;
        this.setMutexBits(4);
    }

    @Override
    public boolean canUse() {
        return this.chicken.canUseAbility() && this.chicken.getEggState() >= 0 && this.chicken.getRNG().nextFloat() < this.chance;

    }

    @Override
    public void start() {
        this.chicken.useAbility();
        this.chicken.breakEgg();
        this.breakCooldown = 60;
    }

    @Override
    public void stop() {
        this.chicken.endAbility();
    }

    @Override
    public boolean canContinueToUse() {
        return this.breakCooldown > 0;
    }

    @Override
    public void tick() {
        --this.breakCooldown;
    }
}

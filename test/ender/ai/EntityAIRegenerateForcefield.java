package cn.evolvefield.mods.morechickens.core.entity.ai;

import cn.evolvefield.mods.morechickens.core.entity.EnderChickenEntity;
import net.minecraft.entity.ai.goal.Goal;

public class EntityAIRegenerateForcefield extends Goal {

    public EnderChickenEntity chicken;
    public float chance;
    public int breakCooldown;

    public EntityAIRegenerateForcefield(EnderChickenEntity chicken, float chance) {
        this.chicken = chicken;
        this.chance = chance;
        this.setMutexBits(0);
    }
    @Override
    public boolean canUse() {
        return this.chicken.getEggState() < 0 && !this.chicken.getForcefield() && this.chicken.isEntityAlive() && this.chicken.getRNG().nextFloat() < this.chance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.breakCooldown > 0;
    }

    @Override
    public void start() {
        this.breakCooldown = MightyEnderChicken.config.regenerateForcefieldCooldown;
    }

    @Override
    public void stop() {
        this.chicken.setForcefield(true);
    }

    @Override
    public void tick() {
        --this.breakCooldown;
    }
}

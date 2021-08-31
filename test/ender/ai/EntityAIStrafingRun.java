package cn.evolvefield.mods.morechickens.core.entity.ai;

import cn.evolvefield.mods.morechickens.core.entity.EnderChickenEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;

public class EntityAIStrafingRun extends Goal {

    public EnderChickenEntity chicken;
    public float chance;
    public int strafingRunTime;
    public int eggCooldown;

    public EntityAIStrafingRun(EnderChickenEntity chicken, float chance) {
        this.chicken = chicken;
        this.chance = chance;
        this.setMutexBits(0);
    }

    @Override
    public boolean canUse() {
        if (this.chicken.getEggState() < 0 && this.chicken.maxStrafingRunTime <= 0) {
            EntityLivingBase target = this.chicken.getAttackTarget();
            if (target != null && target.isEntityAlive()) {
                return this.chicken.canUseAbility() && this.chicken.getRNG().nextFloat() < this.chance;
            } else {
                return false;
            }
        } else {
            --this.chicken.maxStrafingRunTime;
            return false;
        }    }

    @Override
    public boolean canContinueToUse() {
        return this.strafingRunTime < this.chicken.maxStrafingRunTime && this.chicken.isEntityAlive();
    }

    @Override
    public void start() {
        this.chicken.useAbility();
        int min = Math.min(MightyEnderChicken.config.strafingRunMinTime, MightyEnderChicken.config.strafingRunMaxTime);
        int max = Math.max(MightyEnderChicken.config.strafingRunMinTime, MightyEnderChicken.config.strafingRunMaxTime);
        this.chicken.maxStrafingRunTime = min + this.chicken.getRNG().nextInt(max - min);

    }

    @Override
    public void stop() {
        this.chicken.endAbility();
        this.chicken.maxStrafingRunTime = this.strafingRunTime = 0;
    }

    @Override
    public void tick() {
        ++this.strafingRunTime;
        --this.eggCooldown;
        if (this.strafingRunTime < this.chicken.maxStrafingRunTime && this.eggCooldown <= 0 && this.chicken.getRNG().nextFloat() < (float)MightyEnderChicken.config.strafingRunEggChance / 100.0F) {
            float scale = this.chicken.getScale();
            Vec3d buttPos = this.chicken.getHeadPos(-0.5D, 0.02D);
            EntityEggBomb egg = new EntityEggBomb(this.chicken.world, scale, this.chicken.getAttackTarget(), this.chicken.getRNG().nextDouble() < (double)((float)MightyEnderChicken.config.strafingRunEggAimbot / 100.0F), this.chicken.getIsChaos());
            egg.setLocationAndAngles(buttPos.x, buttPos.y, buttPos.z, this.chicken.renderYawOffset - 180.0F, 0.0F);
            double x = (double)(-MathHelper.sin((this.chicken.renderYawOffset + 180.0F) * 0.017453292F));
            double z = (double)MathHelper.cos((this.chicken.renderYawOffset + 180.0F) * 0.017453292F);
            float f1 = MathHelper.sqrt(x * x + z * z);
            x /= (double)f1;
            z /= (double)f1;
            x = x * 0.09D * (double)scale;
            z = z * 0.09D * (double)scale;
            egg.motionX = x + this.chicken.motionX;
            egg.motionZ = z + this.chicken.motionZ;
            this.eggCooldown = 5;
            this.chicken.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F + 0.2F * scale, (this.chicken.getRNG().nextFloat() - this.chicken.getRNG().nextFloat()) * 0.2F + 1.0F);
            this.chicken.world.spawnEntity(egg);
        }
    }
}

package cn.evolvefield.mods.morechickens.core.entity.ai;

import cn.evolvefield.mods.morechickens.core.entity.EnderChickenEntity;
import com.sun.javafx.geom.Vec3d;
import net.minecraft.entity.ai.goal.Goal;

public class EntityAILaser extends Goal {

    public EnderChickenEntity chicken;
    public float chance;
    public Vec3d targetStart;
    public Vec3d lookVector;

    public EntityAILaser(EnderChickenEntity chicken, float chance) {
        this.chicken = chicken;
        this.chance = chance;
        this.setMutexBits(0);
    }
    @Override
    public boolean canUse() {
        if (!this.chicken.getFiring() && this.chicken.firingProgress == 0) {
            float currentChance = this.chance;
            if (this.chicken.isHeadAvailable()) {
                EntityLivingBase target = this.chicken.getAttackTarget();
                if (target == null || !target.isEntityAlive()) {
                    return false;
                }

                if (target.posY > this.chicken.partBody.posY + (double)this.chicken.partBody.height) {
                    currentChance *= 2.5F;
                }
            }

            return this.chicken.canUseAbility() && this.chicken.getRNG().nextFloat() < currentChance;
        } else {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.chicken.getFiring() && this.chicken.isEntityAlive();
    }

    @Override
    public void start() {
        this.chicken.useAbility();
        this.chicken.setFiring(true);
    }

    @Override
    public void stop() {
        this.chicken.endAbility();
        this.targetStart = null;
        this.lookVector = null;
    }

    @Override
    public void tick() {
        EntityLivingBase target;
        double regress;
        if (this.chicken.firingProgress == 1) {
            if (this.chicken.isHeadAvailable()) {
                target = this.chicken.getAttackTarget();
                if (target != null) {
                    this.targetStart = target.getPositionVector().add(0.0D, (double)(target.height / 2.0F), 0.0D);
                }
            }
        } else if (this.chicken.firingProgress < 59) {
            if (this.chicken.isHeadAvailable() && this.targetStart != null) {
                this.chicken.getLookHelper().setLookPosition(this.targetStart.x, this.targetStart.y, this.targetStart.z, 2.0F, 3.0F);
            }
        } else if (this.chicken.firingProgress == 59) {
            double maxRange = 5.0D;
            Vec3d targetEnd = null;
            if (this.chicken.isHeadAvailable()) {
                EntityLivingBase target = this.chicken.getAttackTarget();
                if (target != null) {
                    targetEnd = target.getPositionVector().add(0.0D, (double)(target.height / 2.0F), 0.0D);
                    double dist = (double)this.chicken.getDistance(target);
                    if (dist < maxRange) {
                        maxRange = dist;
                    }
                }
            }

            boolean startNull = this.targetStart == null;
            if (startNull) {
                if (targetEnd != null) {
                    this.targetStart = targetEnd.add(this.chicken.getRNG().nextGaussian() * 8.0D, this.chicken.getRNG().nextGaussian() * 8.0D, this.chicken.getRNG().nextGaussian() * 8.0D);
                } else {
                    this.targetStart = this.chicken.getPositionVector().add(this.chicken.getRNG().nextGaussian() * 8.0D, this.chicken.getRNG().nextGaussian() * 8.0D, this.chicken.getRNG().nextGaussian() * 8.0D);
                }
            }

            if (targetEnd == null) {
                if (!startNull) {
                    targetEnd = this.targetStart.add(this.chicken.getRNG().nextGaussian() * 8.0D, this.chicken.getRNG().nextGaussian() * 8.0D, this.chicken.getRNG().nextGaussian() * 8.0D);
                } else {
                    targetEnd = this.chicken.getPositionVector().add(this.chicken.getRNG().nextGaussian() * 8.0D, this.chicken.getRNG().nextGaussian() * 8.0D, this.chicken.getRNG().nextGaussian() * 8.0D);
                }
            }

            if (targetEnd.equals(this.targetStart)) {
                targetEnd = targetEnd.add(this.chicken.getRNG().nextGaussian() * 8.0D, this.chicken.getRNG().nextGaussian() * 8.0D, this.chicken.getRNG().nextGaussian() * 8.0D);
            }

            Vec3d normVec = targetEnd.subtract(this.targetStart).normalize();
            regress = this.chicken.getRNG().nextDouble() * maxRange;
            this.targetStart = this.targetStart.subtract(normVec.x * regress, normVec.y * regress, normVec.z * regress);
            double progress = this.chicken.getRNG().nextDouble() * maxRange;
            targetEnd = targetEnd.add(normVec.x * progress, normVec.y * progress, normVec.z * progress);
            this.lookVector = targetEnd.subtract(this.targetStart);
            this.targetStart = this.targetStart.subtract(this.chicken.getPositionVector());
        }

        if (this.chicken.firingProgress >= 59 && this.chicken.firingProgress < 160) {
            if (this.chicken.isHeadAvailable() && this.chicken.getAttackTarget() != null) {
                double var10001 = this.chicken.partHead.posY + (double)this.chicken.partHead.height;
                if (this.chicken.getAttackTarget().posY > var10001) {
                    target = this.chicken.getAttackTarget();
                    this.chicken.getLookHelper().setLookPosition(target.posX, target.posY + (target.getEntityBoundingBox().maxY - target.getEntityBoundingBox().minY) / 2.0D, target.posZ, (float)this.chicken.getHorizontalFaceSpeed(), (float)this.chicken.getVerticalFaceSpeed());
                    return;
                }
            }

            float prog = (float)(this.chicken.firingProgress - 60) / 100.0F;
            double x = this.chicken.posX + this.targetStart.x + this.lookVector.x * (double)prog;
            double y = this.chicken.posY + this.targetStart.y + this.lookVector.y * (double)prog;
            regress = this.chicken.posZ + this.targetStart.z + this.lookVector.z * (double)prog;
            this.chicken.getLookHelper().setLookPosition(x, y, regress, (float)this.chicken.getHorizontalFaceSpeed(), (float)this.chicken.getVerticalFaceSpeed());
        }
    }
}

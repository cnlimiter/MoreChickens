package cn.evolvefield.mods.morechickens.core.entity.ai;

import cn.evolvefield.mods.morechickens.core.entity.EnderChickenEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.HashSet;
import java.util.Iterator;

public class EntityAIForcefieldRetaliation extends Goal {
    public EnderChickenEntity chicken;
    public int attackTime;
    public BlockPos chargeStart;
    public float chargeYaw;
    public double chargeLastDist;
    public double lastX;
    public double lastZ;

    public EntityAIForcefieldRetaliation(EnderChickenEntity chicken) {
        this.chicken = chicken;
        this.setMutexBits(3);
    }
    @Override
    public boolean canUse() {
        return this.chicken.canUseAbility() && this.chicken.forcefieldAttacker != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.chicken.forcefieldAttacker != null && this.chicken.isEntityAlive();
    }

    @Override
    public void start() {
        this.chicken.useAbility();
        this.attackTime = -5;
        this.chargeLastDist = -1.0D;
        this.chicken.setFlapping(true);
        this.chicken.setAttackTarget(this.chicken.forcefieldAttacker);
    }

    @Override
    public void stop() {
        this.chicken.endAbility();
        this.chicken.setFlapping(false);
        this.chargeStart = null;
        this.chargeYaw = 0.0F;
        this.attackTime = 0;
    }

    @Override
    public void tick() {
        ++this.attackTime;
        if (this.attackTime >= 0) {
            if (this.attackTime < 100) {
                this.chicken.getMoveHelper().setMoveTo(this.chicken.forcefieldAttacker.posX, this.chicken.forcefieldAttacker.posY, this.chicken.forcefieldAttacker.posZ, 1.2D);
                double dist = (double)this.chicken.getDistance(this.chicken.forcefieldAttacker) - (double)this.chicken.width * 0.75D;
                if (dist < 3.0D) {
                    this.chicken.peck();
                }

                if (!this.chicken.isPecking) {
                    this.chicken.getLookHelper().setLookPosition(this.chicken.forcefieldAttacker.posX, this.chicken.forcefieldAttacker.posY + (double)this.chicken.forcefieldAttacker.getEyeHeight(), this.chicken.forcefieldAttacker.posZ, (float)this.chicken.getHorizontalFaceSpeed(), (float)this.chicken.getVerticalFaceSpeed());
                }
            } else {
                float scale = this.chicken.getScale();
                double speed = 0.41D + 0.044D * (double)scale;
                if (this.chicken.getIsChaos()) {
                    speed *= 1.1D;
                }

                double x = (double)(-MathHelper.sin((this.chargeStart != null ? this.chargeYaw : this.chicken.renderYawOffset + 180.0F) * 0.017453292F));
                double z = (double)MathHelper.cos((this.chargeStart != null ? this.chargeYaw : this.chicken.renderYawOffset + 180.0F) * 0.017453292F);
                float f1 = MathHelper.sqrt(x * x + z * z);
                x /= (double)f1;
                z /= (double)f1;
                x *= speed;
                z *= speed;
                this.chicken.motionX = x;
                this.chicken.motionZ = z;
                if (this.attackTime == 100) {
                    this.chicken.setCharging(true);
                    if (this.chicken.isHeadAvailable()) {
                        this.chicken.playSound(SoundIndex.charge_start, 1.0F + 0.2F * scale, 1.0F);
                    }

                    this.chargeStart = new BlockPos(this.chicken);
                    this.chargeYaw = this.chicken.renderYawOffset + 180.0F;
                    this.lastX = this.chicken.posX;
                    this.lastZ = this.chicken.posZ;
                } else if (this.attackTime > 100) {
                    this.chicken.rotationYaw = this.chicken.rotationYawHead = this.chicken.renderYawOffset = this.chargeYaw;
                    this.chicken.rotationPitch = -10.0F;
                    double diffX = this.chicken.posX - this.lastX;
                    double diffZ = this.chicken.posZ - this.lastZ;
                    double newDist = Math.sqrt(diffX * diffX + diffZ * diffZ);
                    if (newDist > this.chargeLastDist) {
                        this.chargeLastDist = newDist;
                    } else if (newDist / this.chargeLastDist < 0.8D) {
                        if (this.chicken.getEggState() < 0 && this.chicken.getDistanceSq(this.chargeStart) < 9.0D) {
                            this.chicken.explode(this.chicken.posX, this.chicken.posY, this.chicken.posZ, 0.75F * this.chicken.getScale(), false, true);
                        }

                        this.chicken.breakEgg();
                        this.chicken.setCharging(false);
                        this.chicken.forcefieldAttacker = null;
                        this.chicken.motionX = this.chicken.motionZ = 0.0D;
                        EntityAICharging.destroyBlocksInAABB(this.chicken, this.chicken.partBody.getEntityBoundingBox().expand(this.chicken.motionX, this.chicken.motionY, this.chicken.motionZ).grow((double)this.chicken.partBody.width / 3.0D));
                        if (this.chicken.isHeadAvailable()) {
                            EntityAICharging.destroyBlocksInAABB(this.chicken, this.chicken.partHead.getEntityBoundingBox().expand(this.chicken.motionX, this.chicken.motionY, this.chicken.motionZ).grow((double)this.chicken.partHead.width / 3.0D));
                            EntityAICharging.destroyBlocksInAABB(this.chicken, this.chicken.partBill.getEntityBoundingBox().expand(this.chicken.motionX, this.chicken.motionY, this.chicken.motionZ).grow((double)this.chicken.partBill.width / 3.0D));
                        }
                    }

                    if (Double.isNaN(newDist / this.chargeLastDist) || this.chargeStart != null && Math.sqrt(this.chicken.getDistanceSq(this.chargeStart)) > (double)MightyEnderChicken.config.chargeDistanceCancel || this.attackTime > 200) {
                        this.chicken.setCharging(false);
                        this.chicken.forcefieldAttacker = null;
                    }

                    if (this.chicken.getCharging()) {
                        double diffY = this.chicken.posY - this.chicken.prevPosY;
                        HashSet collidedEnts = new HashSet();
                        collidedEnts.addAll(this.chicken.world.getEntitiesInAABBexcluding(this.chicken, this.chicken.partFootL.getEntityBoundingBox().expand(diffX, diffY, diffZ), this.chicken.predicateTargets));
                        collidedEnts.addAll(this.chicken.world.getEntitiesInAABBexcluding(this.chicken, this.chicken.partFootR.getEntityBoundingBox().expand(diffX, diffY, diffZ), this.chicken.predicateTargets));
                        collidedEnts.addAll(this.chicken.world.getEntitiesInAABBexcluding(this.chicken, this.chicken.partLegL.getEntityBoundingBox().expand(diffX, diffY, diffZ), this.chicken.predicateTargets));
                        collidedEnts.addAll(this.chicken.world.getEntitiesInAABBexcluding(this.chicken, this.chicken.partLegR.getEntityBoundingBox().expand(diffX, diffY, diffZ), this.chicken.predicateTargets));
                        collidedEnts.addAll(this.chicken.world.getEntitiesInAABBexcluding(this.chicken, this.chicken.partBody.getEntityBoundingBox().expand(diffX, diffY, diffZ), this.chicken.predicateTargets));

                        Entity ent;
                        float damage;
                        for(Iterator var18 = collidedEnts.iterator(); var18.hasNext(); ent.attackEntityFrom(ent.getEntityBoundingBox().maxY > this.chicken.partBody.posY ? (new EntityDamageSource("chicken_charge_body", this.chicken)).setDamageBypassesArmor().setDamageIsAbsolute() : (new EntityDamageSource("chicken_charge", this.chicken)).setDamageBypassesArmor().setDamageIsAbsolute(), damage)) {
                            ent = (Entity)var18.next();
                            double d = ent.getX() - this.chicken.getX();
                            double d1 = ent.getY() + (ent.getEntityBoundingBox().maxY - ent.getEntityBoundingBox().minY) / 2.0D - this.chicken.posY;
                            double d2 = ent.getZ() - this.chicken.getZ();
                            double dist2 = d * d + d1 * d1 + d2 * d2 + this.chicken.getRandom().nextGaussian() * 1.0E-5D;
                            if (dist2 < (double)(this.chicken.width * this.chicken.width)) {
                                int amp = 7;
                                ent.getMotionDirection().getStepX() += (double)amp / dist2 * (d * d / dist2) * (d > 0.0D ? 1.0D : -1.0D) + this.chicken.motionX;
                                ent.getMotionDirection().getStepY() += (double)amp * 1.5D / dist2 * (d1 * d1 / dist2) * (d1 > 0.0D ? 1.0D : -1.0D) + this.chicken.motionY + 0.07D * (double)scale;
                                ent.getMotionDirection().getStepY() += (double)amp / dist2 * (d2 * d2 / dist2) * (d2 > 0.0D ? 1.0D : -1.0D) + this.chicken.motionZ;
                                if (ent instanceof ServerPlayerEntity) {
                                    ((ServerPlayerEntity)ent).connection.send(new SPacketEntityVelocity(ent.getEntityId(), ent.motionX, ent.motionY, ent.motionZ));
                                }
                            }

                            damage = this.chicken.getScale();
                            if (this.chicken.getIsChaos()) {
                                damage *= 2.5F;
                                damage *= (float)MightyEnderChicken.config.chaosChickenDamageMultiplier / 100.0F;
                            }
                        }
                    }

                    this.chargeLastDist = newDist;
                    this.lastX = this.chicken.posX;
                    this.lastZ = this.chicken.posZ;
                }
            }

        }
    }
}


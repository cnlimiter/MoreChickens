package cn.evolvefield.mods.morechickens.core.entity.ai;

import cn.evolvefield.mods.morechickens.core.entity.EnderChickenEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;

public class EntityAISpinningAttack extends Goal {
    public EnderChickenEntity chicken;
    public float chance;
    public int time;

    public EntityAISpinningAttack(EnderChickenEntity chicken, float chance) {
        this.chicken = chicken;
        this.chance = chance;
        this.setMutexBits(3);
    }
    @Override
    public boolean canUse() {
        if (!this.chicken.getClearingArea() && !this.chicken.getSpinning()) {
            EntityLivingBase target = this.chicken.getAttackTarget();
            if (target != null && target.isEntityAlive()) {
                return this.chicken.canUseAbility() && this.chicken.getEggState() < 0 && !this.chicken.getFiring() && this.chicken.isEntityAlive() && this.chicken.getRNG().nextFloat() < this.chance;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.chicken.isEntityAlive() && this.time < 140;
    }

    @Override
    public void start() {
        this.chicken.useAbility();
        this.time = 0;
        this.chicken.setSpinning(true);
    }

    @Override
    public void stop() {
        this.chicken.endAbility();
        this.chicken.setSpinning(false);
    }

    @Override
    public void tick() {
        ++this.time;
        if (this.time == 1) {
            this.chicken.playSound(SoundIndex.ender_death, 0.3F * this.chicken.getScale(), 1.0F);
        }

        if (this.time > 10) {
            float eggChance = (float)this.time / 140.0F * 0.5F;
            if (this.chicken.getRNG().nextFloat() < eggChance) {
                float scale = this.chicken.getScale();
                Vec3d buttPos = this.chicken.getHeadPos(-0.5D, 0.02D);
                EntityEggBomb egg = new EntityEggBomb(this.chicken.world, scale, this.chicken.getAttackTarget(), false, this.chicken.getIsChaos());
                egg.setLocationAndAngles(buttPos.x, buttPos.y, buttPos.z, this.chicken.renderYawOffset - 180.0F, 0.0F);
                double x = (double)(-MathHelper.sin((this.chicken.renderYawOffset + 180.0F) * 0.017453292F));
                double z = (double)MathHelper.cos((this.chicken.renderYawOffset + 180.0F) * 0.017453292F);
                float f1 = MathHelper.sqrt(x * x + z * z);
                x /= (double)f1;
                z /= (double)f1;
                x = x * 0.14D * (double)scale;
                z = z * 0.14D * (double)scale;
                egg.motionX = x + this.chicken.motionX;
                egg.motionZ = z + this.chicken.motionZ;
                this.chicken.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F + 0.2F * scale, (this.chicken.getRNG().nextFloat() - this.chicken.getRNG().nextFloat()) * 0.2F + 1.0F);
                this.chicken.world.spawnEntity(egg);
            }
        }

        EntityAICharging.destroyBlocksInAABB(this.chicken, this.chicken.partWingL.getEntityBoundingBox().expand(this.chicken.motionX, this.chicken.motionY, this.chicken.motionZ).grow((double)this.chicken.partWingL.width / 3.0D));
        EntityAICharging.destroyBlocksInAABB(this.chicken, this.chicken.partWingR.getEntityBoundingBox().expand(this.chicken.motionX, this.chicken.motionY, this.chicken.motionZ).grow((double)this.chicken.partWingR.width / 3.0D));
        if (this.chicken.isHeadAvailable()) {
            EntityAICharging.destroyBlocksInAABB(this.chicken, this.chicken.partHead.getEntityBoundingBox().expand(this.chicken.motionX, this.chicken.motionY, this.chicken.motionZ).grow((double)this.chicken.partHead.width / 3.0D));
            EntityAICharging.destroyBlocksInAABB(this.chicken, this.chicken.partBill.getEntityBoundingBox().expand(this.chicken.motionX, this.chicken.motionY, this.chicken.motionZ).grow((double)this.chicken.partBill.width / 3.0D));
        }
    }
}

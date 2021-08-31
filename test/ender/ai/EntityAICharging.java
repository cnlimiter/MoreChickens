package cn.evolvefield.mods.morechickens.core.entity.ai;

import cn.evolvefield.mods.morechickens.core.entity.EnderChickenEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.HashSet;
import java.util.Iterator;

public class EntityAICharging extends Goal {
    public EnderChickenEntity chicken;
    public float chance;
    public int chargeTime;
    public BlockPos chargeStart;
    public float chargeYaw;
    public double chargeLastDist;
    public double lastX;
    public double lastZ;

    public EntityAICharging(EnderChickenEntity chicken, float chance) {
        this.chicken = chicken;
        this.chance = chance;
        this.setMutexBits(3);
    }
    @Override
    public boolean canUse() {
        return this.chicken.canUseAbility() && (this.chicken.getEggState() >= 0 || this.chicken.getAttackTarget() != null) && this.chicken.getRNG().nextFloat() < this.chance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.chicken.getCharging() && this.chicken.isEntityAlive();
    }


    @Override
    public void start() {
        this.chicken.useAbility();
        this.chargeLastDist = -1.0D;
        this.chicken.setCharging(true);
    }

    @Override
    public void stop() {
        this.chicken.endAbility();
        this.chargeStart = null;
        this.chargeTime = 0;
        this.chargeYaw = 0.0F;
    }

    @Override
    public void tick() {
        float scale = this.chicken.getScale();
        if (this.chargeTime < MightyEnderChicken.config.chargeWarnTime) {
            if (this.chargeTime == 0) {
                this.chicken.playSound(SoundEvents.ENTITY_ENDERDRAGON_GROWL, 1.0F + 0.2F * this.chicken.getScale(), 0.8F + this.chicken.getRNG().nextFloat() * 0.4F);
            }

            this.chicken.rotationYaw = this.chicken.renderYawOffset;
            if (!this.chicken.getFiring()) {
                Entity ent = this.chicken.getAttackTarget();
                if (ent != null) {
                    this.chicken.getLookHelper().setLookPosition(ent.posX, ent.posY + (double)ent.getEyeHeight(), ent.posZ, (float)this.chicken.getHorizontalFaceSpeed(), (float)this.chicken.getVerticalFaceSpeed());
                }
            }
        } else {
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
            if (this.chargeTime == MightyEnderChicken.config.chargeWarnTime) {
                if (this.chicken.isHeadAvailable()) {
                    this.chicken.playSound(SoundIndex.charge_start, 1.0F + 0.2F * scale, this.chicken.getIsChaos() ? 0.8F : 1.0F);
                }

                this.chargeStart = new BlockPos(this.chicken);
                this.chargeYaw = this.chicken.renderYawOffset;
                this.lastX = this.chicken.posX;
                this.lastZ = this.chicken.posZ;
            } else if (this.chargeTime > MightyEnderChicken.config.chargeWarnTime) {
                this.chicken.rotationYaw = this.chicken.renderYawOffset = this.chargeYaw;
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
                    this.chicken.motionX = this.chicken.motionZ = 0.0D;
                    destroyBlocksInAABB(this.chicken, this.chicken.partBody.getEntityBoundingBox().expand(this.chicken.motionX, this.chicken.motionY, this.chicken.motionZ).grow((double)this.chicken.partBody.width / 3.0D));
                    if (this.chicken.isHeadAvailable()) {
                        destroyBlocksInAABB(this.chicken, this.chicken.partHead.getEntityBoundingBox().expand(this.chicken.motionX, this.chicken.motionY, this.chicken.motionZ).grow((double)this.chicken.partHead.width / 3.0D));
                        destroyBlocksInAABB(this.chicken, this.chicken.partBill.getEntityBoundingBox().expand(this.chicken.motionX, this.chicken.motionY, this.chicken.motionZ).grow((double)this.chicken.partBill.width / 3.0D));
                    }
                }

                if (Double.isNaN(newDist / this.chargeLastDist) || this.chargeStart != null && Math.sqrt(this.chicken.getDistanceSq(this.chargeStart)) > (double)MightyEnderChicken.config.chargeDistanceCancel || this.chargeTime > 100 + MightyEnderChicken.config.chargeWarnTime) {
                    this.chicken.setCharging(false);
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
                        double d = ent.posX - this.chicken.posX;
                        double d1 = ent.posY + (ent.getEntityBoundingBox().maxY - ent.getEntityBoundingBox().minY) / 2.0D - this.chicken.posY;
                        double d2 = ent.posZ - this.chicken.posZ;
                        double dist2 = d * d + d1 * d1 + d2 * d2 + this.chicken.getRNG().nextGaussian() * 1.0E-5D;
                        if (dist2 < (double)(this.chicken.width * this.chicken.width)) {
                            int amp = 7;
                            ent.motionX += (double)amp / dist2 * (d * d / dist2) * (d > 0.0D ? 1.0D : -1.0D) + this.chicken.motionX;
                            ent.motionY += (double)amp * 1.5D / dist2 * (d1 * d1 / dist2) * (d1 > 0.0D ? 1.0D : -1.0D) + this.chicken.motionY + 0.07D * (double)scale;
                            ent.motionZ += (double)amp / dist2 * (d2 * d2 / dist2) * (d2 > 0.0D ? 1.0D : -1.0D) + this.chicken.motionZ;
                            if (ent instanceof EntityPlayerMP) {
                                ((EntityPlayerMP)ent).connection.sendPacket(new SPacketEntityVelocity(ent.getEntityId(), ent.motionX, ent.motionY, ent.motionZ));
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

        ++this.chargeTime;
    }

    public static void destroyBlocksInAABB(Entity ent, AxisAlignedBB aabb) {
        int i = MathHelper.floor(aabb.minX);
        int j = MathHelper.floor(aabb.minY);
        int k = MathHelper.floor(aabb.minZ);
        int l = MathHelper.floor(aabb.maxX);
        int i1 = MathHelper.floor(aabb.maxY);
        int j1 = MathHelper.floor(aabb.maxZ);

        for(int k1 = i; k1 <= l; ++k1) {
            for(int l1 = j; l1 <= i1; ++l1) {
                for(int i2 = k; i2 <= j1; ++i2) {
                    BlockPos blockpos = new BlockPos(k1, l1, i2);
                    IBlockState iblockstate = ent.getEntityWorld().getBlockState(blockpos);
                    Block block = ent.getEntityWorld().getBlockState(blockpos).getBlock();
                    if (!block.isAir(iblockstate, ent.getEntityWorld(), blockpos) && iblockstate.getMaterial() != Material.FIRE && block.canEntityDestroy(iblockstate, ent.getEntityWorld(), blockpos, ent) && block != Blocks.COMMAND_BLOCK && block != Blocks.REPEATING_COMMAND_BLOCK && block != Blocks.CHAIN_COMMAND_BLOCK && block != Blocks.BEDROCK && block != Blocks.END_GATEWAY) {
                        float chance = 0.3F;
                        if (ent.world.rand.nextFloat() < chance) {
                            ent.getEntityWorld().playEvent(2001, blockpos, Block.getStateId(iblockstate));
                        }

                        ent.world.setBlockToAir(blockpos);
                    }
                }
            }
        }

    }
}

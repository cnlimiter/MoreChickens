package cn.evolvefield.mods.morechickens.core.entity.ai;

import cn.evolvefield.mods.morechickens.core.entity.EnderChickenEntity;
import com.sun.javafx.geom.Vec3d;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIWander extends Goal {
    public EnderChickenEntity chicken;
    public Vec3d targetPos;
    public int stillTime;
    public int randomStillTime;
    public int collisionTime;

    public EntityAIWander(EnderChickenEntity chicken) {
        this.chicken = chicken;
        this.setMutexBits(1);
    }
    @Override
    public boolean canUse() {
        return this.chicken.isEntityAlive();
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
        if (this.targetPos == null) {
            ++this.stillTime;
            if (this.stillTime > 60 + this.randomStillTime) {
                this.findNewTargetPos();
            }
        } else {
            this.stillTime = 0;
            this.chicken.getMoveHelper().setMoveTo(this.targetPos.x, (double)((int)this.targetPos.y), this.targetPos.z, 0.2D * (double)this.chicken.getScale());
            if (this.chicken.getDistance(this.targetPos.x, (double)((int)this.targetPos.y), this.targetPos.z) < (double)(this.chicken.width * 0.6F)) {
                this.targetPos = null;
                this.randomStillTime = this.chicken.getRNG().nextInt(40);
            }

            if (this.chicken.collidedHorizontally) {
                ++this.collisionTime;
                if (this.collisionTime > 5) {
                    this.targetPos = null;
                    this.randomStillTime = this.chicken.getRNG().nextInt(40);
                }
            }
        }
    }


    public void findNewTargetPos() {
        double oriX = this.chicken.posX;
        double oriY = this.chicken.posY;
        double oriZ = this.chicken.posZ;
        float scale = this.chicken.getScale();
        double d0 = this.chicken.posX + (this.chicken.getRNG().nextDouble() - 0.5D) * 64.0D / 10.0D * (double)scale;
        double d1 = this.chicken.posY + (double)(this.chicken.getRNG().nextInt(64) - 32) / 10.0D * (double)scale;
        double d2 = this.chicken.posZ + (this.chicken.getRNG().nextDouble() - 0.5D) * 64.0D / 10.0D * (double)scale;
        boolean flag = false;
        BlockPos blockpos = new BlockPos(d0, d1, d2);
        World world = this.chicken.world;
        if (world.isBlockLoaded(blockpos)) {
            boolean flag1 = false;

            while(!flag1 && blockpos.getY() > 0) {
                BlockPos blockpos1 = blockpos.down();
                IBlockState iblockstate = world.getBlockState(blockpos1);
                if (iblockstate.getMaterial().blocksMovement()) {
                    flag1 = true;
                } else {
                    --d1;
                    blockpos = blockpos1;
                }
            }

            if (flag1) {
                this.setChickenPosition(d0, d1, d2);
                if (world.getCollisionBoxes(this.chicken, this.chicken.getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(this.chicken.getEntityBoundingBox())) {
                    flag = true;
                }
            }
        }

        if (flag) {
            this.targetPos = new Vec3d(d0, d1, d2);
        }

        this.setChickenPosition(oriX, oriY, oriZ);
    }

    public void setChickenPosition(double x, double y, double z) {
        this.chicken.posX = x;
        this.chicken.posY = y;
        this.chicken.posZ = z;
        float f = this.chicken.width / 2.0F;
        float f1 = this.chicken.height;
        this.chicken.setEntityBoundingBox(new AxisAlignedBB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
    }
}

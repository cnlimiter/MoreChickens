package cn.evolvefield.mods.morechickens.core.entity.ai;

import cn.evolvefield.mods.morechickens.core.entity.EnderChickenEntity;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;

import java.util.Iterator;
import java.util.List;

public class EntityAIClearSurroundingsOfEntities extends Goal {

    public static final Predicate SPECIAL = (ent) -> {
        return (!(ent instanceof PlayerEntity) || !((PlayerEntity)ent).isSpectator()) && (ent instanceof LivingEntity || ent instanceof ProjectileEntity && (!(ent instanceof ArrowEntity) || !((ArrowEntity)ent).isOnGround())) && !(ent instanceof EntityEggBomb) && !(ent instanceof EnderChickenEntity);
    };
    public EnderChickenEntity chicken;
    public float chance;
    public int flapTime;
    public int lastEggState;

    public EntityAIClearSurroundingsOfEntities(EnderChickenEntity chicken, float chance) {
        this.chicken = chicken;
        this.chance = chance;
        this.setMutexBits(0);
    }

    @Override
    public boolean canUse() {
        if (this.chicken.getEggState() == -1 && this.lastEggState >= 0) {
            this.lastEggState = this.chicken.getEggState();
            return true;
        } else {
            this.lastEggState = this.chicken.getEggState();
            if (this.chicken.onGround && this.chicken.getEggState() < 0) {
                for(int j2 = 0; j2 < this.chicken.world.playerEntities.size(); ++j2) {
                    EntityPlayer player = (EntityPlayer)this.chicken.world.playerEntities.get(j2);
                    if (!player.capabilities.disableDamage && player.isEntityAlive() && !player.isSpectator() && player.getActiveItemStack().getItem() instanceof ItemBow && player.getItemInUseCount() > 0 && this.chicken.canUseAbility() && this.chicken.getRNG().nextFloat() < this.chance * 1.5F) {
                        return true;
                    }
                }

                if (this.chicken.canUseAbility() && this.chicken.getRNG().nextFloat() < this.chance) {
                    List entities = this.chicken.world.getEntitiesInAABBexcluding(this.chicken, this.chicken.getEntityBoundingBox().offset(0.0D, (double)(-this.chicken.height * 0.15F), 0.0D).grow((double)(this.chicken.width * 0.15F), 0.0D, (double)(this.chicken.width * 0.15F)), SPECIAL);
                    Iterator var5 = entities.iterator();

                    while(var5.hasNext()) {
                        Entity entity = (Entity)var5.next();
                        if (this.chicken.canEntityBeSeen(entity)) {
                            return true;
                        }
                    }
                }

                return false;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.flapTime > 0 && this.chicken.onGround && this.chicken.isEntityAlive();
    }

    @Override
    public void start() {
        this.chicken.useAbility();
        this.chicken.setFlapping(true);
        this.flapTime = 60;
    }

    @Override
    public void stop() {
        this.chicken.endAbility();
        this.chicken.setFlapping(false);
        this.flapTime = 0;
    }

    @Override
    public void tick() {
        --this.flapTime;
    }
}

package cn.evolvefield.mods.morechickens.core.entity;

import cn.evolvefield.mods.morechickens.core.entity.ai.*;
import cn.evolvefield.mods.morechickens.core.entity.util.EnderChickenPartEntity;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

public class EnderChickenEntity extends MobEntity {

    public static final DataParameter SCALE;
    public static final DataParameter FIRING;
    public static final DataParameter EGG_STATE;
    public static final DataParameter CHARGING;
    public static final DataParameter FLAPPING;
    public static final DataParameter CLEAR_AREA;
    public static final DataParameter SPINNING;
    public static final DataParameter FORCEFIELD;
    public static final DataParameter INTRO_STATE;
    public static final String[] LOOK_HELPER_NAME;
    public EnderChickenPartEntity[] partArray;
    public EnderChickenPartEntity partFootL;
    public EnderChickenPartEntity partFootR;
    public EnderChickenPartEntity partLegL;
    public EnderChickenPartEntity partLegR;
    public EnderChickenPartEntity partBody;
    public EnderChickenPartEntity partWingL;
    public EnderChickenPartEntity partWingR;
    public EnderChickenPartEntity partHead;
    public EnderChickenPartEntity partBill;
    public EnderChickenPartEntity partForcefield;

    public ServerBossInfo bossInfo;
    public final Predicate predicateTargets;
    public boolean isPecking;
    public int peckProgress;
    public boolean flapping;
    public float wingRotation;
    public float destPos;
    public float oFlapSpeed;
    public float oFlap;
    public float wingRotDelta;
    public float lastScale;
    public int abilityInUse;
    public EnderDragonEntity dragonDummy;
    public int firingProgress;
    public int breathCooldown;
    public int explosionCooldown;
    public double laserDist;
    public int maxStrafingRunTime;
    public boolean clearArea;
    public int clearAreaTime;
    public LivingEntity forcefieldAttacker;
    public boolean doingIntro;
    public boolean doneIntro;
    public int deathTicks;
    public HashSet ffInform;
    public ArrayList renderNodes;
    public float lastPartialTickRender;
    @OnlyIn(Dist.CLIENT)
    public ChickenLaserLoop soundLaserLoop;
    @OnlyIn(Dist.CLIENT)
    public ChickenBossMusic soundBossMusic;
    @OnlyIn(Dist.CLIENT)
    public boolean ffState;
    @OnlyIn(Dist.CLIENT)
    public int ffTime;
    public int lastIntroState;
    public int spinTime;
    public int healTime;
    public DamageSource deathCause;
    private static final Pattern FAKE_PLAYER_PATTERN;

    protected EnderChickenEntity( World world) {
        super(type,world);
        this.partFootL = new EnderChickenPartEntity(this, "footL", 0.1875F, 0.0625F);
        this.partFootR = new EnderChickenPartEntity(this, "footR", 0.1875F, 0.0625F);
        this.partLegL = new EnderChickenPartEntity(this, "legL", 0.0625F, 0.3125F);
        this.partLegR = new EnderChickenPartEntity(this, "legR", 0.0625F, 0.3125F);
        this.partBody = new EnderChickenPartEntity(this, "body", 0.375F, 0.375F);
        this.partWingL = new EnderChickenPartEntity(this, "wingL", 0.25F, 0.1875F);
        this.partWingR = new EnderChickenPartEntity(this, "wingR", 0.25F, 0.1875F);
        this.partHead = new EnderChickenPartEntity(this, "head", 0.21875F, 0.375F);
        this.partBill = new EnderChickenPartEntity(this, "bill", 0.1875F, 0.25F);
        this.partForcefield = new EnderChickenPartEntity(this, "forcefield", 0.9375F, 0.9375F);
        this.bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PINK, BossInfo.Overlay.NOTCHED_10);
        this.predicateTargets = Predicates.and(EntitySelectors.NOT_SPECTATING, (ent) -> {
            return ent.canBeCollidedWith() && ent.ticksExisted > 60;
        });
        this.wingRotDelta = 1.0F;
        this.lastScale = -1.0F;
        this.doingIntro = false;
        this.doneIntro = false;
        this.ffInform = new HashSet();
        this.renderNodes = new ArrayList();
        this.lastPartialTickRender = 0.0F;
        this.lastIntroState = 0;
        this.partArray = new EnderChickenPartEntity[]{this.partFootL, this.partFootR, this.partLegL, this.partLegR, this.partBody, this.partWingL, this.partWingR, this.partHead, this.partBill, this.partForcefield};
        this.isImmuneToFire = MightyEnderChicken.config.chickensImmuneToFire == 1;
        this.ignoreFrustumCheck = true;
        float scale = this.getScale();
        this.setSize(0.4F * scale * 2.5F, 0.9F * scale * 1.05F);
        this.stepHeight = 0.9F * scale * 0.34F;
        this.moveHelper = new EntityFlyHelper(this);
        this.doneIntro = true;
        ObfuscationReflectionHelper.setPrivateValue(LivingEntity.class, this, new EnderChickenEntity.EntityLookHelperChicken(this), LOOK_HELPER_NAME);

    }
    protected EnderChickenEntity( World world, float rotation, float scale, ServerBossInfo info) {
        this(world);
        this.prevRenderYawOffset = this.renderYawOffset = this.prevRotationYaw = this.rotationYaw = this.prevRotationYawHead = this.rotationYawHead = rotation;
        this.setScale(scale);
        this.setSize(0.4F * scale * 2.5F, 0.9F * scale * 1.05F);
        this.stepHeight = 0.9F * scale * 0.34F;
        this.bossInfo = info;
        this.bossInfo.setName(this.getDisplayName());
        if (this.getIsChaos()) {
            this.bossInfo.setColor(BossInfo.Color.GREEN);
            this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue((double)((float)MightyEnderChicken.config.healthChaosChicken * this.getScale()));
            this.setHealth(this.getMaxHealth());
            this.tasks.taskEntries.clear();
            this.targetTasks.taskEntries.clear();
            this.initEntityAI();
            this.getDataManager().set(INTRO_STATE, 1);
            this.bossInfo.setDarkenSky(true);
            this.doneIntro = false;
        }
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1,new EntityAIBreakEgg(this, 0.0075F));
        this.goalSelector.addGoal(2,new SwimGoal(this));
        this.addAttackSkills();
        this.goalSelector.addGoal(4,new EntityAILookAtTarget(this));
        this.goalSelector.addGoal(5, new EntityAIWander(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new EntityAITargetPlayer(this));
        if (MightyEnderChicken.config.targetAllLiving == 1) {
            this.targetTasks.addTask(3, new EntityAINearestAttackableTargetCustomYRange(this, EntityLiving.class, true, 4.0D + 0.3D * (double)this.getScale()));
        }

    }


    private void addAttackSkills() {
        int priority = 3;
        float multiplier = 1.0F;
        String[] skillSet = MightyEnderChicken.config.enderChickenSkillSet;
        if (this.getIsChaos()) {
            multiplier = (float)MightyEnderChicken.config.chaosChickenSkillChanceMultiplier / 100.0F;
            skillSet = MightyEnderChicken.config.chaosChickenSkillSet;
            this.goalSelector.addGoal(0, new EntityAIDoIntro(this));
        }

        String[] var4 = skillSet;
        int var5 = skillSet.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String s = var4[var6];
            byte var9 = -1;
            switch(s.hashCode()) {
                case -2008047626:
                    if (s.equals("spinning")) {
                        var9 = 7;
                    }
                    break;
                case -1719626484:
                    if (s.equals("regenForcefield")) {
                        var9 = 4;
                    }
                    break;
                case -1271460742:
                    if (s.equals("clearArea")) {
                        var9 = 1;
                    }
                    break;
                case -1007312673:
                    if (s.equals("strafingRun")) {
                        var9 = 5;
                    }
                    break;
                case -970941643:
                    if (s.equals("ffRetaliate")) {
                        var9 = 6;
                    }
                    break;
                case 102743755:
                    if (s.equals("laser")) {
                        var9 = 3;
                    }
                    break;
                case 1436115569:
                    if (s.equals("charging")) {
                        var9 = 0;
                    }
                    break;
                case 1454621998:
                    if (s.equals("clearEntities")) {
                        var9 = 2;
                    }
            }

            switch(var9) {
                case 0:
                    this.goalSelector.addGoal(priority, new EntityAICharging(this, (float)MightyEnderChicken.config.chargingChance / 1000.0F * multiplier));
                    break;
                case 1:
                    this.goalSelector.addGoal(priority, new EntityAIClearArea(this, (float)MightyEnderChicken.config.clearAreaChance / 1000.0F * multiplier));
                    break;
                case 2:
                    this.goalSelector.addGoal(priority, new EntityAIClearSurroundingsOfEntities(this, (float)MightyEnderChicken.config.flapChance / 1000.0F * multiplier));
                    break;
                case 3:
                    this.goalSelector.addGoal(priority, new EntityAILaser(this, (float)MightyEnderChicken.config.laserChance / 1000.0F * multiplier));
                    break;
                case 4:
                    this.goalSelector.addGoal(priority, new EntityAIRegenerateForcefield(this, (float)MightyEnderChicken.config.regenerateForcefieldChance / 1000.0F * multiplier));
                    break;
                case 5:
                    this.goalSelector.addGoal(priority, new EntityAIStrafingRun(this, (float)MightyEnderChicken.config.strafingRunChance / 1000.0F * multiplier));
                    break;
                case 6:
                    this.goalSelector.addGoal(priority, new EntityAIForcefieldRetaliation(this));
                    break;
                case 7:
                    this.goalSelector.addGoal(priority, new EntityAISpinningAttack(this, (float)MightyEnderChicken.config.spinningChance / 1000.0F * multiplier));
            }
        }

    }



    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SCALE, (float)(this.getIsChaos() ? MightyEnderChicken.config.scaleChaosChicken : MightyEnderChicken.config.scaleEnderChicken) * 0.1F);
        this.entityData.define(FIRING, false);
        this.entityData.define(EGG_STATE, 0);
        this.entityData.define(CHARGING, false);
        this.entityData.define(FLAPPING, false);
        this.entityData.define(CLEAR_AREA, false);
        this.entityData.define(SPINNING, false);
        this.entityData.define(FORCEFIELD, false);
        this.entityData.define(INTRO_STATE, 0);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    @Override
    public World getCommandSenderWorld() {
        return super.getCommandSenderWorld();
    }

    @Override
    protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
        return 0.85F * this.getScale();
    }







    public boolean hurt(DamageSource source, float damage) {
        if (this.shouldIgnoreDamage(source)) {
            return false;
        } else if (source.equals(DamageSource.IN_WALL)) {
            this.clearArea = true;
            return false;
        } else {
            if (source != DamageSource.OUT_OF_WORLD && MightyEnderChicken.config.minHitsRequired > 1 && damage > (float)MathHelper.ceil(this.getMaxHealth() / (float)MightyEnderChicken.config.minHitsRequired)) {
                damage = (float) MathHelper.ceil(this.getMaxHealth() / (float)MightyEnderChicken.config.minHitsRequired);
            }

            return super.hurt(source, damage);
        }
    }

    public boolean hurt(EnderChickenPartEntity part, DamageSource source, float damage) {
        if (this.getEggState() >= 0) {
            if (damage > this.getMaxHealth() / 10.0F) {
                if (!this.world.isRemote) {
                    this.breakEgg();
                }

                return true;
            } else {
                return false;
            }
        } else {
            if (this.getForcefield() && this.getEggState() < 0) {
                if (part.partName.equals("forcefield") && source.getDamageType().equals("player") && source.getTrueSource() instanceof EntityLivingBase && source instanceof EntityDamageSource && !((EntityDamageSource)source).getIsThornsDamage() && (MightyEnderChicken.config.forcefieldBreakItem.equals("null") || (new ResourceLocation(MightyEnderChicken.config.forcefieldBreakItem)).equals(((EntityLivingBase)source.getTrueSource()).getHeldItemMainhand().getItem().getRegistryName()))) {
                    if (!this.world.isRemote) {
                        this.setForcefield(false);
                        this.forcefieldAttacker = (EntityLivingBase)source.getTrueSource();
                        this.setAttackTarget(this.forcefieldAttacker);
                    } else if (source.getTrueSource() instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer)source.getTrueSource();
                        if (player.getDistanceSq(part) > 36.0D) {
                            MightyEnderChicken.channel.sendToServer(new PacketAttackForcefield(this));
                        }
                    }

                    return true;
                }

                if (!this.world.isRemote && source.getTrueSource() instanceof EntityPlayer) {
                    Item item = (Item)Item.REGISTRY.getObject(new ResourceLocation(MightyEnderChicken.config.forcefieldBreakItem));
                    EntityPlayer player = (EntityPlayer)source.getTrueSource();
                    if (!this.ffInform.contains(player.getName())) {
                        this.ffInform.add(player.getName());
                        if (!MightyEnderChicken.config.forcefieldBreakItem.equals("null") && item == null) {
                            player.sendMessage(new TextComponentTranslation("text.chicken.attack_misconfigured", new Object[0]));
                        } else {
                            String name = MightyEnderChicken.config.forcefieldBreakItem.equals("null") ? I18n.translateToLocal("text.chicken.attack_melee") : item.getItemStackDisplayName(new ItemStack(item));
                            player.sendMessage(new TextComponentTranslation("text.chicken.attack_with_stick", new Object[]{name}));
                        }
                    }

                    return false;
                }
            } else if (!part.partName.equals("forcefield")) {
                return this.hurt(source, damage);
            }

            return false;
        }
    }
}

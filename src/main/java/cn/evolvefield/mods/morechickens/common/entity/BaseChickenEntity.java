package cn.evolvefield.mods.morechickens.common.entity;

import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.common.data.ChickenRegistry;
import cn.evolvefield.mods.morechickens.common.data.ChickenUtils;
import cn.evolvefield.mods.morechickens.common.data.Gene;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import cn.evolvefield.mods.morechickens.integrations.top.ITOPInfoEntityProvider;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Lazy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BaseChickenEntity extends ModAnimalEntity implements ITOPInfoEntityProvider {
    private static final Lazy<Integer> breedingTimeout = Lazy.of(ModConfig.COMMON.chickenBreedingTime::get);

    public static final DataParameter<Boolean> ANALYZED = EntityDataManager.defineId(BaseChickenEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<String> NAME = EntityDataManager.defineId(BaseChickenEntity.class, DataSerializers.STRING);

    private static final Ingredient MATERIAL = Ingredient.of(Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.BEETROOT_SEEDS);

    private static final Logger LOGGER = LogManager.getLogger();

    private Gene gene;
    public Gene alleleA;
    public Gene alleleB;
    public ChickenData type;
    private int layTimer;

    public float oFlap, oFlapSpeed, wingRotation, wingRotDelta = 1.0f, destPos;

    public BaseChickenEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
        this.type = ChickenRegistry.Types.get("oak");
        setAlleles(new Gene(random), new Gene(random));
        randomBreed();
    }

    public static AttributeModifierMap.MutableAttribute setAttributes(){
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.25d);
    }

    public Gene getGene(){
        return gene;
    }

    public void setAlleles(Gene a, Gene b){
        alleleA = a;
        alleleB = b;
        gene = alleleA.STRENGTH >= alleleB.STRENGTH ? alleleA : alleleB;
        resetTimer();
    }

    private void resetTimer(){
        layTimer = ChickenUtils.calcNewEggLayTime(random, type, gene.GROWTH);
        layTimer = Math.max(600, layTimer);
    }

    public String getChickenName(){
        return entityData.get(NAME);
    }

    public void setChickenName(String data) {
        this.entityData.set(NAME, data);
    }

    public void setType(ChickenData type){
        this.type = type;
        this.entityData.set(NAME, type.name);
    }

    public int getLayTimer(){
        return layTimer;
    }

    public void randomBreed(){
        switch(random.nextInt(4)){
            case 0:
                type = ChickenUtils.getChickenDataByName("oak"); break;
            case 1:
                type = ChickenUtils.getChickenDataByName("sand"); break;
            case 2:
                type = ChickenUtils.getChickenDataByName("flint"); break;
            default:
                type = ChickenUtils.getChickenDataByName("quartz"); break;
        }
        entityData.set(NAME, type.name);
    }

    protected int getBreedingTimeout(){
        return breedingTimeout.get();
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag) {
        randomBreed();
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Nonnull
    @Override
    protected ITextComponent getTypeName() {
        return new TranslationTextComponent("text.chickens.name."+ getChickenName());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        goalSelector.addGoal(3, new TemptGoal(this, 1.1, MATERIAL, false));
        goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0));
        goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0f));
        goalSelector.addGoal(7, new LookRandomlyGoal(this));
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(NAME, "painted");
        this.entityData.define(ANALYZED, true);
    }


    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity other) {
        BaseChickenEntity child = ModEntities.BASE_CHICKEN.get().create(world);
        if(child != null) {
            BaseChickenEntity otherChicken = (BaseChickenEntity) other;
            Gene childA = alleleA.crossover(alleleB, random);
            Gene childB = otherChicken.alleleA.crossover(otherChicken.alleleB, random);
            child.setAlleles(childA, childB);
            child.setType(type.getOffspring(otherChicken.type, random));
        }
        return child;
    }


    @Override
    public void aiStep() {
        super.aiStep();
        this.oFlap = this.wingRotation;
        this.oFlapSpeed = this.destPos;
        this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 2) * 0.3D);
        this.destPos = MathHelper.clamp(this.destPos, 0.0F, 1.0F);
        if (!this.onGround && this.wingRotDelta < 1.0F) {
            this.wingRotDelta = 1.0F;
        }

        this.wingRotDelta = (float)((double)this.wingRotDelta * 0.9D);
        Vector3d vector3d = this.getDeltaMovement();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }

        this.wingRotation += this.wingRotDelta * 2.0F;
        if(!level.isClientSide && isAlive() && !isBaby() && gene != null){
            layTimer --;
            if(layTimer <= 0){
                if(type != null) {
                    resetTimer();
                    this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    ChickenUtils.calcDrops(gene.GAIN, type, 0, random).forEach(this::spawnAtLocation);
                }
            }
        }
    }


    @Override
    public boolean isFood(ItemStack stack) {
        return MATERIAL.test(stack);
    }


    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return this.isBaby() ? sizeIn.height * 0.85F : sizeIn.height * 0.92F;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }


    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CHICKEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.CHICKEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CHICKEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Analyzed")) {
            this.entityData.set(ANALYZED, nbt.getBoolean("Analyzed"));
        }
        if(nbt.contains("Name")) {
            setType(ChickenRegistry.Types.get(nbt.getString("Name")));
        }
        if(nbt.contains("AlleleA"))
            alleleA.readFromTag(nbt.getCompound("AlleleA"));
        if(nbt.contains("AlleleB"))
            alleleB.readFromTag(nbt.getCompound("AlleleB"));
        setAlleles(alleleA, alleleB);
        if(nbt.contains("EggLayTime"))
            layTimer = nbt.getInt("EggLayTime");
    }


    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("Analyzed", this.entityData.get(ANALYZED));
        nbt.putInt("EggLayTime", layTimer);
        nbt.putString("Name", type.name);
        nbt.put("AlleleA", alleleA.writeToTag());
        nbt.put("AlleleB", alleleB.writeToTag());
    }

    @Override
    protected void dropAllDeathLoot(DamageSource source) {
        super.dropAllDeathLoot(source);
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, source)) return;
        if(this.removed || this.dead)
            return;
        super.dropAllDeathLoot(source);
        if(type.deathItem == null || type.deathItem.equals("") || type.deathAmount <= 0)
            return;
        int lootingLevel = ForgeHooks.getLootingLevel(this, source.getEntity(), source);
        int amount = type.deathAmount + random.nextInt(Math.max(1, type.deathAmount) + lootingLevel);
        Item dieItem = ChickenUtils.getItem(type.deathItem, random);
        if(dieItem != null)
            spawnAtLocation(new ItemStack(dieItem, amount));
    }


    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if(itemStack.getItem() == Items.BUCKET && !this.isBaby() && this.type == ChickenUtils.getChickenDataByName("leather")){
            player.playSound(SoundEvents.COW_MILK, 1.0f, 1.0f);
            ItemStack leftover = DrinkHelper.createFilledResult(itemStack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, leftover);
            return ActionResultType.sidedSuccess(player.level.isClientSide);
        }
        else
            return super.mobInteract(player, hand);
    }


    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, Entity entity, IProbeHitEntityData data) {
        BaseChickenEntity chicken = (BaseChickenEntity) entity;
        if (this.entityData.get(ANALYZED)) {
            probeInfo.text(new TranslationTextComponent("text.chickens.stat.growth", chicken.gene.GROWTH));
            probeInfo.text(new TranslationTextComponent("text.chickens.stat.gain", chicken.gene.GAIN));
            probeInfo.text(new TranslationTextComponent("text.chickens.stat.strength", chicken.gene.STRENGTH));
        }

        if (! chicken.isBaby()) {
            if (chicken.type.layTime != 0) {
                int secs = chicken.layTimer / 20;
                probeInfo.text(new TranslationTextComponent("text.chickens.stat.eggTimer", String.format("%02d:%02d", secs / 60, secs % 60)));
            }
        }
    }
}

package cn.evolvefield.mods.morechickens.core.entity;

import cn.evolvefield.mods.morechickens.core.entity.util.main.Gene;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import cn.evolvefield.mods.morechickens.init.ModDefaultEntities;
import cn.evolvefield.mods.morechickens.core.entity.util.main.ChickenType;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Lazy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class BaseChickenEntity extends ModAnimalEntity {
    private static final Lazy<Integer> breedingTimeout = Lazy.of(ModConfig.COMMON.chickenBreedingTime::get);



    private static final DataParameter<String> NAME = EntityDataManager.defineId(BaseChickenEntity.class, DataSerializers.STRING);

    private static final Ingredient MATERIAL = Ingredient.of(Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.BEETROOT_SEEDS);

    private static final Logger LOGGER = LogManager.getLogger();

    private Gene gene, alleleA, alleleB;
    private ChickenType type;
    private int layTimer;

    public float oFlap, oFlapSpeed, wingRotation, wingRotDelta = 1.0f, destPos;

    public BaseChickenEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
        this.type = ChickenType.RED_DYE;
        setAlleles(new Gene(random), new Gene(random));
        randomomBreed();
    }

    public static AttributeModifierMap.MutableAttribute setAttributes(){
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    public Gene getGene(){
        return gene;
    }

    private void layLoot(){
        spawnAtLocation(type.getLoot(random, gene));
    }

    public void setAlleles(Gene a, Gene b){
        alleleA = a;
        alleleB = b;
        gene = alleleA.dominance >= alleleB.dominance ? alleleA : alleleB;
        resetTimer();
    }

    private void resetTimer(){
        layTimer = type.layTime + random.nextInt(type.layTime + 1);
        layTimer *=  gene.layTime + random.nextFloat() * gene.layRandomTime;
        layTimer = Math.max(600, layTimer);
    }

    public String getBreedName(){
        return entityData.get(NAME);
    }

    public int getLayTimer(){
        return layTimer;
    }

    public void randomomBreed(){
        switch(random.nextInt(4)){
            case 0:
                type = ChickenType.OAK; break;
            case 1:
                type = ChickenType.SAND; break;
            case 2:
                type = ChickenType.FLINT; break;
            default:
                type = ChickenType.QUARTZ; break;
        }
        entityData.set(NAME, type.name);
    }

    public void setType(ChickenType type){
        this.type = type;
        entityData.set(NAME, type.name);
    }

    protected int getBreedingTimeout(){
        return breedingTimeout.get();
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag) {
        randomomBreed();
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
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
        entityData.define(NAME, "painted");
    }


    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity other) {
        BaseChickenEntity child = ModDefaultEntities.BASE_CHICKEN.get().create(world);
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
                    layLoot();
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
        if(nbt.contains("Name")) {
            setType(ChickenType.Types.get(nbt.getString("Name")));
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
        Item dieItem = ChickenType.getItem(type.deathItem, random);
        if(dieItem != null)
            spawnAtLocation(new ItemStack(dieItem, amount));
    }


    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if(itemStack.getItem() == Items.BUCKET && !this.isBaby() && this.type == ChickenType.LEATHER){
            player.playSound(SoundEvents.COW_MILK, 1.0f, 1.0f);
            ItemStack leftover = DrinkHelper.createFilledResult(itemStack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, leftover);
            return ActionResultType.sidedSuccess(player.level.isClientSide);
        }
        else
            return super.mobInteract(player, hand);
    }


}

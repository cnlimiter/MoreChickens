package cn.evolvefield.mods.morechickens.core.entity;

import cn.evolvefield.mods.morechickens.init.ModConfig;
import cn.evolvefield.mods.morechickens.init.ModDefaultEntities;
import cn.evolvefield.mods.morechickens.core.entity.util.ChickenType;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Lazy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Random;

public class BaseChickenEntity extends ModAnimalEntity {
    private static final Lazy<Integer> breedingTimeout = Lazy.of(ModConfig.COMMON.chickenBreedingTime::get);

    public static class Gene {
        private static final float MUTATION_SIGMA = 0.05f;

        public float layAmount;
        public float layRandomAmount;
        public float layTime;
        public float layRandomTime;
        public float dominance;
//        public float growth;
//        public float gain;
//        public float strength;

        public Gene(Random random){
            layAmount = random.nextFloat() * 1.5f;
            layRandomAmount = random.nextFloat();
            layTime = random.nextFloat() * 1.5f;
            layRandomTime = random.nextFloat();
            dominance = random.nextFloat();
//            growth = random.nextFloat() * 100f;
//            gain = random.nextFloat() * 100f;
//            strength = random.nextFloat() * 100f;
        }

        public Gene(){
        }

        public Gene crossover(Gene other, Random random){
            Gene child = new Gene();
            child.layAmount = Math.max(0, random.nextBoolean() ? layAmount : other.layAmount + (float)random.nextGaussian() * MUTATION_SIGMA);
            child.layRandomAmount = Math.max(0, random.nextBoolean() ? layRandomAmount : other.layRandomAmount + (float)random.nextGaussian() * MUTATION_SIGMA);
            child.layTime = Math.max(0, random.nextBoolean() ? layTime : other.layTime + (float)random.nextGaussian() * MUTATION_SIGMA);
            child.layRandomTime = Math.max(0, random.nextBoolean() ? layRandomTime : other.layRandomTime + (float)random.nextGaussian() * MUTATION_SIGMA);
            child.dominance = random.nextBoolean() ? dominance : other.dominance + (float)random.nextGaussian() * MUTATION_SIGMA;
//            child.growth = random.nextBoolean() ? growth : other.growth + (float)random.nextGaussian() * MUTATION_SIGMA;
//            child.gain = random.nextBoolean() ? gain : other.gain + (float)random.nextGaussian() * MUTATION_SIGMA;
//            child.strength = random.nextBoolean() ? strength : other.strength + (float)random.nextGaussian() * MUTATION_SIGMA;

            return child;
        }

        public Gene readFromTag(CompoundTag nbt){
            layAmount = nbt.getFloat("LayAmount");
            layRandomAmount = nbt.getFloat("LayRandomAmount");
            layTime = nbt.getFloat("LayTime");
            layRandomTime = nbt.getFloat("LayRandomTime");
            dominance = nbt.getFloat("Dominance");
//            growth = nbt.getFloat("Growth");
//            gain = nbt.getFloat("Gain");
//            strength = nbt.getFloat("Strength");
            return this;
        }

        public CompoundTag writeToTag(){
            CompoundTag nbt = new CompoundTag();
            nbt.putFloat("LayAmount", layAmount);
            nbt.putFloat("LayRandomAmount", layRandomAmount);
            nbt.putFloat("LayTime", layTime);
            nbt.putFloat("LayRandomTime", layRandomTime);
            nbt.putFloat("Dominance", dominance);
//            nbt.putFloat("Growth", growth);
//            nbt.putFloat("Gain", gain);
//            nbt.putFloat("Strength", strength);
            return nbt;
        }
    }


    private static final EntityDataAccessor<String> BREED_NAME = SynchedEntityData.defineId(BaseChickenEntity.class, EntityDataSerializers.STRING);

    private static final Ingredient BREED_MATERIAL = Ingredient.of(Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.BEETROOT_SEEDS);

    private static final Logger LOGGER = LogManager.getLogger();

    private Gene gene, alleleA, alleleB;
    private ChickenType breed;
    private int layTimer;
    //Random random = new Random();

    public float oFlap, oFlapSpeed, wingRotation, wingRotDelta = 1.0f, destPos;

    public BaseChickenEntity(EntityType<? extends Animal> type, Level worldIn) {
        super(type, worldIn);
        breed = ChickenType.RED_DYE;
        setAlleles(new Gene(random), new Gene(random));
        randomomBreed();
    }

    public static AttributeSupplier.Builder setAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    public Gene getGene(){
        return gene;
    }

    private void layLoot(){
        spawnAtLocation(breed.getLoot(random, gene));
    }

    public void setAlleles(Gene a, Gene b){
        alleleA = a;
        alleleB = b;
        gene = alleleA.dominance >= alleleB.dominance ? alleleA : alleleB;
        resetTimer();
    }

    private void resetTimer(){
        layTimer = breed.layTime + random.nextInt(breed.layTime + 1);
        layTimer *=  gene.layTime + random.nextFloat() * gene.layRandomTime;
        layTimer = Math.max(600, layTimer);
    }

    public String getBreedName(){
        return entityData.get(BREED_NAME);
    }

    public int getLayTimer(){
        return layTimer;
    }

    public void randomomBreed(){
        switch(random.nextInt(4)){
            case 0:
                breed = ChickenType.OAK; break;
            case 1:
                breed = ChickenType.SAND; break;
            case 2:
                breed = ChickenType.FLINT; break;
            default:
                breed = ChickenType.QUARTZ; break;
        }
        entityData.set(BREED_NAME, breed.name);
    }

    public void setBreed(ChickenType breed){
        this.breed = breed;
        entityData.set(BREED_NAME, breed.name);
    }

    protected int getBreedingTimeout(){
        return breedingTimeout.get();
    }



    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason,@Nullable SpawnGroupData spawnDataIn,@Nullable CompoundTag dataTag) {
        randomomBreed();
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }





    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        goalSelector.addGoal(3, new TemptGoal(this, 1.1, BREED_MATERIAL, false));
        goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BREED_NAME, "painted");
    }



    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob other) {
        BaseChickenEntity child = ModDefaultEntities.BASE_CHICKEN.get().create(world);
        if(child != null) {
            BaseChickenEntity otherChicken = (BaseChickenEntity) other;
            Gene childA = alleleA.crossover(alleleB, random);
            Gene childB = otherChicken.alleleA.crossover(otherChicken.alleleB, random);
            child.setAlleles(childA, childB);
            child.setBreed(breed.getOffspring(otherChicken.breed, random));
        }
        return child;
    }


    @Override
    public void aiStep() {
        super.aiStep();
        this.oFlap = this.wingRotation;
        this.oFlapSpeed = this.destPos;
        this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 2) * 0.3D);
        this.destPos = Mth.clamp(this.destPos, 0.0F, 1.0F);
        if (!this.onGround && this.wingRotDelta < 1.0F) {
            this.wingRotDelta = 1.0F;
        }

        this.wingRotDelta = (float)((double)this.wingRotDelta * 0.9D);
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }

        this.wingRotation += this.wingRotDelta * 2.0F;
        if(!level.isClientSide && isAlive() && !isBaby() && gene != null){
            layTimer --;
            if(layTimer <= 0){
                if(breed != null) {
                    resetTimer();
                    this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    layLoot();
                }
            }
        }
    }


    @Override
    public boolean isFood(ItemStack stack) {
        return BREED_MATERIAL.test(stack);
    }


    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return this.isBaby() ? sizeIn.height * 0.85F : sizeIn.height * 0.92F;
    }


    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier,DamageSource source) {
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
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if(nbt.contains("Breed")) {
            setBreed(ChickenType.Types.get(nbt.getString("Breed")));
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
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("EggLayTime", layTimer);
        nbt.putString("Breed", breed.name);
        nbt.put("AlleleA", alleleA.writeToTag());
        nbt.put("AlleleB", alleleB.writeToTag());
    }

    @Override
    protected void dropAllDeathLoot(DamageSource source) {
        super.dropAllDeathLoot(source);
        if (ForgeHooks.onLivingDeath(this, source)) return;
        if( this.dead)
            return;
        super.dropAllDeathLoot(source);
        if(breed.deathItem == null || breed.deathItem.equals("") || breed.deathAmount <= 0)
            return;
        int lootingLevel = ForgeHooks.getLootingLevel(this, source.getEntity(), source);
        int amount = breed.deathAmount + random.nextInt(Math.max(1, breed.deathAmount) + lootingLevel);
        Item dieItem = ChickenType.getItem(breed.deathItem, random);
        if(dieItem != null)
            spawnAtLocation(new ItemStack(dieItem, amount));
    }


    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if(itemStack.getItem() == Items.BUCKET && !this.isBaby() && this.breed == ChickenType.LEATHER){
            player.playSound(SoundEvents.COW_MILK, 1.0f, 1.0f);
            ItemStack leftover = ItemUtils.createFilledResult(itemStack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, leftover);
            return InteractionResult.sidedSuccess(player.level.isClientSide);
        }
        else
            return super.mobInteract(player, hand);
    }


}

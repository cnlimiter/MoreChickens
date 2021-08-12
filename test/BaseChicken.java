package cn.evolvefield.mods.morechickens.core.entity;

import cn.evolvefield.mods.morechickens.core.entity.util.SpawnType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BaseChicken extends ChickenEntity {

    private float layCoefficient = 1.0f;
    private BaseChicken parent1;
    private BaseChicken parent2;

    private static final DataParameter<Integer> LAY_PROGRESS = EntityDataManager.defineId(BaseChicken.class, DataSerializers.INT);
    private static final DataParameter<Boolean> CHICKEN_STATS_ANALYZED = EntityDataManager.defineId(BaseChicken.class, DataSerializers.BOOLEAN);

    private static final DataParameter<String>  CHICKEN_TYPE = EntityDataManager.defineId(BaseChicken.class, DataSerializers.STRING);
    public static final DataParameter<Integer> LAY_ITEM_ID = EntityDataManager.defineId(BaseChicken.class, DataSerializers.INT);

    private static final DataParameter<Integer> CHICKEN_GROWTH = EntityDataManager.defineId(BaseChicken.class, DataSerializers.INT);
    private static final DataParameter<Integer> CHICKEN_GAIN = EntityDataManager.defineId(BaseChicken.class, DataSerializers.INT);
    private static final DataParameter<Integer> CHICKEN_STRENGTH = EntityDataManager.defineId(BaseChicken.class, DataSerializers.INT);

    private static final String TYPE_NBT = "Type";
    private static final String CHICKEN_STATS_ANALYZED_NBT = "Analyzed";
    private static final String CHICKEN_GROWTH_NBT = "Growth";
    private static final String CHICKEN_GAIN_NBT = "Gain";
    private static final String CHICKEN_STRENGTH_NBT = "Strength";
    private static final String CHICKEN_LAY_ITEM_ID = "LayItemId";


    public BaseChicken(EntityType<? extends ChickenEntity> type, World world) {
        super(type, world);
    }


    public int getLayProgress() {
        return entityData.get(LAY_PROGRESS);
    }

    private void updateLayProgress() {
        entityData.set(LAY_PROGRESS, eggTime / 60 / 20 / 2);
    }

    private void setTimeUntilNextEgg(int value) {
        eggTime = value;
        updateLayProgress();
    }

    private void resetTimeUntilNextEgg() {
        int newBaseTimeUntilNextEgg = (int) (Math.max(6000  * layCoefficient, 1.0f)
                + random.nextInt((int) (2*Math.max(6000  * layCoefficient, 1.0f) - Math.max(6000  * layCoefficient, 1.0f))));
        int newTimeUntilNextEgg = (int) Math.max(1.0f, (newBaseTimeUntilNextEgg * (10.f - getGrowth() + 1.f)) / 10.f);
        setTimeUntilNextEgg(newTimeUntilNextEgg * 2);
    }
////////////////////////////////
    public boolean getStatsAnalyzed() {
        return entityData.get(CHICKEN_STATS_ANALYZED);
    }

    public void setStatsAnalyzed(boolean statsAnalyzed) {
        entityData.set(CHICKEN_STATS_ANALYZED, statsAnalyzed);
    }

    public int getGain() {
        return entityData.get(CHICKEN_GAIN);
    }

    private void setGain(int gain) {
        entityData.set(CHICKEN_GAIN, gain);
    }

    public int getGrowth() {
        return entityData.get(CHICKEN_GROWTH);
    }

    private void setGrowth(int growth) {
        entityData.set(CHICKEN_GROWTH, growth);
    }

    public int getStrength() {
        return entityData.get(CHICKEN_STRENGTH);
    }

    private void setStrength(int strength) {
        entityData.set(CHICKEN_STRENGTH, strength);
    }
    //////////////////
    public int getTier() {
        if (parent1 == null || parent2 == null) {
            return 1;
        }
        return Math.max(parent1.getTier(), parent2.getTier()) + 1;
    }

    private static void inheritStats(BaseChicken newChicken, BaseChicken parent) {
        newChicken.setGrowth(parent.getGrowth());
        newChicken.setGain(parent.getGain());
        newChicken.setStrength(parent.getStrength());
    }

    private static void increaseStats(BaseChicken newChicken, BaseChicken parent1, BaseChicken parent2, Random rand) {
        int parent1Strength = parent1.getStrength();
        int parent2Strength = parent2.getStrength();
        newChicken.setGrowth(calculateNewStat(parent1Strength, parent2Strength, parent1.getGrowth(), parent2.getGrowth(), rand));
        newChicken.setGain(calculateNewStat(parent1Strength, parent2Strength, parent2.getGain(), parent2.getGain(), rand));
        newChicken.setStrength(calculateNewStat(parent1Strength, parent2Strength, parent1Strength, parent2Strength, rand));
    }

    private static int calculateNewStat(int thisStrength, int mateStrength, int stat1, int stat2, Random rand) {
        int mutation = rand.nextInt(2) + 1;
        int newStatValue = (stat1 * thisStrength + stat2 * mateStrength) / (thisStrength + mateStrength) + mutation;
        if (newStatValue <= 1) return 1;
        if (newStatValue >= 10) return 10;
        return newStatValue;
    }

    private static class GroupData implements ILivingEntityData {
        private final String type;

        public GroupData(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public void setChickenType(String registryName) {
        setChickenTypeInternal(registryName);
        //isImmuneToFire = getChickenDescription().isImmuneToFire();
        resetTimeUntilNextEgg();
    }

    private void setChickenTypeInternal(String registryName) {
        this.entityData.set(CHICKEN_TYPE, registryName);
    }

    private String getChickenTypeInternal() {
        return this.entityData.get(CHICKEN_TYPE);
    }


    private SpawnType getSpawnType() {
        Biome biome = level.getBiome(getOnPos());
        return SpawnType.getSpawnType(biome);
    }

    public String getChickenName() {
        return getChickenName(true);
    }

    public String getChickenType() {
        return getEncodeId();
    }

    public String getChickenName(boolean stripName) {
        String[] types = getChickenType().split("[:]");
        String type = types[0];
        if (types.length > 1) {
            type = types[1];
        }
        return stripName ? type.replace("_chicken", "") : type;
    }

    private int getStatusValue(CompoundNBT compound, String statusName) {
        return compound.contains(statusName) ? compound.getInt(statusName) : 1;
    }

    public Item getItem(int id) {
        return Item.byId(id);
    }

    public int getLayItemId(){
        return this.entityData.get(LAY_ITEM_ID);
    }

    public void setLayItemId(int id){
//        int id = Item.getId(item);
        this.entityData.set(LAY_ITEM_ID,id);
    }


    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return ChickenEntity.createAttributes();
    }
    ///////////////////


    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficultyInstance, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT nbt) {
        data = super.finalizeSpawn(world, difficultyInstance, reason, data, nbt);
        if (data instanceof GroupData) {
            GroupData groupData = (GroupData) data;
            setChickenType(groupData.getType());
        } else {
            setChickenType(getChickenName());
            data = new GroupData(getChickenName());
        }
        if (random.nextInt(5) == 0) {
            ageUp(-24000);
        }
        return data;
    }


    @Override
    public void ageUp(int age) {
        int childAge = -24000;
        boolean resetToChild = age == childAge;
        if (resetToChild) {
            age = Math.min(-1, (childAge * (10 - getGrowth() + 1)) / 10);
        }

        int loveAge = 6000;
        boolean resetLoveAfterBreeding = age == loveAge;
        if (resetLoveAfterBreeding) {
            age = Math.max(1, (loveAge * (10 - getGrowth() + 1)) / 10);
        }
        super.ageUp(age);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(CHICKEN_TYPE, "");
        entityData.define(LAY_ITEM_ID, 0);
        entityData.define(CHICKEN_GROWTH, 1);
        entityData.define(CHICKEN_GAIN, 1);
        entityData.define(CHICKEN_STRENGTH, 1);
        entityData.define(LAY_PROGRESS, 0);
        entityData.define(CHICKEN_STATS_ANALYZED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tagCompound) {
        super.addAdditionalSaveData(tagCompound);
        tagCompound.putString(TYPE_NBT, getChickenTypeInternal());
        tagCompound.putBoolean(CHICKEN_STATS_ANALYZED_NBT, getStatsAnalyzed());
        tagCompound.putInt(CHICKEN_GROWTH_NBT, getGrowth());
        tagCompound.putInt(CHICKEN_GAIN_NBT, getGain());
        tagCompound.putInt(CHICKEN_STRENGTH_NBT, getStrength());

        tagCompound.putInt(CHICKEN_LAY_ITEM_ID, getLayItemId());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tagCompound) {
        super.readAdditionalSaveData(tagCompound);
        setChickenTypeInternal(tagCompound.getString(TYPE_NBT));
        setStatsAnalyzed(tagCompound.getBoolean(CHICKEN_STATS_ANALYZED_NBT));
        setGrowth(getStatusValue(tagCompound, CHICKEN_GROWTH_NBT));
        setGain(getStatusValue(tagCompound, CHICKEN_GAIN_NBT));
        setStrength(getStatusValue(tagCompound, CHICKEN_STRENGTH_NBT));
        setLayItemId(tagCompound.getInt(CHICKEN_LAY_ITEM_ID));
        updateLayProgress();
    }

    @Override
    public int getAmbientSoundInterval() {
        return super.getAmbientSoundInterval();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState p_180429_2_) {
        if (this.random.nextFloat() > 0.1) {
            return;
        }
        super.playStepSound(pos, p_180429_2_);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int lootingModifier, boolean wasRecentlyHit) {
        super.dropCustomDeathLoot(source,lootingModifier,wasRecentlyHit);
        if (this.isOnFire()) {
            this.spawnAtLocation(Items.COOKED_CHICKEN, 1);
        } else {
            this.spawnAtLocation(Items.CHICKEN, 1);
        }
    }


    @Override
    public void aiStep() {
        if (!this.level.isClientSide && !this.isBaby() && !this.isChickenJockey()) {
            int newTimeUntilNextEgg = eggTime - 1;
            setTimeUntilNextEgg(newTimeUntilNextEgg);
            if (newTimeUntilNextEgg <= 1) {
                Item item = getItem(getLayItemId());
                ItemStack stack =  new ItemStack(item);

                int gain = getGain();
                if (gain >= 5) {
                    stack.grow(stack.getCount());
                }
                if (gain >= 10) {
                    stack.grow(stack.getCount());
                }

                //itemToLay = TileEntityHenhouse.pushItemStack(itemToLay, world, new Vec3d(posX, posY, posZ));

                if (!stack.isEmpty()) {
                    spawnAtLocation(stack, 0.5f);
                    playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                }

                resetTimeUntilNextEgg();
            }
        }
        super.aiStep();
    }

    @Override
    public ChickenEntity getBreedOffspring(ServerWorld world, AgeableEntity entity) {
        return super.getBreedOffspring(world, entity);
    }
}

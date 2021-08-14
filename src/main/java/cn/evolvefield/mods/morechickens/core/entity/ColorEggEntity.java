package cn.evolvefield.mods.morechickens.core.entity;

import cn.evolvefield.mods.morechickens.core.entity.util.ChickenType;
import cn.evolvefield.mods.morechickens.init.ModDefaultEntities;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;

import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fmllegacy.network.FMLPlayMessages;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class ColorEggEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<String> ITEM_ID = SynchedEntityData.defineId(ColorEggEntity.class, EntityDataSerializers.STRING);


    private int spawnChance;
    private int manySpawnChance;
    private String animal;
    private ChickenType breed;

    public ColorEggEntity(EntityType<? extends ColorEggEntity> type, net.minecraft.world.level.Level world) {
        super(type,world);
    }
    public ColorEggEntity(FMLPlayMessages.SpawnEntity packet, Level worldIn){
        super(ModDefaultEntities.COLOR_EGG.get(), worldIn);
    }

    public ColorEggEntity setEgg(
            String itemID,
            int spawnChance,
            int manySpawnChance,
            String animalType){
        entityData.set(ITEM_ID, itemID);
        this.spawnChance = spawnChance;
        this.manySpawnChance = manySpawnChance;
        this.animal = animalType;
        return this;
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(ITEM_ID, "minecraft:egg");
    }




    @Override
    protected Item getDefaultItem() {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(entityData.get(ITEM_ID)));
    }

    /**
     * Display particles on destroy
     */
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for(int i = 0; i < 8; ++i) {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()) {
                }, this.getX(), this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }


    /**
     * Damage hit entity
     */
    @Override
    protected void onHitEntity(EntityHitResult p_213868_1_) {
        super.onHitEntity(p_213868_1_);
        p_213868_1_.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);

    }




    /**
     * Called when this egg hits a block or entity.
     */


    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level.isClientSide) {
            if (this.random.nextInt(Math.max(spawnChance, 1)) == 0) {
                int i = 1;
                if (this.random.nextInt(Math.max(manySpawnChance, 1)) == 0) {
                    i = 4;
                }

                for(int j = 0; j < i; ++j) {
                    //AnimalEntity animalEntity = ((EntityType<? extends AnimalEntity>) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(animal))).create(this.level);
                    //BaseChickenEntity chickenEntity = ModEntities.BASE_CHICKEN.get().create(this.level);
                    //assert animalEntity != null;
                    //animalEntity.ageUp(-24000);
                    //animalEntity.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
                    this.level.getServer().getCommands().performCommand(new CommandSourceStack(CommandSource.NULL, new Vec3(getX(),getY(),getZ()),  Vec2.ZERO,(ServerLevel) level,4, "",
                            new TextComponent(""), Objects.requireNonNull(level.getServer()), null),"summon chickens:base_chicken ~ ~ ~ {Breed:'"+ animal +"'}");
                    //this.level.addFreshEntity(animalEntity);
                }
            }

            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }


    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if(nbt.contains("Spawn"))
            spawnChance = nbt.getInt("Spawn");
        if(nbt.contains("Extra"))
            manySpawnChance = nbt.getInt("Extra");
        if(nbt.contains("Animal"))
            animal = nbt.getString("Animal");
        if(nbt.contains("Item"))
            entityData.set(ITEM_ID, nbt.getString("Item"));
    }


    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Chance", spawnChance);
        nbt.putInt("Extra", manySpawnChance);
        nbt.putString("Animal", animal);
        nbt.putString("Item", entityData.get(ITEM_ID));
    }



    public Packet<?> createSpawnPacket(){
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}

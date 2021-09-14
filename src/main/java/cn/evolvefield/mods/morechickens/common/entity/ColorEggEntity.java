package cn.evolvefield.mods.morechickens.common.entity;

import cn.evolvefield.mods.morechickens.init.ModEntities;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class ColorEggEntity extends ProjectileItemEntity {
    private static final DataParameter<String> ITEM_ID = EntityDataManager.defineId(ColorEggEntity.class, DataSerializers.STRING);


    private int spawnChance;
    private int manySpawnChance;
    private String animal;


    public ColorEggEntity(EntityType<? extends ColorEggEntity> type, World world) {
        super(type,world);
    }
    public ColorEggEntity(FMLPlayMessages.SpawnEntity packet, World worldIn){
        super(ModEntities.COLOR_EGG.get(), worldIn);
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
    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for(int i = 0; i < 8; ++i) {
                this.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }


    /**
     * Damage hit entity
     */
    @Override
    protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
        super.onHitEntity(p_213868_1_);
        p_213868_1_.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);

    }

    /**
     * Called when this egg hits a block or entity.
     */
    @Override
    protected void onHit(RayTraceResult result) {
        super.onHit(result);
        if (!this.level.isClientSide) {
            if (this.random.nextInt(Math.max(spawnChance, 1)) == 0) {
                int i = 1;
                if (this.random.nextInt(Math.max(manySpawnChance, 1)) == 0) {
                    i = 4;
                }

                for(int j = 0; j < i; ++j) {

                    this.level.getServer().getCommands().performCommand(new CommandSource(ICommandSource.NULL, new Vector3d(getX(),getY(),getZ()), Vector2f.ZERO,(ServerWorld) level,4, "",
                            new StringTextComponent(""), Objects.requireNonNull(level.getServer()), null),"summon chickens:base_chicken ~ ~ ~ {Name:'"+ animal +"'}");

                }
            }

            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove();
        }
    }


    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
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
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Chance", spawnChance);
        nbt.putInt("Extra", manySpawnChance);
        nbt.putString("Animal", animal);
        nbt.putString("Item", entityData.get(ITEM_ID));
    }


}

package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.morechickens.common.entity.ColorEggEntity;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ColorEggItem extends Item {

    private static final List<ColorEggItem> EGG_CHICKEN = new ArrayList<>();

    private int spawnChance;
    private int multiSpawnChance;
    private final String animal;
    private final String itemID;

    public ColorEggItem(Properties properties, int spawnChance, int multiSpawnChance, String animal, String itemID) {
        super(properties);
        this.spawnChance = spawnChance;
        this.multiSpawnChance = multiSpawnChance;
        this.animal = animal;
        this.itemID = itemID;
        EGG_CHICKEN.add(this);
    }

    public void updateOdds(int chance, int multi){
        spawnChance = chance;
        multiSpawnChance = multi;
    }




    /**
     * Throw the egg
     */
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isClientSide) {
            ColorEggEntity eggentity = ModEntities.COLOR_EGG.get().create(worldIn);
            if(eggentity != null) {

                eggentity.setEgg(itemID, spawnChance, multiSpawnChance, animal);
                eggentity.setItem(itemstack);
                eggentity.setPos(playerIn.getX(), playerIn.getEyeY() - 0.1, playerIn.getZ());
                eggentity.shootFromRotation(playerIn, playerIn.xRot, playerIn.yRot, 0.0F, 1.5F, 1.0F);
                worldIn.addFreshEntity(eggentity);
            }
        }

        playerIn.awardStat(Stats.ITEM_USED.get(this));
        if (!playerIn.isCreative()) {
            itemstack.shrink(1);
        }

        return ActionResult.sidedSuccess(itemstack, worldIn.isClientSide);
    }



    public static void registerDispenser(){
        ProjectileDispenseBehavior behavior = new ProjectileDispenseBehavior(){
            @Override
            protected ProjectileEntity getProjectile(World worldIn, IPosition position, ItemStack stackIn) {
                ColorEggItem eggItem = (ColorEggItem)stackIn.getItem();
                ColorEggEntity entity = ModEntities.COLOR_EGG.get().create(worldIn);
                if(entity != null) {
                    entity.setEgg(eggItem.itemID, eggItem.spawnChance, eggItem.multiSpawnChance, eggItem.animal);
                    entity.setPos(position.x(), position.y(), position.z());
                }
                return entity;
            }
        };
        for(ColorEggItem egg : EGG_CHICKEN){
            DispenserBlock.registerBehavior(egg, behavior);
        }
    }
}

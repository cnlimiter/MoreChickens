package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.morechickens.common.entity.ColorEggEntity;
import cn.evolvefield.mods.morechickens.init.registry.ModEntities;
import net.minecraft.Util;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorEggItem extends Item {

    private static final List<ColorEggItem> EGG_CHICKEN = new ArrayList<>();

    private int spawnChance;
    private int multiSpawnChance;
    private final String animal;
    private final String itemID;
    Random random= new Random();

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
     * @return
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isClientSide) {
            ColorEggEntity eggentity = ModEntities.COLOR_EGG.get().create(worldIn);
            if(eggentity != null) {

                eggentity.setEgg(itemID, spawnChance, multiSpawnChance, animal);
                eggentity.setItem(itemstack);
                eggentity.setPos(playerIn.getX(), playerIn.getEyeY() - 0.1, playerIn.getZ());
                eggentity.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.0F, 1.5F, 1.0F);
                worldIn.addFreshEntity(eggentity);
            }
        }

        playerIn.awardStat(Stats.ITEM_USED.get(this));
        if (!playerIn.isCreative()) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide);
    }






    public static void registerDispenser() {


        DefaultDispenseItemBehavior defaultdispenseitembehavior = new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn) {
                return Util.make(new ColorEggEntity(ModEntities.COLOR_EGG.get(), worldIn), (colorEggEntity) -> {
                    colorEggEntity.setItem(stackIn);
                    colorEggEntity.setEgg(((ColorEggItem) stackIn.getItem()).itemID, ((ColorEggItem) stackIn.getItem()).spawnChance, ((ColorEggItem) stackIn.getItem()).multiSpawnChance, ((ColorEggItem) stackIn.getItem()).animal);
                    colorEggEntity.setPos(position.x(), position.y(), position.z());
                });
            }
        };

        for (ColorEggItem egg : EGG_CHICKEN) {
            DispenserBlock.registerBehavior(egg, defaultdispenseitembehavior);
        }

    }


}

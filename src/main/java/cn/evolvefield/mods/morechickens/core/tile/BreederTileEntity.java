package cn.evolvefield.mods.morechickens.core.tile;


import cn.evolvefield.mods.morechickens.MoreChickens;

import cn.evolvefield.mods.morechickens.core.container.BreederContainer;
import cn.evolvefield.mods.morechickens.core.data.DataChicken;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import cn.evolvefield.mods.morechickens.init.ModTileEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class BreederTileEntity extends ChickenContainerTileEntity implements INamedContainerProvider {

    private static final String CHICKEN_0_KEY = "Chicken0";
    private static final String CHICKEN_1_KEY = "Chicken1";
    private static final String COMPLETE_KEY = "Complete";
    private static final String HAS_SEEDS_KEY = "HasSeeds";

    public BreederTileEntity() {
        super(ModTileEntities.TILE_BREEDER);
    }

    private void playSpawnSound() {
        getLevel().playSound(null, getBlockPos(), SoundEvents.CHICKEN_EGG, SoundCategory.NEUTRAL, 0.5F, 0.8F);
    }

    private void spawnParticles() {
        spawnParticle(-0.1d, 0.5d, 0, 0.2d);
        spawnParticle(0.5d, -0.1d, 0.2d, 0);
        spawnParticle(1.1d, 0.5d, 0, 0.2d);
        spawnParticle(0.5d, 1.1d, 0.2d, 0);
    }

    private void spawnParticle(double x, double z, double xOffset, double zOffset) {
        if (getLevel() instanceof ServerWorld) {
            ServerWorld worldServer = (ServerWorld) getLevel();
            worldServer.addParticle(ParticleTypes.HEART, false,getBlockPos().getX() + x, getBlockPos().getY() + 0.5d, getBlockPos().getZ() + z,  xOffset, 0.2d, zOffset);
        }
    }

    public void addInfoToTooltip(List<String> tooltip, CompoundNBT tag) {
        if (tag.contains(CHICKEN_0_KEY)) {
            DataChicken chicken = DataChicken.getDataFromTooltipNBT(tag.getCompound(CHICKEN_0_KEY));
            tooltip.add(chicken.getDisplaySummary());
        }
        if (tag.contains(CHICKEN_1_KEY)) {
            DataChicken chicken = DataChicken.getDataFromTooltipNBT(tag.getCompound(CHICKEN_1_KEY));
            tooltip.add(chicken.getDisplaySummary());
        }
        if (tag.contains(COMPLETE_KEY)) {
            tooltip.add(new TranslationTextComponent("container.roost.progress", formatProgress(tag.getDouble(COMPLETE_KEY))).getString());
            if (!tag.getBoolean(HAS_SEEDS_KEY)) {
                tooltip.add(TextFormatting.RED + new TranslationTextComponent("container.roost.breeder.seedless").getString());
            }
        }
    }

    public void storeInfoForTooltip(CompoundNBT tag) {
        DataChicken chicken0 = getChickenData(0);
        DataChicken chicken1 = getChickenData(1);
        if (chicken0 != null) tag.put(CHICKEN_0_KEY, chicken0.buildTooltipNBT());
        if (chicken1 != null) tag.put(CHICKEN_1_KEY, chicken1.buildTooltipNBT());
        if (chicken0 != null && chicken1 != null) {
            tag.putDouble(COMPLETE_KEY, getProgress());
            tag.putBoolean(HAS_SEEDS_KEY, isFullOfSeeds());
        }
    }


    @Override
    protected void spawnChickenDrop() {
        DataChicken left = getChickenData(0);
        DataChicken right = getChickenData(1);
        if (left != null && right != null) {
            putStackInOutput(left.createChildStack(right, getLevel()));
            playSpawnSound();
            spawnParticles();
        }
    }

    @Override
    public int getContainerSize() {
        return 6;
    }

    @Override
    protected int getSizeChickenInventory() {
        return 2;
    }

    @Override
    protected int requiredSeedsForDrop() {
        return 2;
    }

    @Override
    protected double speedMultiplier() {
        return ModConfig.COMMON.breederSpeed.get();
    }



    public String getName() {
        return "container." + MoreChickens.MODID + ".breeder";
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(getName());
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new BreederContainer(id,playerInventory,this);
    }
}

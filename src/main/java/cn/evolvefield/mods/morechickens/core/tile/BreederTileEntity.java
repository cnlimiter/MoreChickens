package cn.evolvefield.mods.morechickens.core.tile;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.container.ContainerBreeder;
import cn.evolvefield.mods.morechickens.core.data.DataChicken;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import cn.evolvefield.mods.morechickens.init.ModTileEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class BreederTileEntity extends ChickenContainerTileEntity implements MenuProvider {

    private static final String CHICKEN_0_KEY = "Chicken0";
    private static final String CHICKEN_1_KEY = "Chicken1";
    private static final String COMPLETE_KEY = "Complete";
    private static final String HAS_SEEDS_KEY = "HasSeeds";

    public BreederTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.TILE_BREEDER,pos,state);
    }

    private void playSpawnSound() {
        getLevel().playSound(null, getBlockPos(), SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 0.5F, 0.8F);
    }

    private void spawnParticles() {
        spawnParticle(-0.1d, 0.5d, 0, 0.2d);
        spawnParticle(0.5d, -0.1d, 0.2d, 0);
        spawnParticle(1.1d, 0.5d, 0, 0.2d);
        spawnParticle(0.5d, 1.1d, 0.2d, 0);
    }

    private void spawnParticle(double x, double z, double xOffset, double zOffset) {
        if (getLevel() instanceof ServerLevel) {
            ServerLevel worldServer = (ServerLevel) getLevel();
            worldServer.addParticle(ParticleTypes.HEART, false,getBlockPos().getX() + x, getBlockPos().getY() + 0.5d, getBlockPos().getZ() + z,  xOffset, 0.2d, zOffset);
        }
    }

    public void addInfoToTooltip(List<String> tooltip, CompoundTag tag) {
        if (tag.contains(CHICKEN_0_KEY)) {
            DataChicken chicken = DataChicken.getDataFromTooltipNBT(tag.getCompound(CHICKEN_0_KEY));
            tooltip.add(chicken.getDisplaySummary());
        }
        if (tag.contains(CHICKEN_1_KEY)) {
            DataChicken chicken = DataChicken.getDataFromTooltipNBT(tag.getCompound(CHICKEN_1_KEY));
            tooltip.add(chicken.getDisplaySummary());
        }
        if (tag.contains(COMPLETE_KEY)) {
            tooltip.add(new TranslatableComponent("container.roost.progress", formatProgress(tag.getDouble(COMPLETE_KEY))).getString());
            if (!tag.getBoolean(HAS_SEEDS_KEY)) {
                tooltip.add(ChatFormatting.RED + new TranslatableComponent("container.roost.breeder.seedless").getString());
            }
        }
    }

    public void storeInfoForTooltip(CompoundTag tag) {
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
    public Component getDisplayName() {
        return new TranslatableComponent(getName());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new ContainerBreeder(id,playerInventory,this);
    }
}


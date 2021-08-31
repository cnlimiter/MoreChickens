package cn.evolvefield.mods.morechickens.core.entity.util.main;

import net.minecraft.nbt.CompoundNBT;

import java.util.Random;

public class Gene {
    private static final float MUTATION_SIGMA = 0.05f;

    public float layAmount;
    public float layRandomAmount;
    public float layTime;
    public float layRandomTime;
    public float dominance;

    public Gene(Random random){
        layAmount = random.nextFloat() * 1.5f;
        layRandomAmount = random.nextFloat();
        layTime = random.nextFloat() * 1.5f;
        layRandomTime = random.nextFloat();
        dominance = random.nextFloat();
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
        return child;
    }

    public Gene readFromTag(CompoundNBT nbt){
        layAmount = nbt.getFloat("LayAmount");
        layRandomAmount = nbt.getFloat("LayRandomAmount");
        layTime = nbt.getFloat("LayTime");
        layRandomTime = nbt.getFloat("LayRandomTime");
        dominance = nbt.getFloat("Dominance");
        return this;
    }

    public CompoundNBT writeToTag(){
        CompoundNBT nbt = new CompoundNBT();
        nbt.putFloat("LayAmount", layAmount);
        nbt.putFloat("LayRandomAmount", layRandomAmount);
        nbt.putFloat("LayTime", layTime);
        nbt.putFloat("LayRandomTime", layRandomTime);
        nbt.putFloat("Dominance", dominance);
        return nbt;
    }
}

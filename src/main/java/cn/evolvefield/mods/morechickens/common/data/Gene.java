package cn.evolvefield.mods.morechickens.common.data;

import net.minecraft.nbt.CompoundNBT;

import java.util.Random;

public class Gene {
    private static final float MUTATION_SIGMA = 0.05f;

    public float layAmount;
    public float layTime;
    public int GAIN;
    public int GROWTH;
    public int STRENGTH; //gene strength

    public Gene(Random random){
        layAmount = random.nextFloat() * 1.5f;
        layTime = random.nextFloat() * 1.5f;
        GAIN = 1;
        GROWTH = 1;
        STRENGTH = 1;
    }

    public Gene(){
    }


    private static int calculateNewStat(int thisStrength, int mateStrength, int stat1, int stat2, Random rand) {
        int mutation = rand.nextInt(2) + 1;
        int newStatValue = (stat1 * thisStrength + stat2 * mateStrength) / (thisStrength + mateStrength + 1) + mutation;
        if (newStatValue <= 1) return 1;

        return Math.min(newStatValue, 10);
    }


    public Gene crossover(Gene other, Random random){
        Gene child = new Gene();
        child.layAmount = Math.max(0, random.nextBoolean() ? layAmount : other.layAmount + (float)random.nextGaussian() * MUTATION_SIGMA);
        child.layTime = Math.max(0, random.nextBoolean() ? layTime : other.layTime + (float)random.nextGaussian() * MUTATION_SIGMA);
        child.GAIN = calculateNewStat(STRENGTH, other.STRENGTH, GAIN, other.GAIN, random );
        child.GROWTH = calculateNewStat(STRENGTH, other.STRENGTH, GROWTH, other.GROWTH, random );
        child.STRENGTH = calculateNewStat(STRENGTH, other.STRENGTH, STRENGTH, other.STRENGTH, random );
        return child;
    }

    public Gene readFromTag(CompoundNBT nbt){
        layAmount = nbt.getFloat("LayAmount");
        layTime = nbt.getFloat("LayTime");
        GAIN = nbt.getInt("ChickenGain");
        GROWTH = nbt.getInt("ChickenGrowth");
        STRENGTH = nbt.getInt("ChickenStrength");
        return this;
    }

    public CompoundNBT writeToTag(){
        CompoundNBT nbt = new CompoundNBT();
        nbt.putFloat("LayAmount", layAmount);
        nbt.putFloat("LayTime", layTime);
        nbt.putFloat("ChickenGain", GAIN);
        nbt.putFloat("ChickenGrowth", GROWTH);
        nbt.putFloat("ChickenStrength", STRENGTH);
        return nbt;
    }
}

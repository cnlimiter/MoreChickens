package cn.evolvefield.mods.morechickens.common.entity.core;

import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public class Gene {

        private static final float MUTATION_SIGMA = 0.05f;

        public float layCount;
        public float layRandomCount;
        public float layTime;
        public float layRandomTime;
        public float dominance;


        public Gene(Random random){
            layCount = random.nextFloat() * 1.5f;
            layRandomCount = random.nextFloat();
            layTime = random.nextFloat() * 1.5f;
            layRandomTime = random.nextFloat();
            dominance = random.nextFloat();

        }

        public Gene(){
        }

        public Gene crossover(Gene other, Random random){
            Gene child = new Gene();
            child.layCount = Math.max(0, random.nextBoolean() ? layCount : other.layCount + (float)random.nextGaussian() * MUTATION_SIGMA);
            child.layRandomCount = Math.max(0, random.nextBoolean() ? layRandomCount : other.layRandomCount + (float)random.nextGaussian() * MUTATION_SIGMA);
            child.layTime = Math.max(0, random.nextBoolean() ? layTime : other.layTime + (float)random.nextGaussian() * MUTATION_SIGMA);
            child.layRandomTime = Math.max(0, random.nextBoolean() ? layRandomTime : other.layRandomTime + (float)random.nextGaussian() * MUTATION_SIGMA);
            child.dominance = random.nextBoolean() ? dominance : other.dominance + (float)random.nextGaussian() * MUTATION_SIGMA;

            return child;
        }

        public Gene readFromTag(CompoundTag nbt){
            layCount = nbt.getFloat("layCount");
            layRandomCount = nbt.getFloat("layRandomCount");
            layTime = nbt.getFloat("layTime");
            layRandomTime = nbt.getFloat("layRandomTime");
            dominance = nbt.getFloat("dominance");

            return this;
        }

        public CompoundTag writeToTag(){
            CompoundTag nbt = new CompoundTag();
            nbt.putFloat("layCount", layCount);
            nbt.putFloat("layRandomCount", layRandomCount);
            nbt.putFloat("layTime", layTime);
            nbt.putFloat("layRandomTime", layRandomTime);
            nbt.putFloat("dominance", dominance);

            return nbt;
        }


}

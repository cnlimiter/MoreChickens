package cn.evolvefield.mods.morechickens.core.entity.util;

import cn.evolvefield.mods.morechickens.core.entity.BaseChickenEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.Random;

public class VirtualChicken {
    public final ChickenType breed;
    public final BaseChickenEntity.Gene gene;
    private final BaseChickenEntity.Gene alleleA;
    private final BaseChickenEntity.Gene alleleB;
    private final CompoundNBT extraNBT;
    public float layTimer;
    public VirtualChicken(CompoundNBT nbt){
        extraNBT = nbt.copy();
        breed = ChickenType.Types.get(extraNBT.getString("Breed"));
        alleleA = new BaseChickenEntity.Gene().readFromTag(extraNBT.getCompound("AlleleA"));
        alleleB = new BaseChickenEntity.Gene().readFromTag(extraNBT.getCompound("AlleleB"));
        layTimer = extraNBT.getInt("EggLayTime");
        extraNBT.remove("EggLayTime");
        extraNBT.remove("Breed");
        extraNBT.remove("AlleleA");
        extraNBT.remove("AlleleB");
        gene = alleleA.dominance >= alleleB.dominance ? alleleA : alleleB;
    }

    public CompoundNBT writeToTag(){
        CompoundNBT nbt = extraNBT.copy();
        nbt.putString("Breed", breed.name);
        nbt.putInt("EggLayTime", (int)layTimer);
        nbt.put("AlleleA", alleleA.writeToTag());
        nbt.put("AlleleB", alleleB.writeToTag());
        return nbt;
    }

    public void resetTimer(Random rand){
        layTimer = breed.layTime + rand.nextInt(breed.layTime + 1);
        layTimer *=  gene.layTime + rand.nextFloat() * gene.layRandomTime;
        layTimer = Math.max(600, layTimer);
    }
}

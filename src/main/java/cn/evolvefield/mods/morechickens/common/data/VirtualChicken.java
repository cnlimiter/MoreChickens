package cn.evolvefield.mods.morechickens.common.data;

import net.minecraft.nbt.CompoundNBT;

import java.util.Random;

public class VirtualChicken {
    public final ChickenData breed;
    public final Gene gene;
    private final Gene alleleA;
    private final Gene alleleB;
    private final CompoundNBT extraNBT;
    public float layTimer;
    public VirtualChicken(CompoundNBT nbt){
        extraNBT = nbt.copy();
        breed = ChickenRegistry.Types.get(extraNBT.getString("Name"));
        alleleA = new Gene().readFromTag(extraNBT.getCompound("AlleleA"));
        alleleB = new Gene().readFromTag(extraNBT.getCompound("AlleleB"));
        layTimer = extraNBT.getInt("EggLayTime");
        extraNBT.remove("EggLayTime");
        extraNBT.remove("Name");
        extraNBT.remove("AlleleA");
        extraNBT.remove("AlleleB");
        gene = alleleA.STRENGTH >= alleleB.STRENGTH ? alleleA : alleleB;
    }

    public CompoundNBT writeToTag(){
        CompoundNBT nbt = extraNBT.copy();
        nbt.putString("Name", breed.name);
        nbt.putInt("EggLayTime", (int)layTimer);
        nbt.put("AlleleA", alleleA.writeToTag());
        nbt.put("AlleleB", alleleB.writeToTag());
        return nbt;
    }

    public void resetTimer(Random rand){
        layTimer = breed.layTime + rand.nextInt(breed.layTime + 1);
        layTimer = Math.max(600, layTimer);
    }
}

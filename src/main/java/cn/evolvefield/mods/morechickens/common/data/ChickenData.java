package cn.evolvefield.mods.morechickens.common.data;

import cn.evolvefield.mods.morechickens.common.util.math.RandomPool;
import cn.evolvefield.mods.morechickens.common.util.math.UnorderedPair;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static cn.evolvefield.mods.morechickens.common.data.ChickenRegistry.Pairings;

public class ChickenData {

    public String name;
    //common
    public String layItem;
    public String deathItem;
    public int deathAmount;
    public int layAmount;
    public int layTime;
    //gene
    public int GAIN;
    public int GROWTH;
    public int STRENGTH;

    public boolean enabled;
    public String parent1, parent2;
    protected double weight;
    public Pair<String, String> parents;
    public int tier;

    public ChickenData(String name,
                       String itemID, int amt, int time,
                       String die, int dieAmt,
                       int gain, int growth , int strength,
                       boolean isEnabled, double we,
                       String mo, String fa, int level
    ){

        this.name = name;
        this.layItem = itemID;
        this.layAmount = amt;
        this.layTime = time;
        this.deathItem = die;
        this.deathAmount = dieAmt;
        this.GAIN = gain;
        this.GROWTH = growth;
        this.STRENGTH = strength;
        this.enabled = isEnabled;
        this.parent1 = mo;
        this.parent2 = fa;
        this.weight = we;
        this.tier = level;
        this.parents = ChickenUtils.sortParents(fa, mo);
        ChickenRegistry.Types.put(this.name, this);

    }

    public ChickenData(String name){
        this(name, "", 0, 3000, "", 0, 1, 1, 1, true,10, "", "", 0);
    }


    public ChickenData disable(){
        enabled = false;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getParent1() {
        return parent1;
    }

    public String getParent2() {
        return parent2;
    }

    public Pair<String, String> getParents() {
        return parents;
    }

    public double getWeight() {
        return weight;
    }


    public boolean hasParents() {
        return !getParent1().isEmpty() && !getParent2().isEmpty();
    }

    public boolean hasValidParents() {
        return !getParent1().isEmpty() && !getParent2().isEmpty() && ChickenRegistry.containsChickenType(getParent1()) && ChickenRegistry.containsChickenType(getParent2());
    }

    public ChickenData getOffspring(ChickenData other, Random rand){
        UnorderedPair<String> pair = new UnorderedPair<>(name, other.name);
        RandomPool<String> pool = Pairings.getOrDefault(pair, null);
        ChickenData result = pool != null ? ChickenRegistry.Types.get(pool.get(rand.nextFloat())) : null;
        return result != null ? result : rand.nextBoolean() ? this : other;
    }



    public static void matchConfig(){
        List<Float> tiers = Arrays.stream(ModConfig.COMMON.tierOdds)
                .map(ForgeConfigSpec.DoubleValue::get)
                .map(Double::floatValue)
                .collect(Collectors.toList());
        //load config chickens
        for(Map.Entry<String, ChickenData> entry : ChickenRegistry.Types.entrySet()){
            ChickenData type = entry.getValue();
            String key = entry.getKey();
            ModConfig.Common.ChickenTypeConfig configType = ModConfig.COMMON.chickenType.get(key);
            type.layAmount = configType.amount.get();
            type.layTime = configType.time.get();
            type.deathAmount = configType.onDieAmount.get();
            type.layItem = configType.dropItem.get();
            type.deathItem = configType.deathItem.get();
            type.enabled = configType.enabled.get();
            type.parent1 = configType.parent1.get();
            type.parent2 = configType.parent2.get();
            type.tier = configType.tier.get();
            if(type.enabled && !type.parent1.equals("") && !type.parent2.equals("")){
                UnorderedPair<String> pair = new UnorderedPair<>(type.parent1, type.parent2);
                RandomPool<String> pool = Pairings.computeIfAbsent(pair, keyPair -> new RandomPool<>((String)null));
                pool.add(type.name, tiers.get(type.tier));
            }
        }
    }

}

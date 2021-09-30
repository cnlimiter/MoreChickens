package cn.evolvefield.mods.morechickens.common.data;

import cn.evolvefield.mods.morechickens.common.util.math.RandomPool;
import cn.evolvefield.mods.morechickens.common.util.math.UnorderedPair;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ChickenData {
    public static Map<String, ChickenData> Types = new HashMap<>();

    public static Map<UnorderedPair<String>, RandomPool<String>> Pairings = new HashMap<>();

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
    public int tier;

    public ChickenData(String name,
                       String itemID, int amt, int time,
                       String die, int dieAmt,
                       int gain, int growth , int strength,
                       boolean isEnabled,
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
        this.tier = level;
        Types.put(this.name, this);

    }

    public ChickenData(String name){
        this(name, "", 0, 3000, "", 0, 1, 1, 1, true, "", "", 0);
    }

//////////////jei used//////////////

////////////////////////////////

    public ChickenData disable(){
        enabled = false;
        return this;
    }

    public ChickenData getOffspring(ChickenData other, Random rand){
        UnorderedPair<String> pair = new UnorderedPair<>(name, other.name);
        RandomPool<String> pool = Pairings.getOrDefault(pair, null);
        ChickenData result = pool != null ? Types.get(pool.get(rand.nextFloat())) : null;
        return result != null ? result : rand.nextBoolean() ? this : other;
    }



    public static void matchConfig(){
        List<Float> tiers = Arrays.stream(ModConfig.COMMON.tierOdds)
                .map(ForgeConfigSpec.DoubleValue::get)
                .map(Double::floatValue)
                .collect(Collectors.toList());
        //load config chickens
        for(Map.Entry<String, ChickenData> entry : Types.entrySet()){
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

//    public static final ChickenData
//            // Tier 0
//            SAND = new ChickenData("sand", "minecraft:sand", 1, 4800, "minecraft:kelp", 2),
//            OAK = new ChickenData("oak", "minecraft:oak_log", 1,  4800, "minecraft:oak_sapling", 1),
//            FLINT = new ChickenData("flint", "minecraft:flint", 1,  4800, "minecraft:flint", 1),
//            QUARTZ = new ChickenData("quartz", "minecraft:quartz", 1,  4800),
//            SOUL_SAND = new ChickenData("soul_sand", "minecraft:soul_sand", 1,  4800),
//
//            CLAY = new ChickenData("clay", "minecraft:clay_ball", 1,  3000),
//            LEATHER = new ChickenData("leather", "minecraft:leather", 1,  4800, "minecraft:beef", 1),
//            STRING = new ChickenData("string", "minecraft:string", 1,  3000, "minecraft:mutton", 1),
//            SLIME = new ChickenData("slime", "minecraft:slime_balls", 1,  4800),
//
//            MAGMA_CREAM = new ChickenData("magma_cream", "minecraft:magma_cream", 1,  12000),
//
//    // Primary dyes
//            WHITE_DYE = new ChickenData("dye_white", "minecraft:white_dye", 2,  3000),
//                    BLUE_DYE = new ChickenData("dye_blue", "minecraft:blue_dye", 2,  3000),
//                    BLACK_DYE = new ChickenData("dye_black", "minecraft:black_dye", 2,  3000),
//                    MAGENTA_DYE = new ChickenData("dye_magenta", "minecraft:magenta_dye", 2,  3000),
//                    LIME_DYE = new ChickenData("dye_lime", "minecraft:lime_dye", 2,  3000),
//                    YELLOW_DYE = new ChickenData("dye_yellow", "minecraft:yellow_dye", 2,  3000),
//                    PURPLE_DYE = new ChickenData("dye_purple", "minecraft:purple_dye", 2,  3000),
//                    GREEN_DYE = new ChickenData("dye_green", "minecraft:green_dye", 2,  3000),
//                    LIGHT_GRAY_DYE = new ChickenData("dye_light_gray", "minecraft:light_gray_dye", 2,  3000),
//                    LIGHT_BLUE_DYE = new ChickenData("dye_light_blue", "minecraft:light_blue_dye", 2,  3000),
//                    CYAN_DYE  = new ChickenData("dye_cyan", "minecraft:cyan_dye", 2, 3000),
//                    RED_DYE = new ChickenData("dye_red", "minecraft:red_dye", 2,  3000),
//                    PINK_DYE = new ChickenData("dye_pink", "minecraft:pink_dye", 2,  3000),
//                    ORANGE_DYE = new ChickenData("dye_orange", "minecraft:orange_dye", 2,  3000),
//                    GRAY_DYE = new ChickenData("dye_gray", "minecraft:gray_dye", 2,  3000),
//                    BROWN_DYE = new ChickenData("dye_brown", "minecraft:brown_dye", 2,  3000),
//
//
//        REDSTONE = new ChickenData("redstone", "minecraft:redstone", 1,  6000),
//        IRON = new ChickenData("iron", "minecraft:iron_ingot", 1,  6000),
//        GLOWSTONE = new ChickenData("glowstone", "minecraft:glowstone_dust", 1,  6000),
//        COAL = new ChickenData("coal", "minecraft:coal", 1,  6000),
//        GUNPOWDER = new ChickenData("gunpowder", "minecraft:gunpowder", 1,  4800),
//        SNOWBALL = new ChickenData("snowball", "minecraft:snowball", 1,  4800, "minecraft:sweet_berries", 1),
//
//
//        GLASS = new ChickenData("glass", "minecraft:glass", 1,  4800),
//        GOLD = new ChickenData("gold", "minecraft:gold_ingot", 1,  12000),
//        LAVA = new ChickenData("lava", "chickens:ball_lava", 1,  3000),
//        WATER = new ChickenData("water", "chickens:ball_water", 1,  4800),
//
//
//        DIAMOND = new ChickenData("diamond", "minecraft:diamond", 1,  18000),
//        WART = new ChickenData("wart", "minecraft:nether_wart", 1,  6000),
//        BLAZE = new ChickenData("blaze", "minecraft:blaze_rod", 1,  9000),
//        OBSIDIAN = new ChickenData("obsidian", "minecraft:obsidian", 1,  12000),
//        PRISM_SHARD = new ChickenData("prism_shard", "minecraft:prismarine_shard", 1,  18000, "minecraft:prismarine_crystals", 2),
//        PRISM_CRYSTALS = new ChickenData("prism_crystals", "minecraft:prismarine_crystals", 1,  18000),
//
//
//        EMERALD = new ChickenData("emerald", "minecraft:emerald", 1,  24000, "minecraft:saddle", 1),
//        PEARL = new ChickenData("ender_pearl", "minecraft:ender_pearl", 1,  24000),
//        GHAST_TEAR = new ChickenData("ghast_tear", "minecraft:ghast_tear", 1,  16000),
//        NETHERITE_SCRAP = new ChickenData("netherite_scrap", "minecraft:netherite_scrap", 1,  20000,"minecraft:ancient_debris",1)
//
//    ;



//    static {
//
//
////white
//        preRegisterPair(WHITE_DYE, GREEN_DYE, LIME_DYE, (0));
//        preRegisterPair(WHITE_DYE, BLACK_DYE, GRAY_DYE, (0));
//        preRegisterPair(WHITE_DYE, BLUE_DYE, LIGHT_BLUE_DYE, (0));
//        preRegisterPair(WHITE_DYE, BLAZE, GHAST_TEAR , (5));
//        preRegisterPair(WHITE_DYE, RED_DYE, PINK_DYE, (0));
//        preRegisterPair(WHITE_DYE, GRAY_DYE, LIGHT_GRAY_DYE, (0));
//        preRegisterPair(WHITE_DYE, FLINT, IRON, (1));
////blue
//        preRegisterPair(BLUE_DYE, WATER, PRISM_SHARD, (4));
//        preRegisterPair(BLUE_DYE, OAK, SNOWBALL, (1));
//        preRegisterPair(BLUE_DYE, RED_DYE, PURPLE_DYE, (0));
//        preRegisterPair(BLUE_DYE, GREEN_DYE, CYAN_DYE, (0));
////black
//        preRegisterPair(BLACK_DYE, OAK, STRING, (1));
////yellow
//        preRegisterPair(YELLOW_DYE, QUARTZ, GLOWSTONE, (3));
//        preRegisterPair(YELLOW_DYE, RED_DYE, ORANGE_DYE, (0));
//        preRegisterPair(YELLOW_DYE, IRON, GOLD, (3));
////purple
//        preRegisterPair(PURPLE_DYE, PINK_DYE, MAGENTA_DYE, (0));
////green
//        preRegisterPair(GREEN_DYE, DIAMOND, EMERALD, (4));
//        preRegisterPair(GREEN_DYE, CLAY, SLIME, (2));
//        preRegisterPair(GREEN_DYE, RED_DYE, BROWN_DYE, (0));
////red
//        preRegisterPair(RED_DYE, SAND, REDSTONE, (2));
////brown
//        preRegisterPair(BROWN_DYE, STRING, LEATHER, (2));
//        preRegisterPair(BROWN_DYE, GLOWSTONE, WART, (4));
////sand
//        preRegisterPair(SAND, FLINT, GUNPOWDER, (2));
//        preRegisterPair(SAND, SNOWBALL, CLAY, (2));
//        preRegisterPair(SAND, COAL, GLASS, (3));
////oak
//        preRegisterPair(OAK, FLINT, COAL, (2));
////quartz
//        preRegisterPair(QUARTZ, COAL, LAVA, (3));
////blaze
//        preRegisterPair(BLAZE, SLIME, MAGMA_CREAM, (4));
////water
//        preRegisterPair(WATER, EMERALD, PRISM_CRYSTALS, (5));
//        preRegisterPair(WATER, LAVA, OBSIDIAN, (4));
////gold
//        preRegisterPair(GOLD, GLASS, DIAMOND, (5));
////diamond
//        preRegisterPair(DIAMOND, WART, PEARL, (5));
////pearl
//        preRegisterPair(PEARL,GHAST_TEAR,NETHERITE_SCRAP,(6));
//
//    }

}

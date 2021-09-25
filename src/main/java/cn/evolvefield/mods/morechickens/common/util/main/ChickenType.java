package cn.evolvefield.mods.morechickens.common.util.main;

import cn.evolvefield.mods.morechickens.common.util.math.RandomPool;
import cn.evolvefield.mods.morechickens.common.util.math.UnorderedPair;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class ChickenType {
    public static Map<String, ChickenType> Types = new HashMap<>();
    public static Map<UnorderedPair<String>, RandomPool<String>> Pairings = new HashMap<>();

    public static void preRegisterPair(ChickenType mother, ChickenType father, ChickenType child, int tier){
        child.parent1 = mother.name;
        child.parent2 = father.name;
        child.tier = tier;
    }

    public String name;
    public String layItem;
    public String deathItem;
    public int deathAmount;
    public int layAmount;
    public int layRandomAmount;
    public int layTime;
    public boolean enabled;
    public String parent1, parent2;
    public int tier;

    public ChickenType(String name, String itemID, int amt, int rAmt, int time,
                       String die, int dieAmt, boolean isEnabled,
                       String mo, String fa, int level
    ){

        this.name = name;
        this.layItem = itemID;
        this.layAmount = amt;
        this.layRandomAmount = rAmt;
        this.layTime = time;
        this.deathItem = die;
        this.deathAmount = dieAmt;
        this.enabled = isEnabled;
        this.parent1 = mo;
        this.parent2 = fa;
        this.tier = level;
        Types.put(this.name, this);

    }

    public ChickenType(String name, String itemID, int amt, int rAmt, int time, String die, int dieAmt){
        this(name, itemID, amt, rAmt, time, die, dieAmt,true,"","",0);
    }

    public ChickenType(String name, String itemID, int amt, int rAmt, int time){
        this(name, itemID, amt, rAmt, time, "", 0);
    }

    public ChickenType disable(){
        enabled = false;
        return this;
    }

    public ChickenType getOffspring(ChickenType other, Random rand){
        UnorderedPair<String> pair = new UnorderedPair<>(name, other.name);
        RandomPool<String> pool = Pairings.getOrDefault(pair, null);
        ChickenType result = pool != null ? Types.get(pool.get(rand.nextFloat())) : null;
        return result != null ? result : rand.nextBoolean() ? this : other;
    }


    public  String getChickenTypeName(){
        return name;
    }

    public static Item getItem(String id, Random rand){
        Item item;
        if("#@".contains(id.substring(0, 1))){
            Tag<Item> tag = ItemTags.getAllTags().getTag(new ResourceLocation(id.substring(1)));
            if(tag == null)
                return null;
            List<Item> items = tag.getValues();
            if(items.isEmpty())
                return null;
            if(id.charAt(0) == '#') // First item
                item = items.get(0);
            else // Random item
                item = tag.getRandomElement(rand);
        }
        else
            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        return item;
    }

    public ItemStack getLoot(Random rand, Gene gene){
        Item item = getItem(layItem, rand);
        if(item == null)
            return ItemStack.EMPTY;
        int amount = layAmount + rand.nextInt(layRandomAmount + 1);
        amount = Math.max(1, (int)(amount * (gene.layAmount + rand.nextFloat() * gene.layAmount)));
        ItemStack itemStack = new ItemStack(item, amount);
        if(item == Items.BOOK){
            itemStack = EnchantmentHelper.enchantItem(rand, itemStack, 30, true);
        }
        return itemStack;
    }

    public static void matchConfig(){
        List<Float> tiers = Arrays.stream(ModConfig.COMMON.tierOdds)
                .map(ForgeConfigSpec.DoubleValue::get)
                .map(Double::floatValue)
                .collect(Collectors.toList());
        for(Map.Entry<String, ChickenType> entry : Types.entrySet()){
            ChickenType type = entry.getValue();
            String key = entry.getKey();
            ModConfig.Common.ChickenTypeConfig configType = ModConfig.COMMON.chickenType.get(key);
            type.layAmount = configType.amount.get();
            type.layRandomAmount = configType.amountRand.get();
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

    public static final ChickenType
            // Tier 0
            SAND = new ChickenType("sand", "minecraft:sand", 1, 0, 4800, "minecraft:kelp", 2),
            OAK = new ChickenType("oak", "minecraft:oak_log", 1, 0, 4800, "minecraft:oak_sapling", 1),
            FLINT = new ChickenType("flint", "minecraft:flint", 1, 0, 4800, "minecraft:flint", 1),
            QUARTZ = new ChickenType("quartz", "minecraft:quartz", 1, 0, 4800),
            SOUL_SAND = new ChickenType("soul_sand", "minecraft:soul_sand", 1, 0, 4800),

            CLAY = new ChickenType("clay", "minecraft:clay_ball", 1, 2, 3000),
            LEATHER = new ChickenType("leather", "minecraft:leather", 1, 0, 4800, "minecraft:beef", 1),
            STRING = new ChickenType("string", "minecraft:string", 1, 0, 3000, "minecraft:mutton", 1),
            SLIME = new ChickenType("slime", "minecraft:slime_balls", 1, 0, 6000),

            MAGMA_CREAM = new ChickenType("magma_cream", "minecraft:magma_cream", 1, 0, 12000),


    // Primary dyes
            WHITE_DYE = new ChickenType("dye_white", "minecraft:white_dye", 2, 2, 3000),
                    BLUE_DYE = new ChickenType("dye_blue", "minecraft:blue_dye", 2, 2, 3000),
                    BLACK_DYE = new ChickenType("dye_black", "minecraft:black_dye", 2, 2, 3000),
                    MAGENTA_DYE = new ChickenType("dye_magenta", "minecraft:magenta_dye", 2, 2, 3000),
                    LIME_DYE = new ChickenType("dye_lime", "minecraft:lime_dye", 2, 2, 3000),
                    YELLOW_DYE = new ChickenType("dye_yellow", "minecraft:yellow_dye", 2, 2, 3000),
                    PURPLE_DYE = new ChickenType("dye_purple", "minecraft:purple_dye", 2, 2, 3000),
                    GREEN_DYE = new ChickenType("dye_green", "minecraft:green_dye", 2, 2, 3000),
                    LIGHT_GRAY_DYE = new ChickenType("dye_light_gray", "minecraft:light_gray_dye", 2, 2, 3000),
                    LIGHT_BLUE_DYE = new ChickenType("dye_light_blue", "minecraft:light_blue_dye", 2, 2, 3000),
                    CYAN_DYE  = new ChickenType("dye_cyan", "minecraft:cyan_dye", 2, 2, 3000),
                    RED_DYE = new ChickenType("dye_red", "minecraft:red_dye", 2, 2, 3000),
                    PINK_DYE = new ChickenType("dye_pink", "minecraft:pink_dye", 2, 2, 3000),
                    ORANGE_DYE = new ChickenType("dye_orange", "minecraft:orange_dye", 2, 2, 3000),
                    GRAY_DYE = new ChickenType("dye_gray", "minecraft:gray_dye", 2, 2, 3000),
                    BROWN_DYE = new ChickenType("dye_brown", "minecraft:brown_dye", 2, 2, 3000),


        REDSTONE = new ChickenType("redstone", "minecraft:redstone", 1, 2, 6000),
        IRON = new ChickenType("iron", "minecraft:iron_ingot", 1, 0, 6000),
        GLOWSTONE = new ChickenType("glowstone", "minecraft:glowstone_dust", 1, 3, 6000),
        COAL = new ChickenType("coal", "minecraft:coal", 1, 0, 6000),
        GUNPOWDER = new ChickenType("gunpowder", "minecraft:gunpowder", 1, 0, 4800),
        SNOWBALL = new ChickenType("snowball", "minecraft:snowball", 1, 2, 4800, "minecraft:sweet_berries", 1),


        GLASS = new ChickenType("glass", "minecraft:glass", 1, 0, 4800),
        GOLD = new ChickenType("gold", "minecraft:gold_ingot", 1, 0, 12000),
        LAVA = new ChickenType("lava", "chickens:ball_lava", 1, 0, 3000),
        WATER = new ChickenType("water", "chickens:ball_water", 1, 0, 4800),


        DIAMOND = new ChickenType("diamond", "minecraft:diamond", 1, 0, 24000),
        WART = new ChickenType("wart", "minecraft:nether_wart", 1, 0, 6000),
        BLAZE = new ChickenType("blaze", "minecraft:blaze_rod", 1, 0, 9000),
        OBSIDIAN = new ChickenType("obsidian", "minecraft:obsidian", 1, 0, 12000),
        PRISM_SHARD = new ChickenType("prism_shard", "minecraft:prismarine_shard", 1, 3, 20000, "minecraft:prismarine_crystals", 2),
        PRISM_CRYSTALS = new ChickenType("prism_crystals", "minecraft:prismarine_crystals", 1, 3, 20000),


        EMERALD = new ChickenType("emerald", "minecraft:emerald", 1, 0, 24000, "minecraft:saddle", 1),
        PEARL = new ChickenType("ender_pearl", "minecraft:ender_pearl", 1, 0, 24000),
        GHAST_TEAR = new ChickenType("ghast_tear", "minecraft:ghast_tear", 1, 0, 16000),
        NETHERITE_SCRAP = new ChickenType("netherite_scrap", "minecraft:netherite_scrap", 1, 0, 20000,"minecraft:ancient_debris",1)







    ;



    static {
//white
        preRegisterPair(WHITE_DYE, GREEN_DYE, LIME_DYE, (0));
        preRegisterPair(WHITE_DYE, BLACK_DYE, GRAY_DYE, (0));
        preRegisterPair(WHITE_DYE, BLUE_DYE, LIGHT_BLUE_DYE, (0));
        preRegisterPair(WHITE_DYE, BLAZE, GHAST_TEAR , (5));
        preRegisterPair(WHITE_DYE, RED_DYE, PINK_DYE, (0));
        preRegisterPair(WHITE_DYE, GRAY_DYE, LIGHT_GRAY_DYE, (0));
        preRegisterPair(WHITE_DYE, FLINT, IRON, (1));
//blue
        preRegisterPair(BLUE_DYE, WATER, PRISM_SHARD, (4));
        preRegisterPair(BLUE_DYE, OAK, SNOWBALL, (1));
        preRegisterPair(BLUE_DYE, RED_DYE, PURPLE_DYE, (0));
        preRegisterPair(BLUE_DYE, GREEN_DYE, CYAN_DYE, (0));
//black
        preRegisterPair(BLACK_DYE, OAK, STRING, (1));
//yellow
        preRegisterPair(YELLOW_DYE, QUARTZ, GLOWSTONE, (3));
        preRegisterPair(YELLOW_DYE, RED_DYE, ORANGE_DYE, (0));
        preRegisterPair(YELLOW_DYE, IRON, GOLD, (3));
//purple
        preRegisterPair(PURPLE_DYE, PINK_DYE, MAGENTA_DYE, (0));
//green
        preRegisterPair(GREEN_DYE, DIAMOND, EMERALD, (4));
        preRegisterPair(GREEN_DYE, CLAY, SLIME, (2));
        preRegisterPair(GREEN_DYE, RED_DYE, BROWN_DYE, (0));
//red
        preRegisterPair(RED_DYE, SAND, REDSTONE, (2));
//brown
        preRegisterPair(BROWN_DYE, STRING, LEATHER, (2));
        preRegisterPair(BROWN_DYE, GLOWSTONE, WART, (4));
//sand
        preRegisterPair(SAND, FLINT, GUNPOWDER, (2));
        preRegisterPair(SAND, SNOWBALL, CLAY, (2));
//oak
        preRegisterPair(OAK, FLINT, COAL, (2));
//quartz
        preRegisterPair(QUARTZ, REDSTONE, GLASS, (3));
        preRegisterPair(QUARTZ, COAL, LAVA, (3));
//blaze
        preRegisterPair(BLAZE, SLIME, MAGMA_CREAM, (4));
//water
        preRegisterPair(WATER, EMERALD, PRISM_CRYSTALS, (5));
        preRegisterPair(WATER, LAVA, OBSIDIAN, (4));
//gold
        preRegisterPair(GOLD, GLASS, DIAMOND, (5));
//diamond
        preRegisterPair(DIAMOND, WART, PEARL, (5));
//pearl
        preRegisterPair(PEARL,GHAST_TEAR,NETHERITE_SCRAP,(6));






    }

}

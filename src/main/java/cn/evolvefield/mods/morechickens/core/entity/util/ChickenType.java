package cn.evolvefield.mods.morechickens.core.entity.util;

import cn.evolvefield.mods.morechickens.init.ModConfig;
import cn.evolvefield.mods.morechickens.core.entity.BaseChickenEntity;
import com.electronwill.nightconfig.core.Config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;

import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
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
    public String parent1 = "", parent2 = "";
    public int tier = 0;

    public ChickenType(String name, String itemID, int amt, int rAmt, int time, String die, int dieAmt){
        this.name = name;
        layItem = itemID;
        layAmount = amt;
        layRandomAmount = rAmt;
        layTime = time;
        deathItem = die;
        deathAmount = dieAmt;
        enabled = true;
        Types.put(this.name, this);
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

    @Nullable
    public static ChickenType getByChickenTypeName(String name){
        return Types.get(name);
    }

//    public String getTypeName(){
//        return name;
//    }
//
//    public int getTier() {
//        return tier;
//    }

//    public static Collection<ChickenType> getItems() {
//        List<ChickenType> result = new ArrayList<ChickenType>();
//        for (ChickenType chicken : Types.values()) {
//                result.add(chicken);
//        }
//        return result;
//    }
//    @Nullable
//    public ChickenType getParent1() {
//        return getByChickenTypeName(parent1);
//    }
//
//    @Nullable
//    public ChickenType getParent2() {
//        return getByChickenTypeName(parent2);
//    }


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

    public ItemStack getLoot(Random rand, BaseChickenEntity.Gene gene){
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
        for(Config config : ModConfig.COMMON.childChicken.get()){
            int layAmount = config.getIntOrElse("Amount", 1);
            int layRandomAmount = config.getIntOrElse("RandomAmount", 0);
            int layTime = config.getIntOrElse("LayTime", 6000);
            int deathAmount = config.getIntOrElse("DeathAmount", 0);
            int tier = config.getIntOrElse("Tier", 1);
            String name = config.get("Name");
            String dropItem = config.getOrElse("DropItem", "breesources:quail_egg");
            String deathItem = config.getOrElse("DeathItem", "");
            String parent1 = config.getOrElse("Parent1", "");
            String parent2 = config.getOrElse("Parent2", "");
            ChickenType extraType = new ChickenType(name, dropItem, layAmount, layRandomAmount, layTime, deathItem, deathAmount);
            if(!config.getOrElse("Enabled", true))
                extraType.disable();
            extraType.tier = tier;
            extraType.parent1 = parent1;
            extraType.parent2 = parent2;
            if(!parent1.equals("") && !parent2.equals("") && extraType.enabled){
                UnorderedPair<String> pair = new UnorderedPair<>(parent1, parent2);
                RandomPool<String> pool = Pairings.computeIfAbsent(pair, keyPair -> new RandomPool<>((String)null));
                pool.add(name, tiers.get(tier));
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
        GHAST_TEAR = new ChickenType("ghast_tear", "minecraft:ghast_tear", 1, 0, 16000)







    ;
    // Modded
//            COPPER = new ChickenType("copper", "#forge:ingots/copper", 1, 0, 4800).disable(),
//            TIN = new ChickenType("tin", "#forge:ingots/tin", 1, 0, 4800).disable(),
//            ALUMINUM = new ChickenType("aluminum", "#forge:ingots/aluminum", 1, 0, 4800).disable(),
//            LEAD = new ChickenType("lead", "#forge:ingots/lead", 1, 0, 4800).disable(),
//            RUBBER = new ChickenType("rubber", "#forge:rubber", 1, 0, 4800).disable(),
//            SILICON = new ChickenType("silicon", "#forge:silicon", 1, 0, 4800).disable(),



//            // Tier 0 / wild-type
//            PAINTED = new ChickenType("painted", "breesources:quail_egg", 1, 0, 6000),
//            BOBWHITE = new ChickenType("bobwhite", "breesources:quail_egg", 1, 0, 6000),
//            BROWN = new ChickenType("brown", "breesources:quail_egg", 1, 0, 6000),
//            ELEGANT = new ChickenType("elegant", "breesources:quail_egg", 1, 0, 6000),
//
//            // Tier 1
//            GRAVEL = new ChickenType("gravel", "minecraft:gravel", 1, 0, 1200, "minecraft:flint", 2),
//            DIRT = new ChickenType("dirt", "minecraft:dirt", 1, 0, 1200),
//                      NETHERRACK = new ChickenType("netherrack", "minecraft:netherrack", 1, 0, 1200),
//
//            COBBLE = new ChickenType("cobble", "minecraft:cobblestone", 1, 0, 1200, "minecraft:smooth_stone", 2),
//
//            // Tier 2
//                   SPRUCE = new ChickenType("spruce", "minecraft:spruce_log", 1, 0, 2400, "minecraft:spruce_sapling", 1),
//            BIRCH = new ChickenType("birch", "minecraft:birch_log", 1, 0, 2400, "minecraft:birch_sapling", 1),
//            JUNGLE = new ChickenType("jungle", "minecraft:jungle_log", 1, 0, 2400, "minecraft:jungle_sapling", 1),
//            ACACIA = new ChickenType("acacia", "minecraft:acacia_log", 1, 0, 2400, "minecraft:acacia_sapling", 1),
//            DARK_OAK = new ChickenType("dark_oak", "minecraft:dark_oak_log", 1, 0, 2400, "minecraft:dark_oak_sapling", 1),
//
//            QUARTZ = new ChickenType("quartz", "minecraft:quartz", 1, 0, 2400),
//            APPLE = new ChickenType("apple", "minecraft:apple", 1, 0, 2400),
//            REEDS = new ChickenType("reeds", "minecraft:sugar_cane", 1, 2, 2400, "minecraft:bamboo", 4),
//            FEATHER = new ChickenType("feather", "minecraft:feather", 2, 3, 2400, "minecraft:chicken", 1),
//
//
//            // Tier 3
//            BONE = new ChickenType("bone", "minecraft:bone", 1, 0, 4800),
//            COCOA = new ChickenType("cocoa", "minecraft:cocoa_beans", 1, 2, 4800),
//            INK = new ChickenType("ink", "minecraft:ink_sack", 1, 0, 4800),
//            BEET = new ChickenType("beet", "minecraft:beetroot", 1, 0, 4800, "minecraft:beetroot_seeds", 1),
//
//            LAPIS = new ChickenType("lapis", "minecraft:lapis_lazuli", 1, 0, 4800),
//            FLOWER = new ChickenType("flower", "@breesources:normal_flowers", 1, 0, 4800, "minecraft:rabbit", 1),
//          //
//
//            WHEAT = new ChickenType("wheat", "minecraft:wheat", 1, 0, 4800, "minecraft:wheat_seeds", 1),
//            MELON = new ChickenType("melon", "minecraft:melon_slice", 1, 2, 4800),
//            PUMPKIN = new ChickenType("pumpkin", "minecraft:pumpkin", 1, 0, 3600),
//            POTATO = new ChickenType("potato", "minecraft:potato", 1, 0, 4800, "minecraft:porkchop", 1),
//            CARROT = new ChickenType("carrot", "minecraft:carrot", 1, 0, 4800, "minecraft:rabbit", 1),
//
//            //            TERRACOTTA = new ChickenType("terracotta", "minecraft:terracotta", 1, 0, 4800),
//
//
//            // Tier 4
//            GRASS = new ChickenType("grass", "minecraft:grass_block", 1, 0, 6000),
//            REDSHROOM = new ChickenType("redshroom", "minecraft:red_mushroom", 1, 0, 6000),
//            BROWNSHROOM = new ChickenType("brownshroom", "minecraft:brown_mushroom", 1, 0, 6000),
//            ENDSTONE = new ChickenType("endstone", "minecraft:end_stone", 1, 0, 6000, "minecraft:purpur_block", 1),
//
//
//
//            SPIDEREYE = new ChickenType("spidereye", "minecraft:spider_eye", 1, 0, 6000),
//
//
//
//            BASALT = new ChickenType("basalt", "minecraft:basalt", 1, 0, 6000),
//            ICE = new ChickenType("ice", "minecraft:ice", 1, 0, 6000),
//
//            FISH = new ChickenType("fish", "@breesources:raw_fish", 1, 2, 6000, "minecraft:sponge", 1),
//            RABBIT = new ChickenType("rabbit", "minecraft:rabbit_hide", 1, 2, 6000, "minecraft:rabbit_foot", 1),
//            TURTLE = new ChickenType("turtle", "minecraft:scute", 1, 0, 10000, "minecraft:turtle_egg", 1),
//            // Modded
//            SILVER = new ChickenType("silver", "#forge:ingots/silver", 1, 0, 6000).disable(),
//            URANIUM = new ChickenType("uranium", "#forge:ingots/uranium", 1, 0, 6000).disable(),
//
//            // Tier 5
//
//
//
//            WARPED_NYL = new ChickenType("warped_nyl", "minecraft:warped_nylium", 1, 0, 12000),
//            CRIMSON_NYL = new ChickenType("crimson_nyl", "minecraft:crimson_nylium", 1, 0, 12000),
//            MYCELIUM = new ChickenType("mycelium", "minecraft:mycelium", 1, 0, 12000),
//            HONEY = new ChickenType("honey", "minecraft:honey_bottle", 1, 0, 12000, "minecraft:honeycomb", 1),
//
//            BLACKSTONE = new ChickenType("blackstone", "minecraft:blackstone", 1, 0, 12000, "minecraft:gilded_blackstone", 1),
//            CORAL = new ChickenType("coral", "@breesources:coral_items", 1, 0, 12000, "@breesources:coral_blocks", 1),
//            PACKED_ICE = new ChickenType("packed_ice", "minecraft:packed_ice", 1, 0, 12000),
//            // Modded
//            RUBY = new ChickenType("ruby", "#forge:gems/ruby", 1, 0, 12000).disable(),
//            SAPPHIRE = new ChickenType("sapphire", "#forge:gems/sapphire", 1, 0, 12000).disable(),
//
//            // Tier 6
//
//
//            SHULKER = new ChickenType("shulker", "minecraft:shulker_shell", 1, 0, 48000),
//            NAUTILUS = new ChickenType("nautilus", "minecraft:nautilus_shell", 1, 0, 24000, "minecraft:trident", 1),
//
//            MEMBRANE = new ChickenType("membrane", "minecraft:phantom_membrane", 1, 0, 24000, "minecraft:elytra", 1),
//            WITHER_ROSE = new ChickenType("wither_rose", "minecraft:wither_rose", 1, 0, 30000, "minecraft:wither_skeleton_skull", 1),
//            CHORUS = new ChickenType("chorus", "minecraft:chorus_fruit", 1, 3, 24000, "minecraft:chorus_flower", 1),
//            BLUE_ICE = new ChickenType("blue_ice", "minecraft:blue_ice", 1, 0, 24000),
//            WARPED_STEM = new ChickenType("warped_stem", "minecraft:warped_stem", 1, 0, 24000, "minecraft:shroomlight", 1),
//            CRIMSON_STEM = new ChickenType("crimson_stem", "minecraft:crimson_stem", 1, 0, 24000, "minecraft:shroomlight", 1),
//
//            // Tier 7
//            WITHER_STAR = new ChickenType("wither_star", "minecraft:nether_star", 1, 0, 144000),
//            HEART_OF_SEA = new ChickenType("heart_of_sea", "minecraft:heart_of_the_sea", 1, 0, 72000),
//            DEBRIS = new ChickenType("debris", "minecraft:ancient_debris", 1, 0, 72000),
//            DRAGON = new ChickenType("dragon", "minecraft:dragon_breath", 1, 0, 48000, "minecraft:dragon_head", 1),
//            BOOK = new ChickenType("book", "minecraft:book", 1, 0, 48000, "minecraft:experience_bottle", 5),
//            MUSIC = new ChickenType("music", "@minecraft:music_discs", 1, 0, 40000),
//

//            // Concrete powders
//            WHITE_CONCRETE_POWDER = new ChickenType("white_concrete_powder", "minecraft:white_concrete_powder", 1, 0, 2400),
//            BLACK_CONCRETE_POWDER = new ChickenType("black_concrete_powder", "minecraft:black_concrete_powder", 1, 0, 2400),
//            GRAY_CONCRETE_POWDER = new ChickenType("gray_concrete_powder", "minecraft:gray_concrete_powder", 1, 0, 2400),
//            LIGHT_GRAY_CONCRETE_POWDER = new ChickenType("light_gray_concrete_powder", "minecraft:light_gray_concrete_powder", 1, 0, 2400),
//            RED_CONCRETE_POWDER = new ChickenType("red_concrete_powder", "minecraft:red_concrete_powder", 1, 0, 2400),
//            GREEN_CONCRETE_POWDER = new ChickenType("green_concrete_powder", "minecraft:green_concrete_powder", 1, 0, 2400),
//            BLUE_CONCRETE_POWDER = new ChickenType("blue_concrete_powder", "minecraft:blue_concrete_powder", 1, 0, 2400),
//            YELLOW_CONCRETE_POWDER = new ChickenType("yellow_concrete_powder", "minecraft:yellow_concrete_powder", 1, 0, 2400),
//            BROWN_CONCRETE_POWDER = new ChickenType("brown_concrete_powder", "minecraft:brown_concrete_powder", 1, 0, 2400),
//            PINK_CONCRETE_POWDER = new ChickenType("pink_concrete_powder", "minecraft:pink_concrete_powder", 1, 0, 2400),
//            ORANGE_CONCRETE_POWDER = new ChickenType("orange_concrete_powder", "minecraft:orange_concrete_powder", 1, 0, 2400),
//            PURPLE_CONCRETE_POWDER = new ChickenType("purple_concrete_powder", "minecraft:purple_concrete_powder", 1, 0, 2400),
//            MAGENTA_CONCRETE_POWDER = new ChickenType("magenta_concrete_powder", "minecraft:magenta_concrete_powder", 1, 0, 2400),
//            LIME_CONCRETE_POWDER = new ChickenType("lime_concrete_powder", "minecraft:lime_concrete_powder", 1, 0, 2400),
//            CYAN_CONCRETE_POWDER = new ChickenType("cyan_concrete_powder", "minecraft:cyan_concrete_powder", 1, 0, 2400),
//            LIGHT_BLUE_CONCRETE_POWDER = new ChickenType("light_blue_concrete_powder", "minecraft:light_blue_concrete_powder", 1, 0, 2400),
//
//            //Concrete blocks
//            WHITE_CONCRETE = new ChickenType("white_concrete", "minecraft:white_concrete", 1, 0, 4800),
//            BLACK_CONCRETE = new ChickenType("black_concrete", "minecraft:black_concrete", 1, 0, 4800),
//            GRAY_CONCRETE = new ChickenType("gray_concrete", "minecraft:gray_concrete", 1, 0, 4800),
//            LIGHT_GRAY_CONCRETE = new ChickenType("light_gray_concrete", "minecraft:light_gray_concrete", 1, 0, 4800),
//            RED_CONCRETE = new ChickenType("red_concrete", "minecraft:red_concrete", 1, 0, 4800),
//            GREEN_CONCRETE = new ChickenType("green_concrete", "minecraft:green_concrete", 1, 0, 4800),
//            BLUE_CONCRETE = new ChickenType("blue_concrete", "minecraft:blue_concrete", 1, 0, 4800),
//            YELLOW_CONCRETE = new ChickenType("yellow_concrete", "minecraft:yellow_concrete", 1, 0, 4800),
//            BROWN_CONCRETE = new ChickenType("brown_concrete", "minecraft:brown_concrete", 1, 0, 4800),
//            PINK_CONCRETE = new ChickenType("pink_concrete", "minecraft:pink_concrete", 1, 0, 4800),
//            ORANGE_CONCRETE = new ChickenType("orange_concrete", "minecraft:orange_concrete", 1, 0, 4800),
//            PURPLE_CONCRETE = new ChickenType("purple_concrete", "minecraft:purple_concrete", 1, 0, 4800),
//            MAGENTA_CONCRETE = new ChickenType("magenta_concrete", "minecraft:magenta_concrete", 1, 0, 4800),
//            LIME_CONCRETE = new ChickenType("lime_concrete", "minecraft:lime_concrete", 1, 0, 4800),
//            CYAN_CONCRETE = new ChickenType("cyan_concrete", "minecraft:cyan_concrete", 1, 0, 4800),
//            LIGHT_BLUE_CONCRETE = new ChickenType("light_blue_concrete", "minecraft:light_blue_concrete", 1, 0, 4800),
//
//            //Wool blocks
//            WHITE_WOOL = new ChickenType("white_wool", "minecraft:white_wool", 1, 0, 4800),
//            BLACK_WOOL = new ChickenType("black_wool", "minecraft:black_wool", 1, 0, 4800),
//            GRAY_WOOL = new ChickenType("gray_wool", "minecraft:gray_wool", 1, 0, 4800),
//            LIGHT_GRAY_WOOL = new ChickenType("light_gray_wool", "minecraft:light_gray_wool", 1, 0, 4800),
//            RED_WOOL = new ChickenType("red_wool", "minecraft:red_wool", 1, 0, 4800),
//            GREEN_WOOL = new ChickenType("green_wool", "minecraft:green_wool", 1, 0, 4800),
//            BLUE_WOOL = new ChickenType("blue_wool", "minecraft:blue_wool", 1, 0, 4800),
//            YELLOW_WOOL = new ChickenType("yellow_wool", "minecraft:yellow_wool", 1, 0, 4800),
//            BROWN_WOOL = new ChickenType("brown_wool", "minecraft:brown_wool", 1, 0, 4800),
//            PINK_WOOL = new ChickenType("pink_wool", "minecraft:pink_wool", 1, 0, 4800),
//            ORANGE_WOOL = new ChickenType("orange_wool", "minecraft:orange_wool", 1, 0, 4800),
//            PURPLE_WOOL = new ChickenType("purple_wool", "minecraft:purple_wool", 1, 0, 4800),
//            MAGENTA_WOOL = new ChickenType("magenta_wool", "minecraft:magenta_wool", 1, 0, 4800),
//            LIME_WOOL = new ChickenType("lime_wool", "minecraft:lime_wool", 1, 0, 4800),
//            CYAN_WOOL = new ChickenType("cyan_wool", "minecraft:cyan_wool", 1, 0, 4800),
//            LIGHT_BLUE_WOOL = new ChickenType("light_blue_wool", "minecraft:light_blue_wool", 1, 0, 4800),
//


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








//        preRegisterPair(BROWN, ELEGANT, SAND, (0));
//        preRegisterPair(PAINTED, BOBWHITE, NETHERRACK, (0));
//        preRegisterPair(PAINTED, ELEGANT, CLAY, (0));
//        preRegisterPair(BOBWHITE, ELEGANT, COBBLE, (0));
//
//        preRegisterPair(ELEGANT, COBBLE, OAK, (1));
//        preRegisterPair(ELEGANT, SAND, SPRUCE, (1));
//        preRegisterPair(ELEGANT, DIRT, BIRCH, (1));
//        preRegisterPair(ELEGANT, CLAY, JUNGLE, (1));
//        preRegisterPair(ELEGANT, GRAVEL, ACACIA, (1));
//        preRegisterPair(ELEGANT, NETHERRACK, DARK_OAK, (1));
//        preRegisterPair(PAINTED, COBBLE, COAL, (1));
//        preRegisterPair(BOBWHITE, NETHERRACK, QUARTZ, (1));
//        preRegisterPair(BROWN, CLAY, APPLE, (1));
//        preRegisterPair(PAINTED, SAND, REEDS, (1));
//        preRegisterPair(BOBWHITE, DIRT, FEATHER, (1));
//        preRegisterPair(BROWN, GRAVEL, STRING, (1));
//
//        preRegisterPair(DIRT, COAL, BONE, (2));
//        preRegisterPair(APPLE, JUNGLE, COCOA, (2));
//        preRegisterPair(STRING, QUARTZ, LAPIS, (2));
//        preRegisterPair(APPLE, SPRUCE, BEET, (2));
//        preRegisterPair(SAND, STRING, CACTUS, (2));
//        preRegisterPair(DIRT, FEATHER, FLOWER, (2));
//        preRegisterPair(COAL, DARK_OAK, INK, (2));
//        preRegisterPair(COAL, QUARTZ, IRON, (2));
//        preRegisterPair(STRING, NETHERRACK, REDSTONE, (2));
//        preRegisterPair(NETHERRACK, FEATHER, SOULSAND, (2));
//        preRegisterPair(REEDS, OAK, WHEAT, (2));
//        preRegisterPair(REEDS, JUNGLE, MELON, (2));
//        preRegisterPair(REEDS, SPRUCE, PUMPKIN, (2));
//        preRegisterPair(REEDS, BIRCH, POTATO, (2));
//        preRegisterPair(REEDS, ACACIA, CARROT, (2));
//        preRegisterPair(REEDS, SAND, WATER, (2));
//        preRegisterPair(FEATHER, STRING, LEATHER, (2));
//        preRegisterPair(CLAY, COAL, TERRACOTTA, (2));
//        preRegisterPair(FEATHER, CLAY, SNOWBALL, (2));
//
//        preRegisterPair(BONE, IRON, TIN, (2));
//        preRegisterPair(APPLE, IRON, COPPER, (2));
//        preRegisterPair(WATER, IRON, ALUMINUM, (2));
//        preRegisterPair(COAL, IRON, LEAD, (2));
//        preRegisterPair(REEDS, CLAY, RUBBER, (2));
//        preRegisterPair(CLAY, SAND, SILICON, (2));
//
//        preRegisterPair(DIRT, FLOWER, GRASS, (3));
//        preRegisterPair(CARROT, BEET, REDSHROOM, (3));
//        preRegisterPair(POTATO, COCOA, BROWNSHROOM, (3));
//        preRegisterPair(SOULSAND, IRON, ENDSTONE, (3));
//        preRegisterPair(IRON, FLOWER, GOLD, (3));
//        preRegisterPair(WATER, SOULSAND, LAVA, (3));
//        preRegisterPair(COAL, MELON, GUNPOWDER, (3));
//        preRegisterPair(PUMPKIN, LEATHER, SPIDEREYE, (3));
//        preRegisterPair(MELON, CACTUS, SLIME, (3));
//        preRegisterPair(WHEAT, SOULSAND, WART, (3));
//        preRegisterPair(SAND, IRON, GLASS, (3));
//        preRegisterPair(COBBLE, SOULSAND, BASALT, (3));
//        preRegisterPair(WATER, SNOWBALL, ICE, (3));
//        preRegisterPair(SOULSAND, REDSTONE, GLOWSTONE, (3));
//        preRegisterPair(WATER, LEATHER, FISH, (3));
//        preRegisterPair(SAND, GRASS, RABBIT, (3));
//        preRegisterPair(SAND, FISH, TURTLE, (3));
//
//        preRegisterPair(GOLD, BONE, SILVER, (3));
//        preRegisterPair(GOLD, WART, URANIUM, (3));
//
//        preRegisterPair(GOLD, SLIME, EMERALD, (4));
//        preRegisterPair(WATER, LAVA, OBSIDIAN, (4));
//        preRegisterPair(WATER, SPIDEREYE, BLAZE, (4));
//        preRegisterPair(BROWNSHROOM, GRASS, WARPED_NYL, (4));
//        preRegisterPair(REDSHROOM, GRASS, CRIMSON_NYL, (4));
//        preRegisterPair(REDSHROOM, BROWNSHROOM, MYCELIUM, (4));
//        preRegisterPair(SLIME, REEDS, HONEY, (4));
//        preRegisterPair(WART, SPIDEREYE, GHAST, (4));
//        preRegisterPair(BASALT, COAL, BLACKSTONE, (4));
//        preRegisterPair(FISH, GLASS, CORAL, (4));
//        preRegisterPair(ICE, GLASS, PACKED_ICE, (4));
//
//        preRegisterPair(EMERALD, REDSTONE, RUBY, (4));
//        preRegisterPair(EMERALD, LAPIS, SAPPHIRE, (4));
//
//        preRegisterPair(EMERALD, OBSIDIAN, DIAMOND, (5));
//        preRegisterPair(BLAZE, OBSIDIAN, PEARL, (5));
//        preRegisterPair(GHAST, BLAZE, SHULKER, (5));
//        preRegisterPair(WATER, EMERALD, NAUTILUS, (5));
//        preRegisterPair(WATER, OBSIDIAN, PRISM, (5));
//        preRegisterPair(LEATHER, HONEY, MEMBRANE, (5));
//        preRegisterPair(FLOWER, GHAST, WITHER_ROSE, (5));
//        preRegisterPair(ENDSTONE, MYCELIUM, CHORUS, (5));
//        preRegisterPair(PACKED_ICE, EMERALD, BLUE_ICE, (5));
//        preRegisterPair(WARPED_NYL, BONE, WARPED_STEM, (5));
//        preRegisterPair(CRIMSON_NYL, BONE, CRIMSON_STEM, (5));
//
//        preRegisterPair(DIAMOND, WITHER_ROSE, WITHER_STAR, (6));
//        preRegisterPair(NAUTILUS, PEARL, HEART_OF_SEA, (6));
//        preRegisterPair(DIAMOND, BLACKSTONE, DEBRIS, (6));
//        preRegisterPair(MEMBRANE, SHULKER, DRAGON, (6));
//        preRegisterPair(DIAMOND, OBSIDIAN, BOOK, (6));
//        preRegisterPair(DIAMOND, REDSTONE, MUSIC, 6);
//
//        preRegisterPair(WATER, BONE, WHITE_DYE, (1));
//        preRegisterPair(WATER, INK, BLACK_DYE, (1));
//        preRegisterPair(WATER, COCOA, BROWN_DYE, (1));
//        preRegisterPair(WATER, CACTUS, GREEN_DYE, (1));
//        preRegisterPair(WATER, LAPIS, BLUE_DYE, (1));
//        preRegisterPair(WATER, BEET, RED_DYE, (1));
//        preRegisterPair(WATER, FLOWER, YELLOW_DYE, (1));
//        preRegisterPair(WHITE_DYE, BLACK_DYE, GRAY_DYE, (1));
//        preRegisterPair(WHITE_DYE, RED_DYE, PINK_DYE, (1));
//        preRegisterPair(WHITE_DYE, GREEN_DYE, LIME_DYE, (1));
//        preRegisterPair(WHITE_DYE, BLUE_DYE, LIGHT_BLUE_DYE, (1));
//        preRegisterPair(RED_DYE, YELLOW_DYE, ORANGE_DYE, (1));
//        preRegisterPair(RED_DYE, BLUE_DYE, PURPLE_DYE, (1));
//        preRegisterPair(BLUE_DYE, GREEN_DYE, CYAN_DYE, (1));
//        preRegisterPair(PINK_DYE, PURPLE_DYE, MAGENTA_DYE, (1));
//        preRegisterPair(WHITE_DYE, GRAY_DYE, LIGHT_GRAY_DYE, (1));
//
//        preRegisterPair(WHITE_DYE, SAND, WHITE_CONCRETE_POWDER, (1));
//        preRegisterPair(BLACK_DYE, SAND, BLACK_CONCRETE_POWDER, (1));
//        preRegisterPair(RED_DYE, SAND, RED_CONCRETE_POWDER, (1));
//        preRegisterPair(GREEN_DYE, SAND, GREEN_CONCRETE_POWDER, (1));
//        preRegisterPair(BLUE_DYE, SAND, BLUE_CONCRETE_POWDER, (1));
//        preRegisterPair(YELLOW_DYE, SAND, YELLOW_CONCRETE_POWDER, (1));
//        preRegisterPair(BROWN_DYE, SAND, BROWN_CONCRETE_POWDER, (1));
//        preRegisterPair(GRAY_DYE, SAND, GRAY_CONCRETE_POWDER, (1));
//        preRegisterPair(PINK_DYE, SAND, PINK_CONCRETE_POWDER, (1));
//        preRegisterPair(LIME_DYE, SAND, LIME_CONCRETE_POWDER, (1));
//        preRegisterPair(LIGHT_BLUE_DYE, SAND, LIGHT_BLUE_CONCRETE_POWDER, (1));
//        preRegisterPair(ORANGE_DYE, SAND, ORANGE_CONCRETE_POWDER, (1));
//        preRegisterPair(PURPLE_DYE, SAND, PURPLE_CONCRETE_POWDER, (1));
//        preRegisterPair(CYAN_DYE, SAND, CYAN_CONCRETE_POWDER, (1));
//        preRegisterPair(MAGENTA_DYE, SAND, MAGENTA_CONCRETE_POWDER, (1));
//        preRegisterPair(LIGHT_GRAY_DYE, SAND, LIGHT_GRAY_CONCRETE_POWDER, (1));
//
//        preRegisterPair(WHITE_DYE, STRING, WHITE_WOOL, (1));
//        preRegisterPair(BLACK_DYE, STRING, BLACK_WOOL, (1));
//        preRegisterPair(RED_DYE, STRING, RED_WOOL, (1));
//        preRegisterPair(GREEN_DYE, STRING, GREEN_WOOL, (1));
//        preRegisterPair(BLUE_DYE, STRING, BLUE_WOOL, (1));
//        preRegisterPair(YELLOW_DYE, STRING, YELLOW_WOOL, (1));
//        preRegisterPair(BROWN_DYE, STRING, BROWN_WOOL, (1));
//        preRegisterPair(GRAY_DYE, STRING, GRAY_WOOL, (1));
//        preRegisterPair(PINK_DYE, STRING, PINK_WOOL, (1));
//        preRegisterPair(LIME_DYE, STRING, LIME_WOOL, (1));
//        preRegisterPair(LIGHT_BLUE_DYE, STRING, LIGHT_BLUE_WOOL, (1));
//        preRegisterPair(ORANGE_DYE, STRING, ORANGE_WOOL, (1));
//        preRegisterPair(PURPLE_DYE, STRING, PURPLE_WOOL, (1));
//        preRegisterPair(CYAN_DYE, STRING, CYAN_WOOL, (1));
//        preRegisterPair(MAGENTA_DYE, STRING, MAGENTA_WOOL, (1));
//        preRegisterPair(LIGHT_GRAY_DYE, STRING, LIGHT_GRAY_WOOL, (1));
//
//        preRegisterPair(WHITE_DYE, TERRACOTTA, WHITE_TERRACOTTA, (1));
//        preRegisterPair(BLACK_DYE, TERRACOTTA, BLACK_TERRACOTTA, (1));
//        preRegisterPair(RED_DYE, TERRACOTTA, RED_TERRACOTTA, (1));
//        preRegisterPair(GREEN_DYE, TERRACOTTA, GREEN_TERRACOTTA, (1));
//        preRegisterPair(BLUE_DYE, TERRACOTTA, BLUE_TERRACOTTA, (1));
//        preRegisterPair(YELLOW_DYE, TERRACOTTA, YELLOW_TERRACOTTA, (1));
//        preRegisterPair(BROWN_DYE, TERRACOTTA, BROWN_TERRACOTTA, (1));
//        preRegisterPair(GRAY_DYE, TERRACOTTA, GRAY_TERRACOTTA, (1));
//        preRegisterPair(PINK_DYE, TERRACOTTA, PINK_TERRACOTTA, (1));
//        preRegisterPair(LIME_DYE, TERRACOTTA, LIME_TERRACOTTA, (1));
//        preRegisterPair(LIGHT_BLUE_DYE, TERRACOTTA, LIGHT_BLUE_TERRACOTTA, (1));
//        preRegisterPair(ORANGE_DYE, TERRACOTTA, ORANGE_TERRACOTTA, (1));
//        preRegisterPair(PURPLE_DYE, TERRACOTTA, PURPLE_TERRACOTTA, (1));
//        preRegisterPair(CYAN_DYE, TERRACOTTA, CYAN_TERRACOTTA, (1));
//        preRegisterPair(MAGENTA_DYE, TERRACOTTA, MAGENTA_TERRACOTTA, (1));
//        preRegisterPair(LIGHT_GRAY_DYE, TERRACOTTA, LIGHT_GRAY_TERRACOTTA, (1));
//
//        preRegisterPair(WHITE_DYE, GLASS, WHITE_GLASS, (1));
//        preRegisterPair(BLACK_DYE, GLASS, BLACK_GLASS, (1));
//        preRegisterPair(RED_DYE, GLASS, RED_GLASS, (1));
//        preRegisterPair(GREEN_DYE, GLASS, GREEN_GLASS, (1));
//        preRegisterPair(BLUE_DYE, GLASS, BLUE_GLASS, (1));
//        preRegisterPair(YELLOW_DYE, GLASS, YELLOW_GLASS, (1));
//        preRegisterPair(BROWN_DYE, GLASS, BROWN_GLASS, (1));
//        preRegisterPair(GRAY_DYE, GLASS, GRAY_GLASS, (1));
//        preRegisterPair(PINK_DYE, GLASS, PINK_GLASS, (1));
//        preRegisterPair(LIME_DYE, GLASS, LIME_GLASS, (1));
//        preRegisterPair(LIGHT_BLUE_DYE, GLASS, LIGHT_BLUE_GLASS, (1));
//        preRegisterPair(ORANGE_DYE, GLASS, ORANGE_GLASS, (1));
//        preRegisterPair(PURPLE_DYE, GLASS, PURPLE_GLASS, (1));
//        preRegisterPair(CYAN_DYE, GLASS, CYAN_GLASS, (1));
//        preRegisterPair(MAGENTA_DYE, GLASS, MAGENTA_GLASS, (1));
//        preRegisterPair(LIGHT_GRAY_DYE, GLASS, LIGHT_GRAY_GLASS, (1));
//
//        preRegisterPair(WHITE_CONCRETE_POWDER, WATER, WHITE_CONCRETE, (1));
//        preRegisterPair(BLACK_CONCRETE_POWDER, WATER, BLACK_CONCRETE, (1));
//        preRegisterPair(RED_CONCRETE_POWDER, WATER, RED_CONCRETE, (1));
//        preRegisterPair(GREEN_CONCRETE_POWDER, WATER, GREEN_CONCRETE, (1));
//        preRegisterPair(BLUE_CONCRETE_POWDER, WATER, BLUE_CONCRETE, (1));
//        preRegisterPair(YELLOW_CONCRETE_POWDER, WATER, YELLOW_CONCRETE, (1));
//        preRegisterPair(BROWN_CONCRETE_POWDER, WATER, BROWN_CONCRETE, (1));
//        preRegisterPair(GRAY_CONCRETE_POWDER, WATER, GRAY_CONCRETE, (1));
//        preRegisterPair(PINK_CONCRETE_POWDER, WATER, PINK_CONCRETE, (1));
//        preRegisterPair(LIME_CONCRETE_POWDER, WATER, LIME_CONCRETE, (1));
//        preRegisterPair(LIGHT_BLUE_CONCRETE_POWDER, WATER, LIGHT_BLUE_CONCRETE, (1));
//        preRegisterPair(ORANGE_CONCRETE_POWDER, WATER, ORANGE_CONCRETE, (1));
//        preRegisterPair(PURPLE_CONCRETE_POWDER, WATER, PURPLE_CONCRETE, (1));
//        preRegisterPair(CYAN_CONCRETE_POWDER, WATER, CYAN_CONCRETE, (1));
//        preRegisterPair(MAGENTA_CONCRETE_POWDER, WATER, MAGENTA_CONCRETE, (1));
//        preRegisterPair(LIGHT_GRAY_CONCRETE_POWDER, WATER, LIGHT_GRAY_CONCRETE, (1));
    }

}

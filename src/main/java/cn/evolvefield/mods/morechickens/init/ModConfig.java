package cn.evolvefield.mods.morechickens.init;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.common.data.ChickenRegistry;
import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class ModConfig {
    public static class Common {

        private static final double[] TIER_DEFAULTS = {0.5, 1, 0.75, 0.5, 0.25, 0.125, 0.0625};

        public static class ChickenTypeConfig {
            public ForgeConfigSpec.IntValue amount, time, onDieAmount, tier;
            public ForgeConfigSpec.ConfigValue<String> dropItem, deathItem, parent1, parent2;
            public ForgeConfigSpec.BooleanValue enabled;
        }

        public Map<String, ChickenTypeConfig> chickenType;
        public ForgeConfigSpec.DoubleValue[] tierOdds;

        public ForgeConfigSpec.IntValue chickenEggChance, chickenEggMultiChance,
                chickenWeight, chickenMin, chickenMax, chickenBreedingTime;



        //wild spawn chance
        public final ForgeConfigSpec.DoubleValue oakChance;
        public final ForgeConfigSpec.DoubleValue sandChance;
        public final ForgeConfigSpec.DoubleValue flintChance;
        public final ForgeConfigSpec.DoubleValue quartzChance;
        public final ForgeConfigSpec.DoubleValue soulSandChance;

        public final ForgeConfigSpec.BooleanValue disableEggLaying;
        public final ForgeConfigSpec.DoubleValue roostSpeed;
        public final ForgeConfigSpec.DoubleValue breederSpeed;

        public final ForgeConfigSpec.IntValue breedingTime;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> preferredTagSource;



        public Common(ForgeConfigSpec.Builder builder){
            chickenType = new HashMap<>();
            tierOdds = new ForgeConfigSpec.DoubleValue[7];


            preferredTagSource = builder
                    .comment("A priority list of Mod IDs that results of comb output should stem from, aka which mod you want the copper to come from.")
                    .defineList("preferredTagSource", ImmutableList.of("minecraft", MoreChickens.MODID, "thermal", "tconstruct", "immersiveengineering", "create", "mekanism", "silents_mechanisms"), obj -> true);


            builder.comment("Odds of successful cross-breeding to ascend one tier").push("Tiers");
            for(int i = 0; i < 7; i++){
                tierOdds[i] = builder.worldRestart().defineInRange("tier" + i, TIER_DEFAULTS[i], 0, 1);
            }
            builder.pop();

            builder.comment().push("BlockSpeed");
            disableEggLaying = builder
                    .comment("Prevent vanilla chickens from laying eggs. Of interest to modpack makers only. Default: false.")
                    .define("disableEggLaying", false);

            roostSpeed = builder
                    .comment("The speed multiplier for the roost. Higher is faster. Default: 1.")
                    .defineInRange("roostSpeed",1d,0.00d,100d);

            breederSpeed = builder
                    .comment("The speed multiplier for the breeder. Higher is faster. Default: 1.")
                    .defineInRange("breederSpeed",1d,0.01d,100d);
            breedingTime = builder
                    .comment("The time in ticks the breeder takes to create a new chicken")
                    .defineInRange("breedingTime", 20 * 60, 20, Integer.MAX_VALUE);
            builder.pop();

            builder.comment("wild chicken spawn chance").push("Baits");
            oakChance = builder
                    .comment("The chance (per second) that a oak chicken bait will result in a spawn.")
                    .translation("chickens.config.oakChance")
                    .defineInRange("oakChance", 0.01f, 0.002f, 1f);
            sandChance = builder
                    .comment("The chance (per second) that a sand chicken bait will result in a spawn.")
                    .translation("chickens.config.sandChance")
                    .defineInRange("sandChance", 0.01f, 0.002f, 1f);
            flintChance = builder
                    .comment("The chance (per second) that a flint chicken bait will result in a spawn.")
                    .translation("chickens.config.flintChance")
                    .defineInRange("flintChance", 0.01f, 0.002f, 1f);
            quartzChance = builder
                    .comment("The chance (per second) that a quartz chicken bait will result in a spawn.")
                    .translation("chickens.config.quartzChance")
                    .defineInRange("quartzChance", 0.01f, 0.002f, 1f);
            soulSandChance = builder
                    .comment("The chance (per second) that a soulSand chicken bait will result in a spawn.")
                    .translation("chickens.config.soulSandChance")
                    .defineInRange("soulSandChance", 0.01f, 0.002f, 1f);
            builder.pop();


            builder.comment("World gen options").push("World");
            chickenWeight = builder
                    .comment("Chicken spawning weight (higher = more common)")
                    .worldRestart()
                    .defineInRange("SpawnWeight", 10, 0, 100);
            chickenMin = builder
                    .comment("Minimum number of Chickens to spawn at once")
                    .worldRestart()
                    .defineInRange("SpawnMin", 2, 0, 20);
            chickenMax = builder
                    .comment("Maximum number of Chickens to spawn at once")
                    .worldRestart()
                    .defineInRange("SpawnMax", 5, 0, 20);
            builder.pop();

            builder.comment("Options for Chicken breeding").push("ChickenBreeding");
            chickenEggChance = builder
                    .comment("Chance for egg to spawn a Chicken (higher = rarer)")
                    .worldRestart()
                    .defineInRange("ChickenEggChance", 4, 1, 1000);
            chickenEggMultiChance = builder
                    .comment("Chance for egg to spawn 4 Chicken instead of 1")
                    .worldRestart()
                    .defineInRange("ChickenEggMultiChance", 32, 1, 1000);
            chickenBreedingTime = builder
                    .comment("Delay in ticks between Chicken breeding")
                    .worldRestart()
                    .defineInRange("ChickenBreedingTime", 6000, 1, 144_000);
            builder.pop();

            builder.comment("Settings for each type of Chicken").push("ChickenTypes");
            for(Map.Entry<String, ChickenData> type : ChickenRegistry.Types.entrySet()){
                builder.comment("Config values for Chicken type " + type.getKey()).push(type.getKey());
                ChickenTypeConfig config = new ChickenTypeConfig();
                config.amount = builder
                        .comment("Base amount of loot laid")
                        .worldRestart()
                        .defineInRange("Amount", type.getValue().layAmount, 0, 64);
                config.time = builder
                        .comment("Minimum ticks between laying")
                        .worldRestart()
                        .defineInRange("LayTime", type.getValue().layTime, 0, 1_000_000);
                config.onDieAmount = builder
                        .comment("Amount of extra death loot dropped, if any")
                        .worldRestart()
                        .defineInRange("DeathAmount", type.getValue().deathAmount, 0, 64);
                config.dropItem = builder
                        .comment("ID of item dropped as egg")
                        .worldRestart()
                        .define("DropItem", type.getValue().layItem);
                config.deathItem = builder
                        .comment("Extra item dropped on death, empty string for nothing")
                        .worldRestart()
                        .define("DeathItem", type.getValue().deathItem);
                config.enabled = builder
                        .comment("Whether the Chicken type is enabled")
                        .worldRestart()
                        .define("Enabled", type.getValue().enabled);
                config.parent1 = builder
                        .comment("One parent of breeding pair, empty for non-breedable")
                        .worldRestart()
                        .define("Parent1", type.getValue().parent1);
                config.parent2 = builder
                        .comment("One parent of breeding pair, empty for non-breedable")
                        .worldRestart()
                        .define("Parent2", type.getValue().parent2);
                config.tier = builder
                        .comment("Tier for odds of successful breed")
                        .worldRestart()
                        .defineInRange("Tier", type.getValue().tier, 0, 6);
                builder.pop();
                chickenType.put(type.getKey(), config);
            }
            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec CONFIG_SPEC;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        CONFIG_SPEC = specPair.getRight();
    }

    @SubscribeEvent
    public static void onLoad(net.minecraftforge.fml.config.ModConfig.Loading event){

    }

    @SubscribeEvent
    public static void onFileChanged(net.minecraftforge.fml.config.ModConfig.Reloading event){

    }


}

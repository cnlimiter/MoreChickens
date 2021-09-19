package cn.evolvefield.mods.morechickens.init;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.item.*;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;


public class ModItems {

    public static Item CHICKEN_SPAWN_EGG;
    public static Item WATER_EGG;
    public static Item LAVA_EGG;
    public static Item WHITE_EGG;
    public static Item BLUE_EGG;
    public static Item BLACK_EGG;
    public static Item MAGENTA_EGG;
    public static Item LIME_EGG;
    public static Item YELLOW_EGG;
    public static Item PURPLE_EGG;
    public static Item GREEN_EGG;
    public static Item LIGHT_GRAY_EGG;
    public static Item LIGHT_BLUE_EGG;
    public static Item CYAN_EGG;
    public static Item  RED_EGG;
    public static Item PINK_EGG;
    public static Item ORANGE_EGG;
    public static Item GRAY_EGG;
    public static Item BROWN_EGG;

    public static Item ITEM_CHICKEN = new CatcherItem();
    public static Item ITEM_CATCHER = new ChickenItem();
    public static Item ANALYZER = new AnalyzerItem();


    public static void registerItems(RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(

                WHITE_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_white",
                        "chickens:egg_white")
                        .setRegistryName("egg_white"),
                BLUE_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_blue",
                        "chickens:egg_blue")
                        .setRegistryName("egg_blue"),
                BLACK_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_black",
                        "chickens:egg_black")
                        .setRegistryName("egg_black"),
                MAGENTA_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_magenta",
                        "chickens:egg_magenta")
                        .setRegistryName("egg_magenta"),
                LIME_EGG =new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_lime",
                        "chickens:egg_lime")
                        .setRegistryName("egg_lime"),
                YELLOW_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_yellow",
                        "chickens:egg_yellow")
                        .setRegistryName("egg_yellow"),
                PURPLE_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_purple",
                        "chickens:egg_purple")
                        .setRegistryName("egg_purple"),
                GREEN_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_green",
                        "chickens:egg_green")
                        .setRegistryName("egg_green"),
                LIGHT_GRAY_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_light_gray",
                        "chickens:egg_light_gray")
                        .setRegistryName("egg_light_gray"),
                LIGHT_BLUE_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_light_blue",
                        "chickens:egg_light_blue")
                        .setRegistryName("egg_light_blue"),
                CYAN_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_cyan",
                        "chickens:egg_cyan")
                        .setRegistryName("egg_cyan"),
                RED_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_red",
                        "chickens:egg_red")
                        .setRegistryName("egg_red"),
                PINK_EGG =new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_pink",
                        "chickens:egg_pink")
                        .setRegistryName("egg_pink"),
                ORANGE_EGG =new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_orange",
                        "chickens:egg_orange")
                        .setRegistryName("egg_orange"),
                GRAY_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_gray",
                        "chickens:egg_gray")
                        .setRegistryName("egg_gray"),
                BROWN_EGG = new ColorEggItem(new Item.Properties().tab(ModItemGroups.INSTANCE),
                        10,
                        1000,
                        "dye_brown",
                        "chickens:egg_brown")
                        .setRegistryName("egg_brown"),
                CHICKEN_SPAWN_EGG = new ModSpawnEgg(ModEntities.BASE_CHICKEN, 0x734011, 0xa4b5bd, new Item.Properties().tab(ModItemGroups.INSTANCE)).setRegistryName("chicken_spawn_egg"),
                WATER_EGG = new BallItem(new Item.Properties().tab(ModItemGroups.INSTANCE), Fluids.WATER).setRegistryName("egg_water"),
                LAVA_EGG = new BallItem(new Item.Properties().tab(ModItemGroups.INSTANCE), Fluids.LAVA).setRegistryName("egg_lava"),
                ITEM_CATCHER,
                ITEM_CHICKEN ,
                ANALYZER



        );
    //manaHammer = BotaniaCompat.createManaHammerItem(optionalItemProperties(BotaniaCompat.MOD_ID)).setRegistryName(new ResourceLocation(ExCompressum.MOD_ID, "hammer_mana"))

    }

    public static void matchConfig(){
        ColorEggItem whiteEgg = (ColorEggItem)WHITE_EGG;
        whiteEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem blueEgg = (ColorEggItem)BLUE_EGG;
        blueEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem blackEgg = (ColorEggItem)BLACK_EGG;
        blackEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem magentaEgg = (ColorEggItem)MAGENTA_EGG;
        magentaEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem limeEgg = (ColorEggItem)LIME_EGG;
        limeEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem yellowEgg = (ColorEggItem)YELLOW_EGG;
        yellowEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem purpleEgg = (ColorEggItem)PURPLE_EGG;
        purpleEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem greenEgg = (ColorEggItem)GREEN_EGG;
        greenEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem lightGrayEgg = (ColorEggItem)LIGHT_GRAY_EGG;
        lightGrayEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem lightBlueEgg = (ColorEggItem)LIGHT_BLUE_EGG;
        lightBlueEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem cyanEgg = (ColorEggItem)CYAN_EGG;
        cyanEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem redEgg = (ColorEggItem)RED_EGG;
        redEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem pinkEgg = (ColorEggItem)PINK_EGG;
        pinkEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem orangeEgg = (ColorEggItem)ORANGE_EGG;
        orangeEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem grayEgg = (ColorEggItem)GRAY_EGG;
        grayEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem brownEgg = (ColorEggItem)BROWN_EGG;
        brownEgg.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());
        ColorEggItem itemCatcher = (ColorEggItem)ITEM_CATCHER;
        itemCatcher.updateOdds(ModConfig.COMMON.chickenEggChance.get(), ModConfig.COMMON.chickenEggMultiChance.get());

    }

    private static Item.Properties itemProperties() {
        return new Item.Properties().tab(ModItemGroups.INSTANCE);
    }

    private static Item.Properties optionalItemProperties(String modId) {
        Item.Properties properties = new Item.Properties();
        if (ModList.get().isLoaded(modId)) {
            return properties.tab(ModItemGroups.INSTANCE);
        }

        return properties;
    }
}

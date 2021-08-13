package cn.evolvefield.mods.morechickens.init;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.item.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;


@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static Item ANALYZER;
    public static Item CHICKEN_SPAWN_EGG;
    public static Item CHICKEN_JAIL;
    public static Item STRONG_CHICKEN_JAIL;
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

    public static Item ITEM_CHICKEN;
    public static Item ITEM_CATCHER;

    public static Item ITEM_ROOST;
    public static Item ITEM_BREEDER;
    public static Item ITEM_COLLECTOR;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(
                ANALYZER = new AnalyzerItem().setRegistryName("analyzer"),
                CHICKEN_SPAWN_EGG = new ModSpawnEgg(ModDefaultEntities.BASE_CHICKEN, 0x734011, 0xa4b5bd, new Item.Properties().tab(ModItemGroups.INSTANCE)).setRegistryName("chicken_spawn_egg"),
                CHICKEN_JAIL =new JailItem(new Item.Properties().stacksTo(1).tab(ModItemGroups.INSTANCE), false).setRegistryName("chicken_jail"),
                STRONG_CHICKEN_JAIL = new JailItem(new Item.Properties().stacksTo(1).tab(ModItemGroups.INSTANCE), true).setRegistryName("strong_chicken_jail"),
                WATER_EGG = new BallItem(new Item.Properties().tab(ModItemGroups.INSTANCE),Fluids.WATER).setRegistryName("egg_water"),
                LAVA_EGG = new BallItem(new Item.Properties().tab(ModItemGroups.INSTANCE), Fluids.LAVA).setRegistryName("egg_lava"),
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
                ITEM_CATCHER = new CatcherItem(new Item.Properties()
                        .craftRemainder(Items.BUCKET)
                        .stacksTo(1)
                        .tab(ModItemGroups.INSTANCE)
                ).setRegistryName("catcher"),
                ITEM_CHICKEN = new ChickenItem(new Item.Properties()
                        .craftRemainder(Items.BUCKET)
                        .stacksTo(1)
                        .tab(ModItemGroups.INSTANCE)
                ).setRegistryName("chicken")



        );
    //manaHammer = BotaniaCompat.createManaHammerItem(optionalItemProperties(BotaniaCompat.MOD_ID)).setRegistryName(new ResourceLocation(ExCompressum.MOD_ID, "hammer_mana"))

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

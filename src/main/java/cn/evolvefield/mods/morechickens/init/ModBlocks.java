package cn.evolvefield.mods.morechickens.init;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.block.*;
import cn.evolvefield.mods.morechickens.core.block.utils.BaitType;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks {
    //dynamic registry

    public static Block[] BAITS;
    public static Block BLOCK_NEST;
    public static Block BLOCK_ROOST;
    public static Block BLOCK_BREEDER;
    public static Block BLOCK_COLLECTOR;


    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();

        BAITS = registerEnumBlock(registry, BaitType.values(), it -> it + BaitBlock.nameSuffix, BaitBlock::new);

        registry.registerAll(
                BLOCK_NEST = new NestBlock().setRegistryName("chicken_nest"),
                BLOCK_ROOST = new BlockRoost().setRegistryName("roost"),
                BLOCK_BREEDER = new BlockBreeder().setRegistryName("breeder"),
                BLOCK_COLLECTOR = new BlockCollector().setRegistryName("collector")
                  //evolvedOrechid = BotaniaCompat.createOrechidBlock().setRegistryName(new ResourceLocation(ExCompressum.MOD_ID, "evolved_orechid"))
        );


    }
    @SubscribeEvent
    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();

        registerEnumBlockItems(registry, BAITS);

        registry.registerAll(
                blockItem(BLOCK_NEST),
            blockItem(BLOCK_ROOST),
            blockItem(BLOCK_BREEDER),
            blockItem(BLOCK_COLLECTOR)

//                blockItem(rationingAutoCompressor),
//                blockItem(manaSieve, optionalItemProperties(BotaniaCompat.MOD_ID))
        );
    }




    private static <T extends Enum<T> & IStringSerializable> Block[] registerEnumBlock(IForgeRegistry<Block> registry, T[] types, Function<String, String> nameFactory, Function<T, Block> factory) {
        Block[] blocks = new Block[types.length];
        for (T type : types) {
            blocks[type.ordinal()] = factory.apply(type).setRegistryName(nameFactory.apply(type.getSerializedName()));
        }
        registry.registerAll(blocks);
        return blocks;
    }

    private static void registerEnumBlockItems(IForgeRegistry<Item> registry, Block[] blocks) {
        for (Block block : blocks) {
            registry.register(blockItem(block));
        }
    }

    private static Item blockItem(Block block) {
        return blockItem(block, new Item.Properties().tab(ModItemGroups.INSTANCE));
    }

    private static Item blockItem(Block block, Item.Properties properties) {
        return new BlockItem(block, properties).setRegistryName(Objects.requireNonNull(block.getRegistryName()));
    }

    private static Item.Properties optionalItemProperties(String modId) {
        Item.Properties properties = new Item.Properties();
        if (ModList.get().isLoaded(modId)) {
            return properties.tab(ModItemGroups.INSTANCE);
        }

        return properties;
    }
}

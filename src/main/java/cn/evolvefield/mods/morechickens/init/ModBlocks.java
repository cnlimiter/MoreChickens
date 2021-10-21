package cn.evolvefield.mods.morechickens.init;

import cn.evolvefield.mods.morechickens.client.render.item.BreederItemRenderer;
import cn.evolvefield.mods.morechickens.client.render.item.RoostItemRenderer;
import cn.evolvefield.mods.morechickens.common.block.BaitBlock;
import cn.evolvefield.mods.morechickens.common.block.BreederBlock;
import cn.evolvefield.mods.morechickens.common.block.CollectorBlock;
import cn.evolvefield.mods.morechickens.common.block.RoostBlock;
import cn.evolvefield.mods.morechickens.common.block.utils.BaitType;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {

    public static Block[] BAITS;
    public static Block BLOCK_ROOST = new RoostBlock();
    public static Block BLOCK_BREEDER = new BreederBlock();
    public static Block BLOCK_COLLECTOR = new CollectorBlock();


    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();
        //enumBlocks
        BAITS = registerEnumBlock(registry, BaitType.values(), it -> it + BaitBlock.nameSuffix, BaitBlock::new);

        //common
        registry.registerAll(
                BLOCK_ROOST ,
                BLOCK_BREEDER,
                BLOCK_COLLECTOR
                  //evolvedOrechid = BotaniaCompat.createOrechidBlock().setRegistryName(new ResourceLocation(ExCompressum.MOD_ID, "evolved_orechid"))
        );

        //renderType
        if (FMLEnvironment.dist == Dist.CLIENT) {
            RenderTypeLookup.setRenderLayer(ModBlocks.BLOCK_ROOST, RenderType.translucent());
            RenderTypeLookup.setRenderLayer(ModBlocks.BLOCK_COLLECTOR, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.BLOCK_BREEDER, RenderType.cutout());
        }

    }

    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();
        //enumBlocks
        registerEnumBlockItems(registry, BAITS);

        //common
        registry.registerAll(
                blockItemISTER(BLOCK_ROOST,() -> RoostItemRenderer::new),
            //blockItem(BLOCK_ROOST),
            blockItemISTER(BLOCK_BREEDER,() -> BreederItemRenderer::new),
            blockItem(BLOCK_COLLECTOR)


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

    private static Item blockItemISTER(Block block, Supplier<Callable<ItemStackTileEntityRenderer>> ister) {
        return new BlockItem(block, new Item.Properties().tab(ModItemGroups.INSTANCE).setISTER(ister)).setRegistryName(Objects.requireNonNull(block.getRegistryName()));
    }



    //impact
    private static Item.Properties optionalItemProperties(String modId) {
        Item.Properties properties = new Item.Properties();
        if (ModList.get().isLoaded(modId)) {
            return properties.tab(ModItemGroups.INSTANCE);
        }
        return properties;
    }
}

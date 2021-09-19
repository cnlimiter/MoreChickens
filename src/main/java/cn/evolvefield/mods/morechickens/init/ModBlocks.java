package cn.evolvefield.mods.morechickens.init;

import cn.evolvefield.mods.morechickens.client.render.item.BlockItemRendererBase;
import cn.evolvefield.mods.morechickens.client.render.item.BreederItemRenderer;
import cn.evolvefield.mods.morechickens.client.render.item.RoostItemRenderer;
import cn.evolvefield.mods.morechickens.common.block.BaitBlock;
import cn.evolvefield.mods.morechickens.common.block.BreederBlock;
import cn.evolvefield.mods.morechickens.common.block.CollectorBlock;
import cn.evolvefield.mods.morechickens.common.block.RoostBlock;
import cn.evolvefield.mods.morechickens.common.block.utils.BaitType;
import cn.evolvefield.mods.morechickens.common.util.render.CustomRendererBlockItem;
import cn.evolvefield.mods.morechickens.common.util.render.ItemRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;
import java.util.function.Function;

public class ModBlocks {
    //dynamic registry

    public static Block[] BAITS;

    public static final Block BLOCK_ROOST = new RoostBlock();
    public static final Block BLOCK_BREEDER = new BreederBlock();
    public static Block BLOCK_COLLECTOR = new CollectorBlock();



    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        final IForgeRegistry<Block> registry = event.getRegistry();

        BAITS = registerEnumBlock(registry, BaitType.values(), it -> it + BaitBlock.nameSuffix, BaitBlock::new);

        registry.registerAll(
                BLOCK_ROOST ,
                BLOCK_BREEDER ,
                BLOCK_COLLECTOR
                  //evolvedOrechid = BotaniaCompat.createOrechidBlock().setRegistryName(new ResourceLocation(ExCompressum.MOD_ID, "evolved_orechid"))
        );
        //renderType
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOCK_ROOST, RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOCK_COLLECTOR, RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOCK_BREEDER, RenderType.cutout());
        }

    }

    public static void registerBlockItems(RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();

        registerEnumBlockItems(registry, BAITS);

        registry.registerAll(

            toBreederItem(),
            toRoostItem(),
            blockItem(BLOCK_COLLECTOR)

//                blockItem(manaSieve, optionalItemProperties(BotaniaCompat.MOD_ID))
        );
    }




    private static <T extends Enum<T> & StringRepresentable> Block[] registerEnumBlock(IForgeRegistry<Block> registry, T[] types, Function<String, String> nameFactory, Function<T, Block> factory) {
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

    private static Item toBreederItem() {
        return new CustomRendererBlockItem(BLOCK_BREEDER, new Item.Properties().tab(ModItemGroups.INSTANCE)) {
            @OnlyIn(Dist.CLIENT)
            @Override
            public ItemRenderer createItemRenderer() {
                return new BreederItemRenderer();
            }
        }.setRegistryName(Objects.requireNonNull(BLOCK_BREEDER.getRegistryName()));
    }

    private static Item toRoostItem() {
        return new CustomRendererBlockItem(BLOCK_ROOST, new Item.Properties().tab(ModItemGroups.INSTANCE)) {
            @OnlyIn(Dist.CLIENT)
            @Override
            public ItemRenderer createItemRenderer() {
                return new RoostItemRenderer();
            }
        }.setRegistryName(Objects.requireNonNull(BLOCK_ROOST.getRegistryName()));
    }

    private static Item.Properties optionalItemProperties(String modId) {
        Item.Properties properties = new Item.Properties();
        if (ModList.get().isLoaded(modId)) {
            return properties.tab(ModItemGroups.INSTANCE);
        }

        return properties;
    }
}

package cn.evolvefield.mods.morechickens.init;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.client.render.tile.BreederRenderer;
import cn.evolvefield.mods.morechickens.client.render.tile.RoostRenderer;
import cn.evolvefield.mods.morechickens.common.tile.BaitTileEntity;
import cn.evolvefield.mods.morechickens.common.tile.CollectorTileEntity;
import cn.evolvefield.mods.morechickens.common.tile.BreederTileEntity;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTileEntities {

    public static TileEntityType<BaitTileEntity> BAIT;
    public static TileEntityType<RoostTileEntity> ROOST;
    public static TileEntityType<BreederTileEntity> BREEDER;
    public static TileEntityType<CollectorTileEntity> TILE_COLLECTOR;

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        final IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.registerAll(

                BAIT = build(BaitTileEntity::new, new ResourceLocation(MoreChickens.MODID, "bait"), ModBlocks.BAITS),
                //TILE_ROOST = build(RoostTileEntity::new,"roost",ModBlocks.BLOCK_ROOST),
                TILE_COLLECTOR = build(CollectorTileEntity::new,"collector",ModBlocks.BLOCK_COLLECTOR)


        );
        BREEDER = TileEntityType.Builder.of(BreederTileEntity::new, ModBlocks.BLOCK_BREEDER).build(null);
        BREEDER.setRegistryName(new ResourceLocation(MoreChickens.MODID, "breeder"));
        event.getRegistry().register(BREEDER);

        ROOST = TileEntityType.Builder.of(RoostTileEntity::new, ModBlocks.BLOCK_ROOST).build(null);
        ROOST.setRegistryName(new ResourceLocation(MoreChickens.MODID, "roost"));
        event.getRegistry().register(ROOST);
    }


    @SuppressWarnings("unchecked")
    private static <T extends TileEntity> TileEntityType<T> build(Supplier<T> factory, String registryName, Block... block) {
        //noinspection ConstantConditions
        return (TileEntityType<T>) TileEntityType.Builder.of(factory, block).build(null).setRegistryName(registryName);
    }

    @SuppressWarnings("unchecked")
    private static <T extends TileEntity> TileEntityType<T> build(Supplier<T> factory, ResourceLocation registryName, Block... block) {
        //noinspection ConstantConditions
        return (TileEntityType<T>) TileEntityType.Builder.of(factory, block).build(null).setRegistryName(registryName);
    }

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MoreChickens.MODID);


    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {

        ClientRegistry.bindTileEntityRenderer(ModTileEntities.BREEDER, BreederRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntities.ROOST, RoostRenderer::new);
    }

}

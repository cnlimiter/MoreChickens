package cn.evolvefield.mods.morechickens.init;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.tile.*;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTileEntities {

    public static TileEntityType<BaitTileEntity> BAIT;
    public static TileEntityType<NestTileEntity> CHICKEN_NEST;
    public static TileEntityType<TileEntityRoost> TILE_ROOST;
    public static TileEntityType<TileEntityBreeder> TILE_BREEDER;
    public static TileEntityType<TileEntityCollector> TILE_COLLECTOR;

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        final IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.registerAll(

                BAIT = build(BaitTileEntity::new, new ResourceLocation(MoreChickens.MODID, "bait"), ModBlocks.BAITS),
                CHICKEN_NEST = build(NestTileEntity::new,"chicken_nest",ModBlocks.BLOCK_NEST),
                TILE_ROOST = build(TileEntityRoost::new,"roost",ModBlocks.BLOCK_ROOST),
                TILE_BREEDER = build(TileEntityBreeder::new,"breeder",ModBlocks.BLOCK_BREEDER),
                TILE_COLLECTOR = build(TileEntityCollector::new,"collector",ModBlocks.BLOCK_COLLECTOR)

        );
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


//    public static final RegistryObject<TileEntityType<?>> CHICKEN_NEST = TILE_ENTITIES.register("chicken_nest",
//            () -> TileEntityType.Builder.of(NestTileEntity::new, ModBlocks.BLOCK_NEST).build(Util.fetchChoiceType(TypeReferences.BLOCK_ENTITY, "chicken_nest")));

}

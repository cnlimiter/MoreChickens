package cn.evolvefield.mods.morechickens.init;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.tile.*;

import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTileEntities {

    public static BlockEntityType<BaitTileEntity> BAIT;
    public static BlockEntityType<NestTileEntity> CHICKEN_NEST;
    public static BlockEntityType<RoostTileEntity> TILE_ROOST;
    public static BlockEntityType<BreederTileEntity> TILE_BREEDER;
    public static BlockEntityType<CollectorTileEntity> TILE_COLLECTOR;

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        final IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();
        registry.registerAll(
                BAIT = build(BaitTileEntity::new, new ResourceLocation(MoreChickens.MODID, "bait"), ModBlocks.BAITS),
                CHICKEN_NEST = build(NestTileEntity::new,"nest",ModBlocks.BLOCK_NEST),
                TILE_ROOST = build(RoostTileEntity::new,"roost",ModBlocks.BLOCK_ROOST),
                TILE_BREEDER = build(BreederTileEntity::new,"breeder",ModBlocks.BLOCK_BREEDER),
                TILE_COLLECTOR = build(CollectorTileEntity::new,"collector",ModBlocks.BLOCK_COLLECTOR)

        );
    }


    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityType<T> build(BlockEntityType.BlockEntitySupplier<T> factory, String registryName, Block... block) {
        //noinspection ConstantConditions
        return (BlockEntityType<T>) BlockEntityType.Builder.of(factory, block).build(null).setRegistryName(registryName);
    }

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityType<T> build(BlockEntityType.BlockEntitySupplier<T> factory, ResourceLocation registryName, Block... block) {
        //noinspection ConstantConditions
        return (BlockEntityType<T>) BlockEntityType.Builder.of(factory, block).build(null).setRegistryName(registryName);
    }



//    public static final RegistryObject<BlockEntityType<?>> CHICKEN_NEST = TILE_ENTITIES.register("chicken_nest",
//            () -> BlockEntityType.Builder.of(NestTileEntity::new, ModBlocks.BLOCK_NEST).build(Util.fetchChoiceType(TypeReferences.BLOCK_ENTITY, "chicken_nest")));

}

package cn.evolvefield.mods.morechickens.init;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.container.BreederContainer;
import cn.evolvefield.mods.morechickens.core.container.CollectorContainer;
import cn.evolvefield.mods.morechickens.core.container.RoostContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class ModContainers
{

    public static ContainerType<RoostContainer> CONTAINER_ROOST;
    public static ContainerType<BreederContainer> CONTAINER_BREEDER;
    public static ContainerType<CollectorContainer> CONTAINER_COLLECTOR;


    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        final IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.register(
                CONTAINER_ROOST = register("roost", RoostContainer::new));

        registry.register(
                CONTAINER_BREEDER = register("breeder", BreederContainer::new));

        registry.register(
                CONTAINER_COLLECTOR = register("collector", CollectorContainer::new));


    }





    @SuppressWarnings("unchecked")
    private static <T extends Container> ContainerType<T> register(String name, IContainerFactory<T> containerFactory) {
        return (ContainerType<T>) new ContainerType<>(containerFactory).setRegistryName(name);
    }

}

package cn.evolvefield.mods.morechickens.init;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.container.ContainerBreeder;
import cn.evolvefield.mods.morechickens.core.container.ContainerCollector;
import cn.evolvefield.mods.morechickens.core.container.ContainerRoost;
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

    public static ContainerType<ContainerRoost> CONTAINER_ROOST;
    public static ContainerType<ContainerBreeder> CONTAINER_BREEDER;
    public static ContainerType<ContainerCollector> CONTAINER_COLLECTOR;


    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        final IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.register(
                CONTAINER_ROOST = register("roost", ContainerRoost::new));

        registry.register(
                CONTAINER_BREEDER = register("breeder", ContainerBreeder::new));

        registry.register(
                CONTAINER_COLLECTOR = register("collector", ContainerCollector::new));


    }





    @SuppressWarnings("unchecked")
    private static <T extends Container> ContainerType<T> register(String name, IContainerFactory<T> containerFactory) {
        return (ContainerType<T>) new ContainerType<>(containerFactory).setRegistryName(name);
    }

}

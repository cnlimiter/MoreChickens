package cn.evolvefield.mods.morechickens.init;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.container.ContainerBreeder;
import cn.evolvefield.mods.morechickens.core.container.ContainerCollector;
import cn.evolvefield.mods.morechickens.core.container.ContainerRoost;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fmllegacy.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class ModContainers
{

    public static MenuType<ContainerRoost> CONTAINER_ROOST;
    public static MenuType<ContainerBreeder> CONTAINER_BREEDER;
    public static MenuType<ContainerCollector> CONTAINER_COLLECTOR;


    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        final IForgeRegistry<MenuType<?>> registry = event.getRegistry();
        registry.register(
                CONTAINER_ROOST = register("roost", ContainerRoost::new));

        registry.register(
                CONTAINER_BREEDER = register("breeder", ContainerBreeder::new));

        registry.register(
                CONTAINER_COLLECTOR = register("collector", ContainerCollector::new));


    }





    @SuppressWarnings("unchecked")
    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, IContainerFactory<T> containerFactory) {
        return (MenuType<T>) new MenuType<>(containerFactory).setRegistryName(name);
    }

}

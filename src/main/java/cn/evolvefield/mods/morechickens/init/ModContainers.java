package cn.evolvefield.mods.morechickens.init;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.client.gui.BreederScreen;
import cn.evolvefield.mods.morechickens.client.gui.RoostScreen;
import cn.evolvefield.mods.morechickens.client.gui.CollectorScreen;
import cn.evolvefield.mods.morechickens.common.container.BreederContainer;
import cn.evolvefield.mods.morechickens.common.container.CollectorContainer;
import cn.evolvefield.mods.morechickens.common.container.RoostContainer;
import cn.evolvefield.mods.morechickens.init.registry.ClientRegistry;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;


public class ModContainers
{

    public static ContainerType<BreederContainer> BREEDER_CONTAINER;
    public static ContainerType<RoostContainer> ROOST_CONTAINER;
    public static ContainerType<CollectorContainer> CONTAINER_COLLECTOR;


    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        final IForgeRegistry<ContainerType<?>> registry = event.getRegistry();


        registry.register(
                CONTAINER_COLLECTOR = register("collector", CollectorContainer::new));

        registry.register(
                ROOST_CONTAINER = register("roost", RoostContainer::new));

        registry.register(
                BREEDER_CONTAINER = register("breeder", BreederContainer::new));

//        BREEDER_CONTAINER = new ContainerType<>(BreederContainer::new);
//        BREEDER_CONTAINER.setRegistryName(new ResourceLocation(MoreChickens.MODID, "breeder"));
//        event.getRegistry().register(BREEDER_CONTAINER);

//        ROOST_CONTAINER = new ContainerType<>(RoostContainer::new);
//        ROOST_CONTAINER.setRegistryName(new ResourceLocation(MoreChickens.MODID, "roost"));
//        event.getRegistry().register(ROOST_CONTAINER);


    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        ClientRegistry.<BreederContainer, BreederScreen>registerScreen(BREEDER_CONTAINER, BreederScreen::new);
        ClientRegistry.<RoostContainer, RoostScreen>registerScreen(ROOST_CONTAINER, RoostScreen::new);
        ClientRegistry.<CollectorContainer, CollectorScreen>registerScreen(CONTAINER_COLLECTOR, CollectorScreen::new);

    }



    @SuppressWarnings("unchecked")
    private static <T extends Container> ContainerType<T> register(String name, IContainerFactory<T> containerFactory) {
        return (ContainerType<T>) new ContainerType<>(containerFactory).setRegistryName(name);
    }

}

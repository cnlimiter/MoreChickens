package cn.evolvefield.mods.morechickens.init;


import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.client.gui.BreederScreen;
import cn.evolvefield.mods.morechickens.client.gui.CollectorScreen;
import cn.evolvefield.mods.morechickens.client.gui.RoostScreen;
import cn.evolvefield.mods.morechickens.common.container.BreederContainer;
import cn.evolvefield.mods.morechickens.common.container.CollectorContainer;
import cn.evolvefield.mods.morechickens.common.container.RoostContainer;

import cn.evolvefield.mods.morechickens.init.registry.ClientRegistry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fmllegacy.network.IContainerFactory;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class ModContainers
{

    public static MenuType<RoostContainer> CONTAINER_ROOST;
    public static MenuType<BreederContainer> CONTAINER_BREEDER;
    public static MenuType<CollectorContainer> CONTAINER_COLLECTOR;


    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        final IForgeRegistry<MenuType<?>> registry = event.getRegistry();
        registry.register(
                CONTAINER_ROOST = register("roost", RoostContainer::new));

        registry.register(
                CONTAINER_BREEDER = register("breeder", BreederContainer::new));

        registry.register(
                CONTAINER_COLLECTOR = register("collector", CollectorContainer::new));


    }


    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        ClientRegistry.<BreederContainer, BreederScreen>registerScreen(CONTAINER_BREEDER, BreederScreen::new);
        ClientRegistry.<RoostContainer, RoostScreen>registerScreen(CONTAINER_ROOST, RoostScreen::new);
        ClientRegistry.<CollectorContainer, CollectorScreen>registerScreen(CONTAINER_COLLECTOR, CollectorScreen::new);

    }


    @SuppressWarnings("unchecked")
    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, IContainerFactory<T> containerFactory) {
        return (MenuType<T>) new MenuType<>(containerFactory).setRegistryName(name);
    }

}

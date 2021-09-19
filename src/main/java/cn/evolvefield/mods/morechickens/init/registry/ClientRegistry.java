package cn.evolvefield.mods.morechickens.init.registry;


import com.mojang.blaze3d.platform.ScreenManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientRegistry {
    public ClientRegistry() {
    }

    public static KeyMapping registerKeyBinding(String name, String category, int keyCode) {
        KeyMapping keyBinding = new KeyMapping(name, keyCode, category);
        net.minecraftforge.fmlclient.registry.ClientRegistry.registerKeyBinding(keyBinding);
        return keyBinding;
    }

    public static <C extends AbstractContainerMenu, S extends AbstractContainerScreen<C>> void registerScreen(MenuType<C> containerType, MenuScreens.ScreenConstructor<C, S> screenFactory) {
        MenuScreens.register(containerType, screenFactory);
    }
}

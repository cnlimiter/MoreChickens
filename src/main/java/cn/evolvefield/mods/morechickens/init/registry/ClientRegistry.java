package cn.evolvefield.mods.morechickens.init.registry;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientRegistry {
    public ClientRegistry() {
    }

    public static KeyBinding registerKeyBinding(String name, String category, int keyCode) {
        KeyBinding keyBinding = new KeyBinding(name, keyCode, category);
        net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(keyBinding);
        return keyBinding;
    }

    public static <C extends Container, S extends ContainerScreen<C>> void registerScreen(ContainerType<C> containerType, ScreenManager.IScreenFactory<C, S> screenFactory) {
        ScreenManager.register(containerType, screenFactory);
    }
}

package cn.evolvefield.mods.morechickens.init.event;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.item.ColorEggItem;
import cn.evolvefield.mods.morechickens.common.item.ModSpawnEgg;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBus {

    @SubscribeEvent
    public static void onRegisterEntities(RegistryEvent.Register<EntityType<?>> event){
        ModSpawnEgg.registerMobs();
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event){
        ColorEggItem.registerDispenser();
    }

}

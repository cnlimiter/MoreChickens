package cn.evolvefield.mods.morechickens.init;

import cn.evolvefield.mods.morechickens.MoreChickens;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = MoreChickens.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public class ModRecipeTypes {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MoreChickens.MODID);


    public static <B extends IRecipeSerializer<?>> RegistryObject<B> createRecipeType(String name, Supplier<? extends B> supplier) {
        return RECIPE_SERIALIZERS.register(name, supplier);
    }

}

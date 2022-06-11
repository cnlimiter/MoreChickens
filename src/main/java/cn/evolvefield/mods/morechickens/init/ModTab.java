package cn.evolvefield.mods.morechickens.init;


import cn.evolvefield.mods.morechickens.MoreChickens;

import cn.evolvefield.mods.morechickens.init.registry.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModTab extends CreativeModeTab {
    public ModTab() {
        super(MoreChickens.MODID);
    }


    public static final ModTab INSTANCE = new ModTab();

    @Override
    public @NotNull ItemStack makeIcon() {
        return new ItemStack(ModItems.ANALYZER);
    }


}

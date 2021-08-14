package cn.evolvefield.mods.morechickens.init;


import cn.evolvefield.mods.morechickens.MoreChickens;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups extends CreativeModeTab {
    public ModItemGroups() {
        super(MoreChickens.MODID);
    }


    public static final ModItemGroups INSTANCE = new ModItemGroups();

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ModItems.ANALYZER);
    }


}

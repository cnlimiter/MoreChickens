package cn.evolvefield.mods.morechickens.init;

import cn.evolvefield.mods.atomlib.utils.lang.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/2 12:45
 * Version: 1.0
 */
public class ModTooltips {
    public static final Tooltip ADDED_BY = new Tooltip("tooltip.chickens.added_by");
    public static final Tooltip CHICKEN_ID = new Tooltip("tooltip.chickens.chicken_id");


    public static Component getAddedByTooltip(String modid) {
        var name = ModList.get().getModFileById(modid).getMods().get(0).getDisplayName();
        return ADDED_BY.args(name).build();
    }
}

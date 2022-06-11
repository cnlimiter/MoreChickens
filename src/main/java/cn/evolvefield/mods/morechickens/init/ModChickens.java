package cn.evolvefield.mods.morechickens.init;

import cn.evolvefield.mods.morechickens.Static;
import cn.evolvefield.mods.morechickens.common.entity.core.ChickenIns;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/10 18:04
 * Version: 1.0
 */
public class ModChickens {

    public static final ChickenIns COAL = new ChickenIns(new ResourceLocation(Static.MOD_ID, "coal"),
            "chickens.coal", 2, new int[]{0x363739, 0x261E24}, Ingredient.of(Items.COAL),
            1, 6000, 0, null, null);



    public static List<ChickenIns> getDefaults() {
        return List.of(
                COAL,
                IRON,
                LAPIS_LAZULI,
                REDSTONE,
                GLOWSTONE,
                GOLD,
                DIAMOND,
                EMERALD,
                ALUMINUM,
                COPPER,
                TIN,
                BRONZE,
                SILVER,
                LEAD,
                STEEL,
                NICKEL,
                ELECTRUM,
                INVAR,
                PLATINUM
        );
    }
}

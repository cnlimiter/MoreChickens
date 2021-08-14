package cn.evolvefield.mods.morechickens.core.entity.util;


import net.minecraft.world.level.biome.Biome;

/**
 * Created by setyc on 28.02.2016.
 */
public enum SpawnType {
    NORMAL, SNOW, NONE, HELL;

    public static String[] names() {
        SpawnType[] states = values();
        String[] names = new String[states.length];

        for (int i = 0; i < states.length; i++) {
            names[i] = states[i].name();
        }

        return names;
    }

    public static SpawnType getSpawnType(Biome biome) {
        if (biome.getBiomeCategory() == Biome.BiomeCategory.NETHER) {
            return SpawnType.HELL;
        }

        if (biome.getBiomeCategory() == Biome.BiomeCategory.EXTREME_HILLS || biome.getPrecipitation() == Biome.Precipitation.SNOW) {
            return SpawnType.SNOW;
        }

        return SpawnType.NORMAL;
    }
}

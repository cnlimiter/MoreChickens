package cn.evolvefield.mods.morechickens.core.block.utils;

public enum EnvironmentalCondition {
    CanSpawn("chickens.tooltip.baitCanSpawn"),
    NearbyBait("chickens.tooltip.baitNearbyBait"),
    WrongEnv("chickens.tooltip.baitWrongEnv"),
    NearbyAnimal("chickens.tooltip.baitNearbyAnimal"),
    NoWater("chickens.tooltip.baitNoWater");

    public final String langKey;

    EnvironmentalCondition(String langKey) {
        this.langKey = langKey;
    }
}

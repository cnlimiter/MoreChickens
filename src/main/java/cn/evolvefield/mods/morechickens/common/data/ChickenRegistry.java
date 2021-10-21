package cn.evolvefield.mods.morechickens.common.data;

import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
import cn.evolvefield.mods.morechickens.common.util.math.RandomCollection;
import cn.evolvefield.mods.morechickens.common.util.math.RandomPool;
import cn.evolvefield.mods.morechickens.common.util.math.UnorderedPair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChickenRegistry {
    public static Map<String, ChickenData> Types = new HashMap<>();

    public static Map<UnorderedPair<String>, RandomPool<String>> Pairings = new HashMap<>();

    public static final Map<Pair<String, String>, RandomCollection<ChickenData>> FAMILY_TREE = new LinkedHashMap<>();

    public static void buildFamilyTree() {
        FAMILY_TREE.clear();
        ChickenReloadListener.INSTANCE.getData().values().stream()
                .filter(ChickenData::hasValidParents)
                .forEach(ChickenUtils::addBreedPairToFamilyTree);
    }


    public static boolean containsChickenType(String type) {
        return Types.containsKey(type);
    }

    public static double getAdjustedWeightForChild(ChickenData data) {
        return FAMILY_TREE.get(data.getParents()).getAdjustedWeight(data.getWeight());
    }


}

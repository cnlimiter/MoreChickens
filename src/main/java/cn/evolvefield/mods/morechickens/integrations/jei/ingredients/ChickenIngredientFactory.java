//package cn.evolvefield.mods.morechickens.integrations.jei.ingredients;
//
//import cn.evolvefield.mods.morechickens.common.data.custom.ChickenReloadListener;
//import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
//import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
//import cn.evolvefield.mods.morechickens.init.ModEntities;
//import net.minecraft.entity.EntityType;
//
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Supplier;
//
//public class ChickenIngredientFactory
//{
//    private static Map<String,ChickenType> ingredientList = new HashMap<>();
//    private static int ChickenIngredientCount = 0; // counter to see if list needs to be recalculated
//
//    public static String getIngredientKey(BaseChickenEntity chicken) {
//        String type = chicken.getChickenName();
//        return type;
//    }
//
//    public static Map<String, ChickenType> getOrCreateList(boolean removeDeprecated) {
//        Map<String, ChickenType> list = new HashMap<>();
//        if (removeDeprecated) {
//            for (Map.Entry<String, ChickenType> entry : getOrCreateList().entrySet()) {
//                    list.put(entry.getKey(), entry.getValue());
//            }
//        } else {
//            list = getOrCreateList();
//        }
//        return list;
//    }
//
//    public static Supplier<ChickenType> getIngredient(String name) {
//        return () -> getOrCreateList().get(name);
//    }
//
//    public static Map<String, ChickenType> getOrCreateList() {
//
//
//        // Add configured bees
//        if (ChickenIngredientCount != ChickenReloadListener.INSTANCE.getData().size()) {
//            ChickenIngredientCount = 0;
//            for (Map.Entry<String, ChickenType> entry : ChickenReloadListener.INSTANCE.getData().entrySet()) {
//                String chickenType = entry.getKey();
//                EntityType<BaseChickenEntity> chicken = ModEntities.BASE_CHICKEN.get();
//                addChicken(chickenType, new ChickenType(chicken, ChickenType.Types.get(chickenType)));
//                ChickenIngredientCount++;
//            }
//        }
//
//        return ingredientList;
//    }
//
//    public static void addChicken(String name, ChickenType chicken) {
//        ingredientList.put(name, chicken);
//    }
//
//    public static Map<String, ChickenType> getRBeesIngredients() {
//        Map<String, ChickenType> list = new HashMap<>(getOrCreateList());
//        list.entrySet().removeIf(entry -> !entry.getKey().contains("resourcefulbees"));
//        return list;
//    }
//}

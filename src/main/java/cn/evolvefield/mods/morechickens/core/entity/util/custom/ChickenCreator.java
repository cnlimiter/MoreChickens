package cn.evolvefield.mods.morechickens.core.entity.util.custom;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ChickenCreator {
    public static CompoundNBT create(ResourceLocation id, JsonObject json) {
        CompoundNBT data = new CompoundNBT();
        data.putString("id", id.toString());
        data.putString("Name", json.has("name") ? json.get("name").getAsString() : idToName(id.getPath()) + " Chicken");
        if (json.has("description")) {
            data.putString("description", json.get("description").getAsString());
        }
        if (json.has("layItem")) {
            data.putString("layItem", json.get("layItem").getAsString());
        }
        if (json.has("layAmount")) {
            data.putInt("layAmount", json.get("layAmount").getAsInt());
        }
        if (json.has("layRandomAmount")) {
            data.putInt("layRandomAmount", json.get("layRandomAmount").getAsInt());
        }
        if (json.has("layTime")) {
            data.putInt("layTime", json.get("layTime").getAsInt());
        }
        if (json.has("deathItem")) {
            data.putString("deathItem", json.get("deathItem").getAsString());
        }
        if (json.has("deathAmount")) {
            data.putInt("deathAmount", json.get("deathAmount").getAsInt());
        }
//        if (json.has("deathAmount")) {
//            data.putInt("deathAmount", json.get("deathAmount").getAsInt());
//        }
//        if (json.has("deathAmount")) {
//            data.putInt("deathAmount", json.get("deathAmount").getAsInt());
//        }
        return data;
    }

    public static String idToName(String givenString) {
        String[] arr = givenString.replace("_", " ").split(" ");
        StringBuilder sb = new StringBuilder();

        for (String s : arr) {
            sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}

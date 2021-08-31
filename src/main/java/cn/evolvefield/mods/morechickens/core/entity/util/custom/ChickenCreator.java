package cn.evolvefield.mods.morechickens.core.entity.util.custom;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ChickenCreator {
    public static CompoundNBT create(ResourceLocation id, JsonObject json) {
        CompoundNBT data = new CompoundNBT();
        data.putString("id", id.toString());
        data.putString("Name", json.has("name") ? json.get("name").getAsString() : id.toString());

        if (json.has("LayItem")) {
            data.putString("LayItem", json.get("LayItem").getAsString());
        }
        if (json.has("LayAmount")) {
            data.putInt("LayAmount", json.get("LayAmount").getAsInt());
        }
        if (json.has("LayRandomAmount")) {
            data.putInt("LayRandomAmount", json.get("LayRandomAmount").getAsInt());
        }
        if (json.has("LayTime")) {
            data.putInt("LayTime", json.get("LayTime").getAsInt());
        }
        if (json.has("DeathItem")) {
            data.putString("DeathItem", json.get("DeathItem").getAsString());
        }
        if (json.has("DeathAmount")) {
            data.putInt("DeathAmount", json.get("DeathAmount").getAsInt());
        }
        if (json.has("Tier")) {
            data.putInt("Tier", json.get("Tier").getAsInt());
        }
        if (json.has("Parent1")) {
            data.putString("Parent1", json.get("Parent1").getAsString());
        }
        if (json.has("Parent2")) {
            data.putString("Parent2", json.get("Parent2").getAsString());
        }
        if (json.has("Enable")) {
            data.putBoolean("Enable", json.get("Enable").getAsBoolean());
        }

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

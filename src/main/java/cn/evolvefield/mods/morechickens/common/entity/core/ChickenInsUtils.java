package cn.evolvefield.mods.morechickens.common.entity.core;

import cn.evolvefield.mods.atomlib.utils.NBTUtil;
import cn.evolvefield.mods.morechickens.Static;
import cn.evolvefield.mods.morechickens.init.registry.ModItems;
import cn.evolvefield.mods.morechickens.init.handler.ChickenInsRegistryHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/2 12:39
 * Version: 1.0
 */
public class ChickenInsUtils {
    public static ChickenIns loadFromJson(ResourceLocation id, JsonObject json, ICondition.IContext context) {
        if (!CraftingHelper.processConditions(json, "conditions", context)) {
            Static.LOGGER.info("Skipping loading Chickens {} as its conditions were not met", id);
            return null;
        }
        var name = GsonHelper.getAsString(json, "name");
        var parent1 = GsonHelper.getAsString(json, "parent1", "");
        var parent2 = GsonHelper.getAsString(json, "parent2", "");

        var colors = GsonHelper.getAsJsonArray(json, "colors");
        int overlayColor = Integer.parseInt(colors.get(0).getAsString(), 16);
        int underlayColor = Integer.parseInt(colors.get(1).getAsString(), 16);

        var enabled = GsonHelper.getAsBoolean(json, "enabled", true);

        ChickenIns singularity;
        var layIngredient = GsonHelper.getAsJsonObject(json, "layIngredient", null);

        var tier = GsonHelper.getAsInt(json, "tier", 0);
        var layCount = GsonHelper.getAsInt(json, "layCount", 0);
        var layRandomCount = GsonHelper.getAsInt(json, "layRandomCount", 0);
        var layTime = GsonHelper.getAsInt(json, "layTime", 6000);



        if (layIngredient == null) {
            singularity = new ChickenIns(id, name, tier, new int[]{overlayColor, underlayColor}, Ingredient.EMPTY,
                    layCount, layTime, layRandomCount, parent1, parent2);
        } else if (layIngredient.has("tag")) {
            var tag = layIngredient.get("tag").getAsString();

            singularity = new ChickenIns(id, name, tier, new int[]{overlayColor, underlayColor}, tag,
                    layCount, layTime, layRandomCount, parent1, parent2);
        } else {
            var ingredient = Ingredient.fromJson(json.get("layIngredient"));
            singularity = new ChickenIns(id, name, tier, new int[]{overlayColor, underlayColor}, ingredient,
                    layCount, layTime, layRandomCount, parent1, parent2);
        }


        singularity.setEnabled(enabled);

        return singularity;
    }

    public static JsonObject writeToJson(ChickenIns singularity) {
        var json = new JsonObject();

        json.addProperty("name", singularity.getName());

        var colors = new JsonArray();

        colors.add(Integer.toString(singularity.getOverlayColor(), 16));
        colors.add(Integer.toString(singularity.getUnderlayColor(), 16));

        json.add("colors", colors);
        json.addProperty("parent1", singularity.getParent1());
        json.addProperty("parent2", singularity.getParent2());
        json.addProperty("tier", singularity.getTier());
        json.addProperty("layCount", singularity.getLayCount());
        json.addProperty("layRandomCount", singularity.getLayRandomCount());
        json.addProperty("layTime", singularity.getLayTime());

        JsonElement ingredient;
        if (singularity.getLayTag() != null) {
            var obj = new JsonObject();
            obj.addProperty("tag", singularity.getLayTag());
            ingredient = obj;

            var array = new JsonArray();
            var main = new JsonObject();

            var sub = new JsonObject();
            main.addProperty("type", "forge:not");

            sub.addProperty("tag", singularity.getLayTag());
            sub.addProperty("type", "forge:tag_empty");

            main.add("value", sub);
            array.add(main);
            json.add("conditions", array);

        } else {
            ingredient = singularity.getLayIngredient().toJson();
        }

        json.add("layIngredient", ingredient);

        if (!singularity.isEnabled()) {
            json.addProperty("enabled", false);
        }

        return json;
    }

    public static CompoundTag makeTag(ChickenIns chickenIns) {
        var nbt = new CompoundTag();

        nbt.putString("Id", chickenIns.getId().toString());

        return nbt;
    }

    public static ItemStack getItemForChickenIns(ChickenIns chickenIns) {
        var nbt = makeTag(chickenIns);
        var stack = new ItemStack(ModItems.ITEM_CHICKEN);

        stack.setTag(nbt);

        return stack;
    }

    public static ChickenIns getChickenIns(ItemStack stack) {
        var id = NBTUtil.getString(stack, "Id");
        if (!id.isEmpty()) {
            return ChickenInsRegistryHandler.getInstance().getChickensById(ResourceLocation.tryParse(id));
        }

        return null;
    }
}

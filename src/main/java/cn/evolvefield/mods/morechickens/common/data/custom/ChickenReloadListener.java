package cn.evolvefield.mods.morechickens.common.data.custom;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ChickenReloadListener extends JsonReloadListener {
    public static RecipeManager recipeManager;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final ChickenReloadListener INSTANCE = new ChickenReloadListener();
    private Map<String, ChickenData> CHICKEN_DATA = new HashMap<>();

    public ChickenReloadListener() {
        super(GSON, "custom");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, @Nonnull IResourceManager resourceManager, IProfiler profiler) {
        profiler.push("ChickenReloadListener");

        Map<String, ChickenData> data = new HashMap<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : dataMap.entrySet()) {
            ResourceLocation id = entry.getKey();

            try {
                if (!CraftingHelper.processConditions(entry.getValue().getAsJsonObject(), "conditions")) {
                    MoreChickens.LOGGER.debug("Skipping loading chickens {} as its conditions were not met", id);
                    continue;
                }
            } catch (Exception e) {
                MoreChickens.LOGGER.debug("Skipping loading chickens {} as its conditions were invalid", id);
                throw e;
            }

            ResourceLocation simpleId = id.getPath().contains("/") ? new ResourceLocation(id.getNamespace(), id.getPath().substring(id.getPath().lastIndexOf("/") + 1)) : id;
            ChickenData chicken = ChickenCreator.create(simpleId,entry.getValue().getAsJsonObject());

            data.remove(simpleId.toString());
            data.put(simpleId.toString(), chicken);

            MoreChickens.LOGGER.debug("Adding to chicken data " + simpleId);
        }

        setData(data);

        profiler.popPush("ChickenReloadListener");
    }

    public ChickenData getData(String id) {
        return CHICKEN_DATA.get(id);
    }

    public Map<String, ChickenData> getData() {
        return CHICKEN_DATA;
    }

    public void setData(Map<String, ChickenData> data) {
        CHICKEN_DATA = data;
        if (ModList.get().isLoaded("patchouli")) {
            //ProductiveBeesPatchouli.setBeeFlags();
        }
    }
}

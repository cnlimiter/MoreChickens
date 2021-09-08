package cn.evolvefield.mods.morechickens.core.data.custom;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.util.main.ChickenType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ChickenReloadListener extends SimpleJsonResourceReloadListener {
    public static RecipeManager recipeManager;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static final ChickenReloadListener INSTANCE = new ChickenReloadListener();
    private Map<String, ChickenType> CHICKEN_DATA = new HashMap<>();

    public ChickenReloadListener() {
        super(GSON, "custom_chickens");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, @Nonnull ResourceManager resourceManager, ProfilerFiller profiler) {
        profiler.push("ChickenReloadListener");

        Map<String, ChickenType> data = new HashMap<>();
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
            ChickenType chicken = ChickenCreator.create(simpleId,entry.getValue().getAsJsonObject());

            data.remove(simpleId.toString());
            data.put(simpleId.toString(), chicken);

            MoreChickens.LOGGER.debug("Adding to chicken data " + simpleId);
        }

        setData(data);

        profiler.popPush("ChickenReloadListener");
    }

    public ChickenType getData(String id) {
        return CHICKEN_DATA.get(id);
    }

    public Map<String, ChickenType> getData() {
        return CHICKEN_DATA;
    }

    public void setData(Map<String, ChickenType> data) {
        CHICKEN_DATA = data;
        if (ModList.get().isLoaded("patchouli")) {
            //ProductiveBeesPatchouli.setBeeFlags();
        }
    }
}

package cn.evolvefield.mods.morechickens.common.data.custom;

import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
import cn.evolvefield.mods.morechickens.common.util.math.RandomPool;
import cn.evolvefield.mods.morechickens.common.util.math.UnorderedPair;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.evolvefield.mods.morechickens.common.util.main.ChickenType.Pairings;


public class ChickenCreator {
    private static final Logger LOGGER = LogManager.getLogger();


    public static ChickenType create(ResourceLocation id, JsonObject json) throws JsonSyntaxException {
        List<Float> tiers = Arrays.stream(ModConfig.COMMON.tierOdds)
                .map(ForgeConfigSpec.DoubleValue::get)
                .map(Double::floatValue)
                .collect(Collectors.toList());

        String LayItem;
        String DeathItem;
        String Parent1;
        String Parent2;
        int LayAmount;
        int LayRandomAmount;
        int LayTime;
        int DeathAmount;
        int Tier;
        boolean Enable;
        String Type;



        LayItem = json.has("LayItem") ? JSONUtils.getAsString(json, "LayItem") : "";
        DeathItem = json.has("DeathItem") ? JSONUtils.getAsString(json, "DeathItem") : "";
        Parent1 = json.has("Parent1") ? JSONUtils.getAsString(json, "Parent1") : "";
        Parent2 = json.has("Parent2") ? JSONUtils.getAsString(json, "Parent2") : "";
        LayAmount = json.has("LayAmount") ? JSONUtils.getAsInt(json, "LayAmount") : 0;
        LayRandomAmount = json.has("LayRandomAmount") ? JSONUtils.getAsInt(json, "LayRandomAmount") : 0;
        LayTime = json.has("LayTime") ? JSONUtils.getAsInt(json, "LayTime") : 0;
        DeathAmount = json.has("DeathAmount") ? JSONUtils.getAsInt(json, "DeathAmount") : 0;
        Tier = json.has("Tier") ? JSONUtils.getAsInt(json, "Tier") : 0;
        Enable = !json.has("Enable") || JSONUtils.getAsBoolean(json, "Enable");
        Type = json.has("Type") ? JSONUtils.getAsString(json, "Type") : "base";


        if(!Parent1.equals("") && !Parent2.equals("") && Enable){
                UnorderedPair<String> pair = new UnorderedPair<>(Parent1, Parent2);
                RandomPool<String> pool = Pairings.computeIfAbsent(pair, keyPair -> new RandomPool<>((String)null));
                pool.add(id.getPath(), tiers.get(Tier));
            }

        ChickenType chicken = new ChickenType(id.getPath(),LayItem,LayAmount,LayRandomAmount,LayTime,
                DeathItem,DeathAmount,Enable,Parent1,Parent2,Tier);

        return chicken;
    }

    public static void setTag(String type, ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTagElement("EntityTag");
        tag.putString("type", type);
    }

}

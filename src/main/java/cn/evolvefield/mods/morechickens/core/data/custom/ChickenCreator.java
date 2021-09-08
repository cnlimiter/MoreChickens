package cn.evolvefield.mods.morechickens.core.data.custom;

import cn.evolvefield.mods.morechickens.core.util.main.ChickenType;
import cn.evolvefield.mods.morechickens.core.util.math.RandomPool;
import cn.evolvefield.mods.morechickens.core.util.math.UnorderedPair;
import cn.evolvefield.mods.morechickens.init.ModConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;




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



        LayItem = JsonUtils.getStringOr("LayItem", json, "") ;
        DeathItem = JsonUtils.getStringOr( "DeathItem", json,"") ;
        Parent1 = JsonUtils.getStringOr( "Parent1",json,"") ;
        Parent2 = JsonUtils.getStringOr( "Parent2",json,"") ;
        LayAmount = JsonUtils.getIntOr( "LayAmount",json,0) ;
        LayRandomAmount = JsonUtils.getIntOr( "LayRandomAmount",json,0) ;
        LayTime = JsonUtils.getIntOr( "LayTime",json,0) ;
        DeathAmount = JsonUtils.getIntOr( "DeathAmount",json,0) ;
        Tier = JsonUtils.getIntOr( "Tier",json,0) ;
        Enable = JsonUtils.getBooleanOr( "Enable",json,false);
        Type =  JsonUtils.getStringOr( "Type",json,"base") ;


        if(!Parent1.equals("") && !Parent2.equals("") && Enable){
                UnorderedPair<String> pair = new UnorderedPair<>(Parent1, Parent2);
                RandomPool<String> pool = ChickenType.Pairings.computeIfAbsent(pair, keyPair -> new RandomPool<>((String)null));
                pool.add(id.getPath(), tiers.get(Tier));
            }

        ChickenType chicken = new ChickenType(id.getPath(),LayItem,LayAmount,LayRandomAmount,LayTime,
                DeathItem,DeathAmount,Enable,Parent1,Parent2,Tier);

        return chicken;
    }

}

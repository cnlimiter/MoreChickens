package cn.evolvefield.mods.morechickens.init.handler;

import cn.evolvefield.mods.morechickens.Static;
import cn.evolvefield.mods.morechickens.common.entity.core.ChickenIns;
import cn.evolvefield.mods.morechickens.common.net.SyncChickenInsPacket;
import cn.evolvefield.mods.morechickens.init.ModChickens;
import cn.evolvefield.mods.morechickens.common.entity.core.ChickenInsUtils;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/6 21:49
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChickenInsRegistryHandler {

    private static final ChickenInsRegistryHandler INSTANCE = new ChickenInsRegistryHandler();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    private final Map<ResourceLocation, ChickenIns> chickenInsMap = new LinkedHashMap<>();

    public static ChickenInsRegistryHandler getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    public void onDatapackSync(OnDatapackSyncEvent event) {
        var message = new SyncChickenInsPacket(this.getChickenIns());
        var player = event.getPlayer();

        if (player != null) {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
        } else {
            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), message);
        }
    }

    public void onResourceManagerReload(ResourceManager manager, ICondition.IContext context) {
        this.loadChickens(context);
    }

    public void loadChickens(ICondition.IContext context) {
        var stopwatch = Stopwatch.createStarted();
        var dir = FMLPaths.CONFIGDIR.get().resolve("chickens/instance/").toFile();

        this.writeDefaultSingularityFiles();

        this.chickenInsMap.clear();

        if (!dir.mkdirs() && dir.isDirectory()) {
            this.loadFiles(dir, context);
        }

        stopwatch.stop();

        Static.LOGGER.info("Loaded {} chicken type(s) in {} ms", this.chickenInsMap.size(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    public void writeDefaultSingularityFiles() {
        var dir = FMLPaths.CONFIGDIR.get().resolve("chickens/instance/").toFile();

        if (!dir.exists() && dir.mkdirs()) {
            for (var chickenIns : ModChickens.getDefaults()) {
                var json = ChickenInsUtils.writeToJson(chickenIns);
                FileWriter writer = null;

                try {
                    var file = new File(dir, chickenIns.getId().getPath() + ".json");
                    writer = new FileWriter(file);

                    GSON.toJson(json, writer);
                    writer.close();
                } catch (Exception e) {
                    Static.LOGGER.error("An error occurred while generating default chickens", e);
                } finally {
                    IOUtils.closeQuietly(writer);
                }
            }
        }
    }

    public List<ChickenIns> getChickenIns() {
        return Lists.newArrayList(this.chickenInsMap.values());
    }

    public ChickenIns getChickensById(ResourceLocation id) {
        return this.chickenInsMap.get(id);
    }

    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.chickenInsMap.size());

        this.chickenInsMap.forEach((id, chickenIns) -> {
            chickenIns.write(buffer);
        });
    }

    public List<ChickenIns> readFromBuffer(FriendlyByteBuf buffer) {
        List<ChickenIns> chickenIns = new ArrayList<>();

        int size = buffer.readVarInt();

        for (int i = 0; i < size; i++) {
            var chickenIns1 = ChickenIns.read(buffer);

            chickenIns.add(chickenIns1);
        }

        return chickenIns;
    }

    public void loadSingularities(SyncChickenInsPacket message) {
        var chickenInsMap1 = message.getChickens()
                .stream()
                .collect(Collectors.toMap(ChickenIns::getId, s -> s));

        this.chickenInsMap.clear();
        this.chickenInsMap.putAll(chickenInsMap1);

        Static.LOGGER.info("Loaded {} chickenInsMap1 from the server", chickenInsMap1.size());
    }

    private void loadFiles(File dir, ICondition.IContext context) {
        var files = dir.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".json"));
        if (files == null)
            return;

        for (var file : files) {
            JsonObject json;
            InputStreamReader reader = null;
            ChickenIns chickenIns = null;

            try {
                var parser = new JsonParser();
                reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                var name = file.getName().replace(".json", "");
                json = parser.parse(reader).getAsJsonObject();

                chickenIns = ChickenInsUtils.loadFromJson(new ResourceLocation(Static.MOD_ID, name), json, context);

                reader.close();
            } catch (Exception e) {
                Static.LOGGER.error("An error occurred while loading chickens", e);
            } finally {
                IOUtils.closeQuietly(reader);
            }

            if (chickenIns != null && chickenIns.isEnabled()) {
                var id = chickenIns.getId();

                this.chickenInsMap.put(id, chickenIns);
            }
        }
    }
}

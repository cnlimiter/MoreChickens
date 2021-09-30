package cn.evolvefield.mods.morechickens.integrations.jei.ingredients;


import cn.evolvefield.mods.morechickens.common.data.ChickenUtils;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.data.ChickenData;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class EntityIngredient {

    private static final TranslationTextComponent CREATOR_PREFIX = new TranslationTextComponent("tooltip.resourcefulbees.bee.creator");

    @Nullable
    private final AnimalEntity entity;

    private ChickenData chickenData;


    public EntityIngredient(ChickenData chickenData) {
        this.chickenData =chickenData;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            entity = new BaseChickenEntity(ModEntities.BASE_CHICKEN.get(),mc.level);
            CompoundNBT nbt = new CompoundNBT();
            nbt.putString("Name", chickenData.name);
            entity.readAdditionalSaveData(nbt);
        }else {
            entity = null;
        }
    }

    @Override
    public String toString() {
        return "ChickenIngredient{" +
                "chicken=" + chickenData.name +
                '}';
    }

    public @Nullable Entity getEntity() {
        return entity;
    }

    public ChickenData getChickenData() {
        return chickenData;
    }

    public static Supplier<EntityIngredient> getIngredient(String name) {
        return () -> getTypes().get(name);
    }

    public static Map<String, EntityIngredient> getTypes(){
        Map<String, EntityIngredient> list = new HashMap<>();
        for (Map.Entry<String, ChickenData> entry : ChickenData.Types.entrySet()) {
            String id = entry.getKey();
            list.put(id,new EntityIngredient(entry.getValue()));
        }
        return list;
    }

    public static ChickenData fromNetwork(PacketBuffer buffer) {
        String chickenName = buffer.readUtf();
        return ChickenUtils.getChickenDataByName(chickenName);
    }

    public final void toNetwork(PacketBuffer buffer) {
        buffer.writeUtf(chickenData.name);
    }

    
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("text.chickens.name." + chickenData.name);
    }

    public List<ITextComponent> getTooltip() {
        List<ITextComponent> tooltip = new ArrayList<>();

//        if (entity != null) {
//            if (entity instanceof CustomBeeEntity) {
//                CoreData beeData = ((CustomBeeEntity) entity).getCoreData();
//                if (beeData.getLore().isPresent()) {
//                    String lore = beeData.getLore().get();
//                    String[] loreTooltip = lore.split("\\r?\\n");
//                    for (String s : loreTooltip) {
//                        tooltip.add(new StringTextComponent(s).withStyle(beeData.getLoreColorStyle()));
//                    }
//                }
//                if (beeData.getCreator().isPresent()) {
//                    tooltip.add(CREATOR_PREFIX.copy().append(beeData.getCreator().get()).withStyle(TextFormatting.GRAY));
//                }
//                String desc = I18n.get("tooltip.resourcefulbees.jei.click_bee_info");
//                String[] descTooltip = desc.split("\\r?\\n");
//                for (String s : descTooltip) {
//                    tooltip.add(new StringTextComponent(s).withStyle(TextFormatting.GOLD));
//                }
//            }
//            if (Minecraft.getInstance().options.advancedItemTooltips && entityType.getRegistryName() != null) {
//                tooltip.add(new StringTextComponent(entityType.getRegistryName().toString()).withStyle(TextFormatting.DARK_GRAY));
//            }
//        }
        return tooltip;
    }


}

package cn.evolvefield.mods.morechickens.common.item;

import cn.evolvefield.mods.atomlib.utils.lang.Localizable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/6 21:24
 * Version: 1.0
 */
public class ChickenIns {
    private final ResourceLocation id;
    private final String name;
    private final int tier;
    private final int[] colors;
    private Ingredient layIngredient;
    private final String layTag;
    private final int layCount;
    private final int layTime;
    private final int layRandomCount;
    private boolean enabled = true;
    private final String parent1;
    private final String parent2;



    public ChickenIns(ResourceLocation id, String name, int tier, int[] colors, Ingredient layIngredient,
                       int layCount, int layTime, int layRandomCount, String parent1, String parent2) {
        this.id = id;
        this.name = name;
        this.tier = tier;
        this.colors = colors;
        this.layIngredient = layIngredient;
        this.layTag = null;
        this.layCount = layCount;
        this.layTime = layTime;
        this.layRandomCount = layRandomCount;
        this.parent1 = parent1;
        this.parent2 = parent2;
    }



    public ChickenIns(ResourceLocation id, String name, int tier, int[] colors, String layTag,
                      int layCount, int layTime, int layRandomCount, String parent1, String parent2) {
        this.id = id;
        this.name = name;
        this.tier = tier;
        this.colors = colors;
        this.layIngredient = Ingredient.EMPTY;
        this.layTag = layTag;
        this.layCount = layCount;
        this.layTime = layTime;
        this.layRandomCount = layRandomCount;
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public static ChickenIns read(FriendlyByteBuf buffer) {
        var id = buffer.readResourceLocation();
        var name = buffer.readUtf();
        var tier = buffer.readVarInt();
        var colors = buffer.readVarIntArray();
        var isLayTagIngredient = buffer.readBoolean();

        String layTag = null;

        var layIngredient = Ingredient.EMPTY;

        if (isLayTagIngredient) {
            layTag = buffer.readUtf();
        } else {
            layIngredient = Ingredient.fromNetwork(buffer);
        }


        var layIngredientCount = buffer.readVarInt();

        var parent1 = buffer.readUtf();
        var parent2 = buffer.readUtf();

        var layTime = buffer.readVarInt();
        var layRandomCount = buffer.readVarInt();


        ChickenIns singularity;
        if (isLayTagIngredient) {
            singularity = new ChickenIns(id, name, tier, colors, layTag, layTime, layRandomCount, layIngredientCount, parent1, parent2);
        }
        else {
            singularity = new ChickenIns(id, name, tier, colors, layIngredient, layTime, layRandomCount, layIngredientCount, parent1, parent2);
        }

        singularity.enabled = buffer.readBoolean();

        return singularity;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.id);
        buffer.writeUtf(this.name);
        buffer.writeInt(this.tier);
        buffer.writeVarIntArray(this.colors);
        buffer.writeBoolean(this.layTag != null);

        if (this.layTag != null) {
            buffer.writeUtf(this.layTag);
        } else {
            this.layIngredient.toNetwork(buffer);
        }


        buffer.writeVarInt(this.getLayCount());

        buffer.writeUtf(this.parent1);
        buffer.writeUtf(this.parent2);

        buffer.writeBoolean(this.enabled);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getTier() {
        return tier;
    }

    public int getOverlayColor() {
        return this.colors[0];
    }

    public int getUnderlayColor() {
        return this.colors[1];
    }

    public Ingredient getLayIngredient() {
        if (this.layTag != null && this.layIngredient == Ingredient.EMPTY) {
            var tag = ItemTags.create(new ResourceLocation(this.layTag));
            this.layIngredient = Ingredient.of(tag);
        }

        return this.layIngredient;
    }

    public int getLayCount() {
        if (this.layCount == -1) {
            return 1;
        }
        return this.layCount;
    }

    public int getLayTime() {
        if (this.layTime == -1) {
            return 1;
        }
        return this.layTime;
    }

    public int getLayRandomCount() {
        if (this.layRandomCount == -1) {
            return 1;
        }
        return this.layRandomCount;
    }

    public String getLayTag() {
        return layTag;
    }

    public String getParent1() {
        return parent1;
    }

    public String getParent2() {
        return parent2;
    }

    public Component getDisplayName() {
        return Localizable.of(this.name).build();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


}

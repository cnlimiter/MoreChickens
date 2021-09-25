package cn.evolvefield.mods.morechickens.common.recipe;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.util.main.ChickenHelper;
import cn.evolvefield.mods.morechickens.common.util.main.ChickenType;
import cn.evolvefield.mods.morechickens.init.ModRecipeTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class RoostRecipe extends TagOutputRecipe implements IRecipe<IInventory> {
    public static final IRecipeType<RoostRecipe> ROOST = IRecipeType.register(MoreChickens.MODID + ":roost_recipe");
    public final ResourceLocation id;
    public final Lazy<ChickenType> ingredient;

    public RoostRecipe(ResourceLocation id, Lazy<ChickenType> ingredient, Map<Ingredient, IntArrayNBT> itemOutput) {
        super(itemOutput);
        this.id = id;
        this.ingredient = ingredient;
    }

    @Override
    public String toString() {
        return "RoostRecipe{" +
                "id=" + id +
                ", chicken=" + ingredient.get().name +
                '}';
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if (inv instanceof ChickenHelper.IdentifierInventory && ingredient.get() != null) {
            String chickenName = ((ChickenHelper.IdentifierInventory) inv).getIdentifier();
            return chickenName.equals(ingredient.get().name);
        }
        if (ingredient.get() == null) {
            MoreChickens.LOGGER.info(id + " is null");
        }

        return false;
    }



    @Nonnull
    @Override
    public ItemStack assemble(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.ROOST_RECIPE.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return ROOST;
    }

    public static class Serializer<T extends RoostRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final IRecipeFactory<T> factory;

        public Serializer(Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            String chickenName = JSONUtils.getAsString(json, "ingredient");

            Lazy<ChickenType> beeIngredient = Lazy.of(ChickenType.getIngredient(chickenName));

            Map<Ingredient, IntArrayNBT> itemOutputs = new LinkedHashMap<>();

            JsonArray jsonArray = JSONUtils.getAsJsonArray(json, "results");
            jsonArray.forEach(jsonElement -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                String ingredientKey = jsonObject.has("item") ? "item" : "layItem";

                Ingredient produce;
                if (JSONUtils.isArrayNode(jsonObject, ingredientKey)) {
                    produce = Ingredient.fromJson(JSONUtils.getAsJsonArray(jsonObject, ingredientKey));
                } else {
                    produce = Ingredient.fromJson(JSONUtils.getAsJsonObject(jsonObject, ingredientKey));
                }

                int min = JSONUtils.getAsInt(jsonObject, "min", 1);
                int max = JSONUtils.getAsInt(jsonObject, "max", 1);
                int outputChance = JSONUtils.getAsInt(jsonObject, "chance", 100);
                IntArrayNBT nbt = new IntArrayNBT(new int[]{min, max, outputChance});

                itemOutputs.put(produce, nbt);
            });

            return this.factory.create(id, beeIngredient, itemOutputs);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                ChickenType ingredient = ChickenType.fromNetwork(buffer);
                Map<Ingredient, IntArrayNBT> itemOutput = new LinkedHashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> itemOutput.put(Ingredient.fromNetwork(buffer), new IntArrayNBT(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
                );

                return this.factory.create(id, Lazy.of(() -> ingredient), itemOutput);
            } catch (Exception e) {
                MoreChickens.LOGGER.error("Error reading beehive produce recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                if (recipe.ingredient.get() != null) {
                    recipe.ingredient.get().toNetwork(buffer);
                } else {
                    throw new RuntimeException("Bee produce recipe ingredient missing " + recipe.getId() + " - " + recipe.ingredient);
                }
                buffer.writeInt(recipe.itemOutput.size());

                recipe.itemOutput.forEach((key, value) -> {
                    key.toNetwork(buffer);
                    buffer.writeInt(value.get(0).getAsInt());
                    buffer.writeInt(value.get(1).getAsInt());
                    buffer.writeInt(value.get(2).getAsInt());
                });
            } catch (Exception e) {
                MoreChickens.LOGGER.error("Error writing beehive produce recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends RoostRecipe>
        {
            T create(ResourceLocation id, Lazy<ChickenType> input, Map<Ingredient, IntArrayNBT> itemOutput);
        }
    }
}

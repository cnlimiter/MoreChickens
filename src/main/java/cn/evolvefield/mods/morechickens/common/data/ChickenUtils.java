package cn.evolvefield.mods.morechickens.common.data;

import cn.evolvefield.mods.morechickens.common.util.math.RandomCollection;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;

import static cn.evolvefield.mods.morechickens.common.data.ChickenRegistry.FAMILY_TREE;

public class ChickenUtils {

    public static ChickenData getChickenDataByName(String name){
        return ChickenRegistry.Types.get(name);
    }

    public static int calcNewEggLayTime(Random r, ChickenData type, int growth) {
        if (type.layTime == 0) return 0;
        int egg = r.nextInt(type.layTime) + type.layTime;
        return (int) Math.max(1.0f, (egg * (10.f - growth + 1.f)) / 10.f);
    }

    public static int calcDropQuantity(int gain) {
        if (gain < 5) return 1;         // between 1-4
        if (gain < 10) return 2;        // between 5-9
        return 3;                       // 10
    }

    public static Item getItem(String id, Random rand){
        Item item;
        if("#@".contains(id.substring(0, 1))){
            //if(id.contains("#")){
            ITag<Item> tag = ItemTags.getAllTags().getTag(new ResourceLocation(id.substring(1)));
            if(tag == null)
                return null;
            List<Item> items = tag.getValues();
            if(items.isEmpty())
                return null;
            if(id.charAt(0) == '#') // First item
                item = items.get(0);
            else // Random item
                item = tag.getRandomElement(rand);
        }
        else
            item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        return item;
    }

    public static List<ItemStack> calcDrops(int gain, ChickenData type, int fortune, Random random) {
        // return a list of item drops
        // done like this to avoid making stacks of non-stackable items
        List<ItemStack> lst = NonNullList.create();

        // TODO: if no drop item then try and find a loot table?
        if (!type.layItem.isEmpty()) {
            ItemStack itemStack = new ItemStack(getItem(type.layItem, random));
            if (! itemStack.isEmpty()) {
                int dropQuantity = calcDropQuantity(gain) + fortune;
                if (itemStack.isStackable()) {
                    itemStack.setCount(dropQuantity);
                    lst.add(itemStack);
                }
                else {
                    for (int a = 0; a < dropQuantity; a++) {
                        ItemStack itm = itemStack.copy();
                        lst.add(itm);
                    }
                }
            }
        }

        if (random.nextInt(2) == 0) lst.add(
                new ItemStack(Items.EGG));
        if (random.nextInt(2) == 0) lst.add(
                new ItemStack(Items.FEATHER));

        return lst;
    }

    public static Pair<String, String> sortParents(String parent1, String parent2) {
        return parent1.compareTo(parent2) > 0 ? Pair.of(parent1, parent2) : Pair.of(parent2, parent1);
    }

    public static void addBreedPairToFamilyTree(ChickenData data) {
        FAMILY_TREE.computeIfAbsent(data.getParents(), k -> new RandomCollection<>()).add(data.getWeight(), data);
    }
}

package cn.evolvefield.mods.morechickens.common.block.utils;

import cn.evolvefield.mods.morechickens.init.ModConfig;
import com.google.common.collect.Lists;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

public enum BaitType implements IStringSerializable {

    OAK_CHICKEN(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.WHEAT_SEEDS), "oak", ModConfig.COMMON.oakChance::get),
    SAND_CHICKEN(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.WHEAT_SEEDS), "sand", ModConfig.COMMON.sandChance::get),
    FLINT_CHICKEN(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.WHEAT_SEEDS), "flint", ModConfig.COMMON.flintChance::get),
    QUARTZ_CHICKEN(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.WHEAT_SEEDS), "quartz", ModConfig.COMMON.quartzChance::get),
    SOUL_SAND_CHICKEN(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.WHEAT_SEEDS), "soul_sand", ModConfig.COMMON.soulSandChance::get);

    private final ItemStack displayItemFirst;
    private final ItemStack displayItemSecond;
    private final String entityType;
    private final Supplier<Double> chanceSupplier;

    private List<BaitEnvironmentCondition> environmentConditions;

    BaitType(ItemStack displayItemFirst, ItemStack displayItemSecond, String entityType, Supplier<Double> chanceSupplier) {
        this.displayItemFirst = displayItemFirst;
        this.displayItemSecond = displayItemSecond;
        this.entityType = entityType;
        this.chanceSupplier = chanceSupplier;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ENGLISH);
    }



    @Nullable
    public void createEntity(World world,double x,double y,double z) {
        world.getServer().getCommands().performCommand(new CommandSource(ICommandSource.NULL, new Vector3d(x,y,z), Vector2f.ZERO,(ServerWorld) world,4, "",
                new StringTextComponent(""), Objects.requireNonNull(world.getServer()), null),"summon chickens:base_chicken ~ ~ ~ {Breed:'"+ entityType +"'}");

    }

    public ItemStack getDisplayItemFirst() {
        return displayItemFirst;
    }

    public ItemStack getDisplayItemSecond() {
        return displayItemSecond;
    }

    public float getChance() {
        return chanceSupplier.get().floatValue();
    }

    public Collection<BaitEnvironmentCondition> getEnvironmentConditions() {
        if (environmentConditions == null) {
            if (this == OAK_CHICKEN) {
                environmentConditions = Lists.newArrayList(
                        //new BaitBlockTagCondition(new ResourceLocation("minecraft", "logs")),
                        new BaitBlockStateCondition(Blocks.GRASS.defaultBlockState()),
                        new BaitBlockStateCondition(Blocks.TALL_GRASS.defaultBlockState()),
                        new BaitFluidCondition(Fluids.WATER)
                        //new BaitBlockTagCondition(new ResourceLocation("minecraft", "saplings"))
                );
            } else if (this == SAND_CHICKEN) {
                environmentConditions = Lists.newArrayList(
                        new BaitBlockStateCondition(Blocks.SAND.defaultBlockState())
                );
            } else if (this == FLINT_CHICKEN) {
                environmentConditions = Lists.newArrayList(
                        new BaitFluidCondition(Fluids.WATER),
                        new BaitBlockStateCondition(Blocks.GRAVEL.defaultBlockState())
                );
            }
            else if (this == QUARTZ_CHICKEN) {
                environmentConditions = Lists.newArrayList(
                        new BaitBlockStateCondition(Blocks.NETHERRACK.defaultBlockState())
                );
            } else {
                environmentConditions = Lists.newArrayList(
                        new BaitBlockStateCondition(Blocks.NETHERRACK.defaultBlockState()),
                        new BaitBlockStateCondition(Blocks.LAVA.defaultBlockState())
                        );
            }
        }

        return environmentConditions;
    }


}

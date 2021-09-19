package cn.evolvefield.mods.morechickens.common.item;


import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmllegacy.RegistryObject;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModSpawnEgg extends SpawnEggItem {

    private static final List<ModSpawnEgg> CHICKEN = new ArrayList<>();
    private final Lazy<? extends EntityType<?>> entityTypeSupplier;

    public ModSpawnEgg(RegistryObject<? extends EntityType<?>> entityType, int fg, int bg, Properties properties) {
        super(null, fg, bg, properties);
        this.entityTypeSupplier = Lazy.of(entityType);
        CHICKEN.add(this);
    }

    public static void registerMobs(){
        try {
            Map<EntityType<?>, SpawnEggItem> EGGS = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "f_43201_");
            DefaultDispenseItemBehavior behavior = new DefaultDispenseItemBehavior(){
                @Override
                protected ItemStack execute(BlockSource source, ItemStack stack) {
                    Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
                    EntityType<?> type = ((ModSpawnEgg)stack.getItem()).getType(stack.getTag());
                    type.spawn(source.getLevel(), stack, null, source.getPos().relative(dir), MobSpawnType.DISPENSER, dir != Direction.UP, false);
                    stack.shrink(1);
                    return stack;
                }
            };
            for(ModSpawnEgg egg : CHICKEN){EGGS.put(egg.getType(null), egg);
                DispenserBlock.registerBehavior(egg, behavior);
            }
            CHICKEN.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EntityType<?> getType(CompoundTag nbt){
        return entityTypeSupplier.get();
    }
}

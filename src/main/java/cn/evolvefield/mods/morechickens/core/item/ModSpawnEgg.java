package cn.evolvefield.mods.morechickens.core.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModSpawnEgg extends SpawnEggItem {

    private static final List<ModSpawnEgg> QUEUE = new ArrayList<>();
    private final Lazy<? extends EntityType<?>> entityTypeSupplier;

    public ModSpawnEgg( RegistryObject<? extends EntityType<?>> entityType, int fg, int bg, Properties properties) {
        super(null, fg, bg, properties);
        this.entityTypeSupplier = Lazy.of(entityType);
        QUEUE.add(this);
    }

    public static void registerMobs(){
        try {
            Map<EntityType<?>, SpawnEggItem> EGGS = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "field_195987_b");
            DefaultDispenseItemBehavior behavior = new DefaultDispenseItemBehavior(){
                @Override
                protected ItemStack execute(IBlockSource source, ItemStack stack) {
                    Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
                    EntityType<?> type = ((ModSpawnEgg)stack.getItem()).getType(stack.getTag());
                    type.spawn(source.getLevel(), stack, null, source.getPos().relative(dir), SpawnReason.DISPENSER, dir != Direction.UP, false);
                    stack.shrink(1);
                    return stack;
                }
            };
            for(ModSpawnEgg egg : QUEUE){EGGS.put(egg.getType(null), egg);
                DispenserBlock.registerBehavior(egg, behavior);
            }
            QUEUE.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EntityType<?> getType(CompoundNBT nbt){
        return entityTypeSupplier.get();
    }
}

package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.atomlib.init.iface.IColored;
import cn.evolvefield.mods.atomlib.utils.lang.Localizable;
import cn.evolvefield.mods.morechickens.Static;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.init.registry.ModEntities;
import cn.evolvefield.mods.morechickens.init.ModTab;
import cn.evolvefield.mods.morechickens.init.ModTooltips;
import cn.evolvefield.mods.morechickens.init.handler.ChickenInsRegistryHandler;
import cn.evolvefield.mods.morechickens.common.entity.core.ChickenInsUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChickenItem extends Item implements IColored {

    public ChickenItem() {
        super(new Properties().craftRemainder(Items.BUCKET)
                .stacksTo(1)
                .tab(ModTab.INSTANCE)
        );
        setRegistryName("chicken");
    }


    @Override
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            ChickenInsRegistryHandler.getInstance().getChickenIns().forEach(singularity -> {
                items.add(ChickenInsUtils.getItemForChickenIns(singularity));
            });
        }
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        var chickenIns = ChickenInsUtils.getChickenIns(stack);

        if (chickenIns == null) {
            return Localizable.of(this.getDescriptionId(stack)).args("NULL").build();
        }

        return Localizable.of(this.getDescriptionId(stack)).args(chickenIns.getDisplayName()).build();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        var chickenIns = ChickenInsUtils.getChickenIns(stack);

        if (chickenIns != null) {
            var modid = chickenIns.getId().getNamespace();

            if (!modid.equals(Static.MOD_ID))
                tooltip.add(ModTooltips.getAddedByTooltip(modid));

            if (flag.isAdvanced())
                tooltip.add(ModTooltips.CHICKEN_ID.args(chickenIns.getId()).color(ChatFormatting.DARK_GRAY).build());
        }
    }

    @Override
    public int getColor(int i, ItemStack stack) {
        var chickenIns = ChickenInsUtils.getChickenIns(stack);

        if (chickenIns == null)
            return -1;

        return i == 0 ? chickenIns.getUnderlayColor() : i == 1 ? chickenIns.getOverlayColor() : -1;
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        InteractionHand hand = context.getHand();
        BlockPos pos = context.getClickedPos().above();
        ItemStack chickenItem = player.getItemInHand(hand);
        String typeTag = chickenItem.getOrCreateTag().getString("Type");
        CompoundTag chickenTag = chickenItem.getTagElement("ChickenData");

        if (!world.isClientSide) {
            BlockEntity tileEntity = world.getBlockEntity(pos);

            if(typeTag.equals("vanilla")){
                Chicken chicken =  new Chicken(EntityType.CHICKEN,world);
                chicken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                chicken.finalizeSpawn(world.getServer().overworld(),world.getCurrentDifficultyAt(pos), MobSpawnType.SPAWN_EGG,null,null);
                world.getServer().overworld().addFreshEntity(chicken);
                chickenItem.shrink(1);
            }
            else if(typeTag.equals("modded")){
                CompoundTag nbt = chickenItem.getTag();
                if (nbt == null)
                    return InteractionResult.PASS;
                BaseChickenEntity chicken = new BaseChickenEntity(ModEntities.BASE_CHICKEN.get(),world);
                chicken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                chicken.finalizeSpawn(world.getServer().overworld(),world.getCurrentDifficultyAt(pos), MobSpawnType.SPAWN_EGG,null,null);
                chicken.readAdditionalSaveData(nbt);
                world.getServer().overworld().addFreshEntity(chicken);

                chickenItem.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }




}

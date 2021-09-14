package cn.evolvefield.mods.morechickens.common.item;


import cn.evolvefield.mods.morechickens.common.data.DataChicken;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.tile.RoostTileEntity;
import cn.evolvefield.mods.morechickens.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ChickenItem extends Item {


    public ChickenItem(Properties properties) {
        super(properties
            .stacksTo(16)
        );
    }


    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {

    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public ITextComponent getName(ItemStack stack) {
        World world = Minecraft.getInstance().level;
        if (world == null) {
            return super.getName(stack);
        } else {

            String name = stack.getOrCreateTag().getString("Name");
            return  new TranslationTextComponent("text.chickens.name."+ name);
        }
    }


    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getLevel();
        Hand hand = context.getHand();
        BlockPos pos = context.getClickedPos().above();
        ItemStack chickenItem = player.getItemInHand(hand);
        String typeTag = chickenItem.getOrCreateTag().getString("Type");
        CompoundNBT chickenTag = chickenItem.getTagElement("Chicken");

        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);

            if (tileEntity instanceof RoostTileEntity) {
                putChickenIn(player.getItemInHand(hand), (RoostTileEntity) tileEntity);
            } else {
                if(typeTag.equals("vanilla")){
                    ChickenEntity chicken =  new ChickenEntity(EntityType.CHICKEN,world);
                    chicken.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    chicken.finalizeSpawn(world.getServer().overworld(),world.getCurrentDifficultyAt(pos), null,null,null);
                    world.getServer().overworld().addFreshEntity(chicken);
                    chickenItem.shrink(1);
                }
                else if(typeTag.equals("modded")){

                    String name = chickenItem.getOrCreateTag().getString("Name");
                    world.getServer().getCommands().performCommand(new CommandSource(ICommandSource.NULL, new Vector3d(pos.getX() + 0.5,pos.getY(),pos.getZ() + 0.5), Vector2f.ZERO,(ServerWorld) world,4, "",
                            new StringTextComponent(""), Objects.requireNonNull(world.getServer()), null),"summon chickens:base_chicken ~ ~ ~ {Name:'"+ name +"'}");
                    chickenItem.shrink(1);
                }
            }
        }

        return ActionResultType.SUCCESS;
    }




    private void putChickenIn(ItemStack stack, RoostTileEntity tileEntity) {
        tileEntity.putChickenIn(stack);
    }


    private void spawnChicken(ItemStack stack, World worldIn, BlockPos pos) {
        DataChicken chickenData = DataChicken.getDataFromStack(stack);
        if (chickenData == null) return;
        chickenData.spawnEntity(worldIn, pos);
        stack.shrink(1);
    }


}

package cn.evolvefield.mods.morechickens.core.tile;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.core.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.core.util.main.VirtualChicken;
import cn.evolvefield.mods.morechickens.init.*;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

public class NestTileEntity extends BlockEntity implements WorldlyContainer, BlockEntityTicker {



    private final Stack<VirtualChicken> chickens;
    //private NonNullList<ItemStack> inventory  ;
    private final Queue<ItemStack> inventory;
    private final Random rand;


    public NestTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.CHICKEN_NEST,pos,state);
        chickens = new Stack<>();
        //inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        inventory = new ArrayDeque<>();
        rand = new Random();
    }

    public void putChicken(CompoundTag nbt){
        chickens.add(new VirtualChicken(nbt));
    }

    public CompoundTag getChicken(){
        if(chickens.isEmpty())
            return null;
        VirtualChicken head = chickens.pop();
        return head.writeToTag();
    }

    public ListTag getChickens(){
        ListTag nbt = new ListTag();
        for(VirtualChicken chicken : chickens){
            nbt.add(chicken.writeToTag());
        }
        return nbt;
    }


    @Override
    public void tick(Level level, BlockPos pos, BlockState state, BlockEntity entity) {
        for(VirtualChicken chicken : chickens){
            chicken.layTimer -= ModConfig.COMMON.nestTickRate.get();
            if(chicken.layTimer <= 0){
                chicken.resetTimer(rand);
                ItemStack nextThing = chicken.breed.getLoot(rand, chicken.gene);
                inventory.add(nextThing);
            }
        }
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        int count = getContainerSize();
        int[] itemSlots = new int[count];
        for (int i = 0; i < count; i++) {
            itemSlots[i] = i;
        }
        return itemSlots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int p_180462_1_, ItemStack stack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int p_180461_1_, ItemStack itemStack, Direction direction) {
        return !inventory.isEmpty();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        if(inventory.isEmpty())
            return ItemStack.EMPTY;
        return inventory.peek();
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack head = inventory.peek();
        if(head != null) {
            ItemStack taken = head.split(count);
            if (head.isEmpty())
                inventory.poll();
            return taken;
        }
        else
            return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if(inventory.isEmpty())
            return ItemStack.EMPTY;
        return inventory.poll();
    }

    @Override
    public void setItem(int index, ItemStack itemStack) {

    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        ListTag nbt = new ListTag();
        for(VirtualChicken chicken : chickens){
            nbt.add(chicken.writeToTag());
        }
        compound.put("Chickens", nbt);
        return compound;
    }


    @Override
    public void load(CompoundTag nbt) {
        super.load( nbt);
        ListTag listNBT = nbt.getList("Chickens", 10);
        for(int i = 0; i < listNBT.size(); i++){
            CompoundTag chickenTag = listNBT.getCompound(i);
            VirtualChicken chicken = new VirtualChicken(chickenTag);
            chickens.add(chicken);
        }
    }

    public void spawnChickens(Level world){
        for(VirtualChicken chicken : chickens){
            CompoundTag nbt = chicken.writeToTag();
            BaseChickenEntity entity = (BaseChickenEntity) ModDefaultEntities.BASE_CHICKEN.get().spawn((ServerLevel)world, null, null, getBlockPos(), MobSpawnType.TRIGGERED, true, false);
            if(entity != null)
                entity.load(nbt);
        }
    }

    public void printChickens(Player player){
        Map<String, Integer> breeds = new HashMap<>();
        for(VirtualChicken chicken : chickens){
            breeds.put(chicken.breed.name, breeds.getOrDefault(chicken.breed.name, 0) + 1);
        }
        for(Map.Entry<String, Integer> breed : breeds.entrySet()){
            player.sendMessage(new TranslatableComponent("text." + MoreChickens.MODID + ".multiplier",
                            new TranslatableComponent("text." + MoreChickens.MODID + ".breed." + breed.getKey()),
                            breed.getValue()),
                    Util.NIL_UUID);
        }
    }

    public int numChickens(){
        return chickens.size();
    }
}


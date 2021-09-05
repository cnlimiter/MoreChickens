package cn.evolvefield.mods.morechickens.common.tile;

import cn.evolvefield.mods.morechickens.MoreChickens;
import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import cn.evolvefield.mods.morechickens.common.util.main.VirtualChicken;
import cn.evolvefield.mods.morechickens.init.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;

public class NestTileEntity extends TileEntity implements ITickableTileEntity, ISidedInventory {



    private final Stack<VirtualChicken> chickens;
    //private NonNullList<ItemStack> inventory  ;
    private final Queue<ItemStack> inventory;
    private final Random rand;


    public NestTileEntity() {
        super(ModTileEntities.CHICKEN_NEST);
        chickens = new Stack<>();
        //inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        inventory = new ArrayDeque<>();
        rand = new Random();
    }

    public void putChicken(CompoundNBT nbt){
        chickens.add(new VirtualChicken(nbt));
    }

    public CompoundNBT getChicken(){
        if(chickens.isEmpty())
            return null;
        VirtualChicken head = chickens.pop();
        return head.writeToTag();
    }

    public ListNBT getChickens(){
        ListNBT nbt = new ListNBT();
        for(VirtualChicken chicken : chickens){
            nbt.add(chicken.writeToTag());
        }
        return nbt;
    }


    @Override
    public void tick() {
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
    public boolean stillValid(PlayerEntity player) {
        return false;
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        ListNBT nbt = new ListNBT();
        for(VirtualChicken chicken : chickens){
            nbt.add(chicken.writeToTag());
        }
        compound.put("Chickens", nbt);
        return compound;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        ListNBT listNBT = nbt.getList("Chickens", 10);
        for(int i = 0; i < listNBT.size(); i++){
            CompoundNBT chickenTag = listNBT.getCompound(i);
            VirtualChicken chicken = new VirtualChicken(chickenTag);
            chickens.add(chicken);
        }
    }

    public void spawnChickens(World world){
        for(VirtualChicken chicken : chickens){
            CompoundNBT nbt = chicken.writeToTag();
            BaseChickenEntity entity = (BaseChickenEntity) ModEntities.BASE_CHICKEN.get().spawn((ServerWorld)world, null, null, getBlockPos(), SpawnReason.TRIGGERED, true, false);
            if(entity != null)
                entity.load(nbt);
        }
    }

    public void printChickens(PlayerEntity player){
        Map<String, Integer> breeds = new HashMap<>();
        for(VirtualChicken chicken : chickens){
            breeds.put(chicken.breed.name, breeds.getOrDefault(chicken.breed.name, 0) + 1);
        }
        for(Map.Entry<String, Integer> breed : breeds.entrySet()){
            player.sendMessage(new TranslationTextComponent("text." + MoreChickens.MODID + ".multiplier",
                            new TranslationTextComponent("text." + MoreChickens.MODID + ".breed." + breed.getKey()),
                            breed.getValue()),
                    Util.NIL_UUID);
        }
    }

    public int numChickens(){
        return chickens.size();
    }
}


package cn.evolvefield.mods.morechickens.common.util.main;

import cn.evolvefield.mods.morechickens.common.entity.BaseChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ChickenHelper {
    public static class IdentifierInventory implements IInventory
    {
        private List<String> identifiers = new ArrayList<>();

        public IdentifierInventory(String identifier) {
            this.identifiers.add(identifier);
        }

        public IdentifierInventory(BaseChickenEntity bee1, BaseChickenEntity bee2) {
            String identifier1 ;
            identifier1 =  bee1.getChickenName();

            String identifier2 ;
            identifier2 = bee2.getChickenName();

            this.identifiers.add(identifier1);
            this.identifiers.add(identifier2);
        }

        public IdentifierInventory(BaseChickenEntity bee1, String identifier2) {
            String identifier1 ;
            identifier1 = bee1.getChickenName();

            this.identifiers.add(identifier1);
            this.identifiers.add(identifier2);
        }

        public IdentifierInventory(String identifier1, String identifier2) {
            this.identifiers.add(identifier1);
            this.identifiers.add(identifier2);
        }

        public String getIdentifier() {
            return getIdentifier(0);
        }

        public String getIdentifier(int index) {
            return this.identifiers.get(index);
        }

        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return identifiers.isEmpty();
        }

        @Nonnull
        @Override
        public ItemStack getItem(int i) {
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack removeItem(int i, int i1) {
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack removeItemNoUpdate(int i) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int i, @Nonnull ItemStack itemStack) {

        }

        @Override
        public void setChanged() {

        }

        @Override
        public boolean stillValid(@Nonnull PlayerEntity playerEntity) {
            return false;
        }

        @Override
        public void clearContent() {
            this.identifiers.clear();
        }
    }
}

package mod.ckenja.relocate.foundation.item;

import net.minecraft.world.item.ItemStack;

import java.util.Objects;

import static net.minecraftforge.items.ItemHandlerHelper.canItemStacksStack;

public class ItemStackWrapper {
    private ItemStack itemStack;
    public ItemStackWrapper(ItemStack stack) {
        this.itemStack = stack;
    }
    @Override
    public int hashCode(){
        return Objects.hash(itemStack.getTag(),itemStack.getItem());
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ItemStack other = (ItemStack) obj;
        return canItemStacksStack(other, itemStack);
    }
    public ItemStack getItemStack(){
        return this.itemStack;
    }
}

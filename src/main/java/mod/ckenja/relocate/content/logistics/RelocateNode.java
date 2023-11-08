package mod.ckenja.relocate.content.logistics;

import mod.ckenja.relocate.foundation.item.ItemStackWrapper;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class RelocateNode {
    public HashMap<ItemStackWrapper, Integer> itemCountList;

    public RelocateNode() {
        itemCountList = new HashMap<>();
    }

    public void removeCountedItemStack(ItemStack itemStack) {
        int i = itemCountList.get(itemStack) - itemStack.getCount();
        if (i < 0) {
            itemCountList.remove(itemStack);
        } else {
            itemCountList.put(new ItemStackWrapper(itemStack), i);
        }
    }

}

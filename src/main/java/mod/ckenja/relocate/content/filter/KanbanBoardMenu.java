package mod.ckenja.relocate.content.filter;

import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import mod.ckenja.relocate.Relocate;
import mod.ckenja.relocate.content.logistics.RelocateNode;
import mod.ckenja.relocate.foundation.item.ItemStackWrapper;
import mod.ckenja.relocate.registry.RelocateMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static mod.ckenja.relocate.content.filter.KanbanBoardItem.getKanbanBoardID;

public class KanbanBoardMenu extends AbstractFilterMenu {
    private UUID uuid;
    public KanbanBoardMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }
    public KanbanBoardMenu(MenuType<?> type, int id, Inventory inv, ItemStack stack) {
        super(type, id, inv, stack);
    }

    public static KanbanBoardMenu create(int pContainerId, Inventory inv, ItemStack heldItem) {
        return new KanbanBoardMenu(RelocateMenuTypes.KANBAN_BOARD.get(), pContainerId, inv, heldItem);
    }

    @Override
    protected int getPlayerInventoryXOffset() {
        return 38;
    }

    @Override
    protected int getPlayerInventoryYOffset() {
        return 121;
    }

    @Override
    protected void addFilterSlots() {
        int x = 23;
        int y = 22;
        for (int row = 0; row < 2; ++row)
            for (int col = 0; col < 9; ++col)
                this.addSlot(new SlotItemHandler(ghostInventory, col + row * 9, x + col * 18, y + row * 18));
    }

    @Override
    protected void initAndReadInventory(ItemStack filterItem) {
        super.initAndReadInventory(filterItem);
        uuid = getKanbanBoardID(filterItem);
    }
    @Override
    protected ItemStackHandler createGhostInventory() {
        ItemStackHandler handler = new ItemStackHandler(18);
        //HashMap<ItemStackWrapper, Integer> itemCountList = Relocate.RELOCATE_SAVED_DATA.getNode(uuid).itemCountList;
        int i = 0;
        /*for(Map.Entry<ItemStackWrapper, Integer> entry : itemCountList.entrySet()) {
            i++;
            handler.setStackInSlot(i, entry.getKey().getItemStack());
        }*/
        return handler;
    }
    protected void saveData(ItemStack itemStack){
        RelocateNode relocateNode = new RelocateNode();
        relocateNode.itemCountList.put(new ItemStackWrapper(new ItemStack(Items.APPLE)),130);
        //Relocate.RELOCATE_SAVED_DATA.addNode(new UUID(0L, 0L),relocateNode);
    }
}

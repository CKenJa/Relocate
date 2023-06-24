package mod.ckenja.relocate.content.filter;

import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import mod.ckenja.relocate.registry.RelocateMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class KanbanBoardMenu extends AbstractFilterMenu {
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
    protected ItemStackHandler createGhostInventory() {
        return new ItemStackHandler(18);
    }
}

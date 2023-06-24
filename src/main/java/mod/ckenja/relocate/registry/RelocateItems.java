package mod.ckenja.relocate.registry;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import mod.ckenja.relocate.Relocate;
import mod.ckenja.relocate.content.filter.KanbanBoardItem;

public class RelocateItems {
    private static final CreateRegistrate REGISTRATE = Relocate.registrate();
    public static final ItemEntry<KanbanBoardItem> KANBAN_BOARD = REGISTRATE.item("kanban_board",KanbanBoardItem::new)
            .properties(properties -> properties.stacksTo(1))
            .lang("Kanban board")
            .register();
    public static void register(){
        REGISTRATE.creativeModeTab(()-> AllCreativeModeTabs.BASE_CREATIVE_TAB, Relocate.NAME);
    }
}

package mod.ckenja.relocate.registry;

import com.simibubi.create.AllCreativeModeTabs;
import com.tterrag.registrate.util.entry.ItemEntry;
import mod.ckenja.relocate.content.filter.KanbanBoardItem;

import static mod.ckenja.relocate.Relocate.REGISTRATE;

public class RelocateItems {
    public static final ItemEntry<KanbanBoardItem> KANBAN_BOARD = REGISTRATE.item("kanban_board",KanbanBoardItem::new)
            .properties(properties -> properties.stacksTo(64))
            .lang("Kanban board")
            .register();
    public static void register(){
        REGISTRATE.creativeModeTab(()-> AllCreativeModeTabs.BASE_CREATIVE_TAB);
    }
}

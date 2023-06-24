package mod.ckenja.relocate.registry;

import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import mod.ckenja.relocate.Relocate;
import mod.ckenja.relocate.content.filter.KanbanBoardMenu;
import mod.ckenja.relocate.content.filter.KanbanBoardScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class RelocateMenuTypes {
    public static final MenuEntry<KanbanBoardMenu> KANBAN_BOARD =
            register("kanban_board_menu", KanbanBoardMenu::new, () -> KanbanBoardScreen::new);
    private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
            String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return Relocate.registrate()
                .menu(name, factory, screenFactory)
                .register();
    }

    public static void register() {}
}

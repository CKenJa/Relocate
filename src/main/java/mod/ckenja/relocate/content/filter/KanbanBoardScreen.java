package mod.ckenja.relocate.content.filter;

import com.simibubi.create.content.logistics.filter.AbstractFilterScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class KanbanBoardScreen extends AbstractFilterScreen<KanbanBoardMenu> {
    public KanbanBoardScreen(KanbanBoardMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, AllGuiTextures.FILTER);
    }

    @Override
    protected boolean isButtonEnabled(IconButton button) {
        return false;
    }

    @Override
    protected boolean isIndicatorOn(Indicator indicator) {
        return false;
    }
}

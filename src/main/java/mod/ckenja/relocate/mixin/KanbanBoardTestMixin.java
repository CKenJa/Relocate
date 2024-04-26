package mod.ckenja.relocate.mixin;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import mod.ckenja.relocate.Relocate;
import mod.ckenja.relocate.registry.RelocateItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static mod.ckenja.relocate.content.filter.KanbanBoardItem.getKanbanBoardID;

@Mixin(FilterItemStack.class)
public class KanbanBoardTestMixin {
    @Inject(method = "test(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Z)Z", at = @At("TAIL"),cancellable=true,remap=false)
    private static void relocate$test(Level world, ItemStack stack, boolean matchNBT, CallbackInfoReturnable<Boolean> cir){
        if (RelocateItems.KANBAN_BOARD.get() != stack.getItem()) {
            cir.setReturnValue(true);
        }
        //Relocate.RELOCATE_SAVED_DATA.getNode(getKanbanBoardID(filter)).removeCountedItemStack(stack);
    }
}

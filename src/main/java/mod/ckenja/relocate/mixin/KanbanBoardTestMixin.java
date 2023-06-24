package mod.ckenja.relocate.mixin;

import com.simibubi.create.content.logistics.filter.FilterItem;
import mod.ckenja.relocate.registry.RelocateItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FilterItem.class)
public class KanbanBoardTestMixin {
    @Inject(method = "test(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("TAIL"),cancellable=true,remap=false)
    private static void relocate$test(Level world, ItemStack stack, ItemStack filter, CallbackInfoReturnable<Boolean> cir){
        if (RelocateItems.KANBAN_BOARD.get() == filter.getItem()) {
            cir.setReturnValue(true);
        }
    }
}

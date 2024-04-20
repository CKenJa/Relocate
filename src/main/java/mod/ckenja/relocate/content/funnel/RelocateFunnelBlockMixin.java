package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FunnelBlockEntity.class)
public class RelocateFunnelBlockMixin {
    @Inject(method = "determineCurrentMode", at = @At("RETURN"),cancellable=true,remap=false)
    private static void determineCurrentModeMixin(CallbackInfoReturnable<Enum> cir){

    }
}

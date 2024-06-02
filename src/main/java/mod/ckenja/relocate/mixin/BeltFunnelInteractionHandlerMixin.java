package mod.ckenja.relocate.mixin;

import com.simibubi.create.content.kinetics.belt.transport.BeltFunnelInteractionHandler;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import mod.ckenja.relocate.content.funnel.RelocateBeltFunnelBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static mod.ckenja.relocate.content.funnel.RelocateBeltFunnelBlock.NEUTRAL;

@Mixin(BeltFunnelInteractionHandler.class)
public class BeltFunnelInteractionHandlerMixin{
    @Redirect(method = "checkForFunnels", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;",
            ordinal = 1))
    private static Comparable<?> redirectFunnelShapeCheck(BlockState funnelState, Property<?> property) {
        if (funnelState.getBlock() instanceof RelocateBeltFunnelBlock && funnelState.getValue(NEUTRAL))
            return BeltFunnelBlock.Shape.PULLING;
        return funnelState.getValue(BeltFunnelBlock.SHAPE);
    }
}

package mod.ckenja.relocate.mixin;

import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = FunnelBlockEntity.class,remap = false)
public interface FunnelBlockEntityAccessor {

    @Accessor(value = "invManipulation")
    InvManipulationBehaviour invManipulation();

    @Accessor(value = "invManipulation")
    void setInvManipulation(InvManipulationBehaviour invManipulation);
}

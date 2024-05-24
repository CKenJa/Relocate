package mod.ckenja.relocate.mixin;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = FunnelBlockEntity.class,remap = false)
public interface FunnelBlockEntityAccessor {

    @Accessor(value = "invManipulation")
    InvManipulationBehaviour invManipulation();

    @Accessor(value = "invManipulation")
    void setInvManipulation(InvManipulationBehaviour invManipulation);

    @Accessor(value = "filtering")
    FilteringBehaviour filtering();

    @Accessor(value = "filtering")
    void setFiltering(FilteringBehaviour filtering);

    @Accessor(value = "invVersionTracker")
    VersionedInventoryTrackerBehaviour invVersionTracker();

    @Accessor(value = "invVersionTracker")
    void setInvVersionTracker(VersionedInventoryTrackerBehaviour invVersionTracker);

    @Invoker
    boolean supportsAmountOnFilter();

    @Invoker
    ItemStack handleDirectBeltInput(TransportedItemStack stack, Direction side, boolean simulate);
}

package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.content.logistics.funnel.FunnelFilterSlotPositioning;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.utility.BlockFace;
import mod.ckenja.relocate.Relocate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RelocateFunnelBlockEntity extends FunnelBlockEntity {
    public RelocateFunnelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick(){
        super.tick();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        for (BlockEntityBehaviour behaviour : behaviours) {
            if (behaviour instanceof FilteringBehaviour fb) {
                fb.onlyActiveWhen(()->true);
            }
        }
    }

}

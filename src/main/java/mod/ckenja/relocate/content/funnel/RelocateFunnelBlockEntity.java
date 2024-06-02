package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.utility.BlockFace;
import mod.ckenja.relocate.mixin.FunnelBlockEntityAccessor;
import mod.ckenja.relocate.registry.RelocateBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RelocateFunnelBlockEntity extends FunnelBlockEntity {

    public RelocateFunnelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    private boolean supportsFiltering() {
        BlockState blockState = getBlockState();
        return RelocateBlocks.RELOCATE_BELT_FUNNEL.has(blockState) || RelocateBlocks.RELOCATE_FUNNEL.has(blockState);
    }

    public static void handleBlockEntityBehaviourEvent(BlockEntityBehaviourEvent<RelocateFunnelBlockEntity> event){
        //MixinでinvManipulationBehaviourを改変するより、継承したやつをMixinで入れたほうがいいかなって。そこんとこどうなの?
        event.remove(InvManipulationBehaviour.TYPE);
        FunnelBlockEntityAccessor access = (FunnelBlockEntityAccessor) event.getBlockEntity();
        access.setInvManipulation(new RelocateInvManipulationBehaviour(event.getBlockEntity(), (w, p, s) -> new BlockFace(p, AbstractFunnelBlock.getFunnelFacing(s)
                .getOpposite())));
        event.attach(access.invManipulation());

        //RelocateFunnelでFilterBehaviourが動くように
        BlockEntityBehaviour filtering = event.remove(FilteringBehaviour.TYPE);
        ((FilteringBehaviour)filtering).onlyActiveWhen(event.getBlockEntity()::supportsFiltering);
        event.attach(filtering);
    }
}

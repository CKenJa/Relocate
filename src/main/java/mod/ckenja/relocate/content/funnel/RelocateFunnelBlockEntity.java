package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.content.logistics.funnel.FunnelFilterSlotPositioning;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.utility.BlockFace;
import mod.ckenja.relocate.mixin.FunnelBlockEntityAccessor;
import mod.ckenja.relocate.registry.RelocateBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class RelocateFunnelBlockEntity extends FunnelBlockEntity {

    enum Mode {
        INVALID, PAUSED, COLLECT, PUSHING_TO_BELT, TAKING_FROM_BELT, EXTRACT
    }

    private final FunnelBlockEntityAccessor access = (FunnelBlockEntityAccessor) this;

    public RelocateFunnelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        //Insertを逆にする。
        access.setInvManipulation(new RelocateInvManipulationBehaviour(this, (w, p, s) -> new BlockFace(p, AbstractFunnelBlock.getFunnelFacing(s)
                .getOpposite())));
        behaviours.add(access.invManipulation());

        access.setInvVersionTracker(new VersionedInventoryTrackerBehaviour(this));
        behaviours.add(access.invVersionTracker());

        //RelocateFunnelでFilterBehaviourが動くように
        access.setFiltering(new FilteringBehaviour(this, new FunnelFilterSlotPositioning())
                .showCountWhen(access::supportsAmountOnFilter)
                .onlyActiveWhen(this::supportsFiltering)
                .withCallback($ -> access.invVersionTracker().reset())
        );
        behaviours.add(access.filtering());

        //BeltInsertBehaviourの搬入条件を変える。もうaddBehaviourをOverrideしちゃえよ
        behaviours.add(new DirectBeltInputBehaviour(this).onlyInsertWhen(this::supportsDirectBeltInput)
                .setInsertionHandler(access::handleDirectBeltInput));
    }

    private boolean supportsDirectBeltInput(Direction side) {
        BlockState blockState = getBlockState();
        if (blockState == null)
            return false;
        if (!(blockState.getBlock() instanceof RelocateFunnelBlock))
            return false;
        if (blockState.getValue(FunnelBlock.EXTRACTING) || blockState.getValue(RelocateFunnelBlock.NEUTRAL))
            return false;
        return FunnelBlock.getFunnelFacing(blockState) == Direction.UP;
    }

    private boolean supportsFiltering() {
        BlockState blockState = getBlockState();
        return RelocateBlocks.RELOCATE_BELT_FUNNEL.has(blockState) || RelocateBlocks.RELOCATE_FUNNEL.has(blockState);
    }

    public static void handleBlockEntityBehaviourEvent(BlockEntityBehaviourEvent<RelocateFunnelBlockEntity> event){
        //MixinでinvManipulationBehaviourを改変するより、継承したやつをMixinで入れたほうがいいかなって。そこんとこどうなの?
        /*event.remove(InvManipulationBehaviour.TYPE);
        FunnelBlockEntityAccessor access = (FunnelBlockEntityAccessor) event.getBlockEntity();
        access.setInvManipulation(new RelocateInvManipulationBehaviour(event.getBlockEntity(), (w, p, s) -> new BlockFace(p, AbstractFunnelBlock.getFunnelFacing(s)
                .getOpposite())));
        event.attach(access.invManipulation());

        //RelocateFunnelでFilterBehaviourが動くように
        BlockEntityBehaviour filtering = event.remove(FilteringBehaviour.TYPE);
        ((FilteringBehaviour)filtering).onlyActiveWhen(()->{
            BlockState blockState = event.getBlockState();
            return RelocateBlocks.RELOCATE_BELT_FUNNEL.has(blockState) || RelocateBlocks.RELOCATE_FUNNEL.has(blockState);
        });
        event.attach(filtering);*/
    }
}

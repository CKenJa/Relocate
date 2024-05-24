package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import mod.ckenja.relocate.registry.RelocateBlockEntityTypes;
import mod.ckenja.relocate.registry.RelocateBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RelocateFunnelBlock extends FunnelBlock {

    public static final BooleanProperty NEUTRAL = BooleanProperty.create("neutral");

    public RelocateFunnelBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(NEUTRAL, true));
    }

    public BlockState getEquivalentBeltFunnel(BlockGetter world, BlockPos pos, BlockState state) {
        Direction facing = getFacing(state);
        return RelocateBlocks.RELOCATE_BELT_FUNNEL.getDefaultState()
                .setValue(RelocateBeltFunnelBlock.HORIZONTAL_FACING, facing)
                .setValue(POWERED, state.getValue(POWERED));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(EXTRACTING, true);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(NEUTRAL));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) {
            boolean newNeutral = false;
            boolean newExtracting = true;
            if (state.getValue(NEUTRAL)) {
                newNeutral = false;
            }
            else if (state.getValue(EXTRACTING))
                newExtracting = false;
            else {
                //NEUTRALの時はEXTRACTING trueじゃないとFunnelBlockEntity.tickで搬出されない。
                newNeutral = true;
                newExtracting = true;
            }
            world.setBlockAndUpdate(context.getClickedPos(), state.setValue(NEUTRAL,newNeutral).setValue(EXTRACTING,newExtracting));
        }
        return InteractionResult.SUCCESS;
    }

    public BlockEntityType<? extends FunnelBlockEntity> getBlockEntityType() {
        return RelocateBlockEntityTypes.RELOCATE_FUNNEL.get();
    }

    protected boolean canInsertIntoFunnel(BlockState state) {
        return !state.getValue(POWERED) && (!state.getValue(EXTRACTING) || state.getValue(NEUTRAL));
    }
}

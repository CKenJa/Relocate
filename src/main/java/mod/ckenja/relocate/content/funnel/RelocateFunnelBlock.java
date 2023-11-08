package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import mod.ckenja.relocate.registry.RelocateBlockEntityTypes;
import mod.ckenja.relocate.registry.RelocateBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RelocateFunnelBlock extends FunnelBlock{

    public RelocateFunnelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getEquivalentBeltFunnel(BlockGetter world, BlockPos pos, BlockState state) {
        Direction facing = getFacing(state);
        return RelocateBlocks.RELOCATE_BELT_FUNNEL.getDefaultState()
                .setValue(BeltFunnelBlock.HORIZONTAL_FACING, facing)
                .setValue(POWERED, state.getValue(POWERED));
    }

    public BlockEntityType<? extends RelocateFunnelBlockEntity> getBlockEntityType() {
        return RelocateBlockEntityTypes.RELOCATE_FUNNEL.get();
    }
}

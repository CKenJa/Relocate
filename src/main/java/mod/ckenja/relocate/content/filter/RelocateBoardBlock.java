package mod.ckenja.relocate.content.filter;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import mod.ckenja.relocate.registry.RelocateBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;

public class RelocateBoardBlock extends FaceAttachedHorizontalDirectionalBlock
implements IBE<RelocateBoardEntity>, IWrenchable, ProperWaterloggedBlock {
    public RelocateBoardBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACE, FACING, WATERLOGGED));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState stateForPlacement = super.getStateForPlacement(pContext);
        if (stateForPlacement == null)
            return null;
        if (stateForPlacement.getValue(FACE) != AttachFace.WALL)
            stateForPlacement = stateForPlacement.setValue(FACING, stateForPlacement.getValue(FACING)
                    .getOpposite());
        return withWater(stateForPlacement, pContext);
    }
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return !pLevel.getBlockState(pPos.relative(getConnectedDirection(pState).getOpposite()))
                .getMaterial()
                .isReplaceable();
    }
    @Override
    public Class<RelocateBoardEntity> getBlockEntityClass() {
        return null;
    }

    @Override
    public BlockEntityType<? extends RelocateBoardEntity> getBlockEntityType() {
        return RelocateBlockEntityTypes.RELOCATE_BOARD.get();
    }
}

package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.simibubi.create.content.schematics.requirement.ISpecialBlockItemRequirement;
import com.tterrag.registrate.util.entry.BlockEntry;
import mod.ckenja.relocate.registry.RelocateBlockEntityTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;

public class RelocateBeltFunnelBlock extends BeltFunnelBlock implements ISpecialBlockItemRequirement {

    public static final BooleanProperty NEUTRAL = BooleanProperty.create("neutral");

    public RelocateBeltFunnelBlock(BlockEntry<? extends RelocateFunnelBlock> parent, Properties p_i48377_1_) {
        super(parent, p_i48377_1_);
        registerDefaultState(defaultBlockState().setValue(NEUTRAL, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_.add(NEUTRAL));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        if(state.getValue(SHAPE) == Shape.PULLING && state.getValue(NEUTRAL))
            return state.setValue(SHAPE, Shape.PUSHING);
        return state;
    }

    public BlockEntityType<? extends RelocateFunnelBlockEntity> getBlockEntityType() {
        return RelocateBlockEntityTypes.RELOCATE_FUNNEL.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide)
            return InteractionResult.SUCCESS;

        Shape shape = state.getValue(SHAPE);
        Shape newShape = shape;
        boolean newNeutral = false;
        boolean changeNeutral = false;
        if (state.getValue(NEUTRAL))
            changeNeutral = true;
        else if (shape == Shape.PUSHING)
            newShape = Shape.PULLING;
        else if (shape == Shape.PULLING) {
            //NEUTRALの時はEXTRACTING trueじゃないとFunnelBlockEntity.tickで搬出されない。
            newShape = Shape.PUSHING;
            newNeutral = true;
            changeNeutral = true;
        }

        else if (shape == Shape.EXTENDED)
            newShape = Shape.RETRACTED;
        else if (shape == Shape.RETRACTED) {
            BlockState belt = world.getBlockState(context.getClickedPos().below());
            if (belt.getBlock() instanceof BeltBlock && belt.getValue(BeltBlock.SLOPE) != BeltSlope.HORIZONTAL)
                newShape = Shape.RETRACTED;
            else
                newShape = Shape.EXTENDED;
        }

        if (newShape == shape && !changeNeutral)
            return InteractionResult.SUCCESS;
        world.setBlockAndUpdate(context.getClickedPos(), state.setValue(SHAPE, newShape).setValue(NEUTRAL, newNeutral));
        return InteractionResult.SUCCESS;
    }
}
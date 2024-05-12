package mod.ckenja.relocate.content.funnel;

import com.simibubi.create.content.schematics.requirement.ISpecialBlockItemRequirement;
import com.tterrag.registrate.util.entry.BlockEntry;
import mod.ckenja.relocate.registry.RelocateBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BeltFunnelBlock.class)
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
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() instanceof RelocateFunnelBlock fb && isOfSameType(fb))
            return;
        super.onRemove(state, world, pos, newState, isMoving);
    }

    public BlockEntityType<? extends RelocateFunnelBlockEntity> getBlockEntityType() {
        return RelocateBlockEntityTypes.RELOCATE_FUNNEL.get();
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return super.onWrenched(state, context);//TODO
    }
}